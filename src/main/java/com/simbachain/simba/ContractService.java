/*
 * Copyright (c) 2023 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.simbachain.simba;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.simba.gen.Builder;
import com.simbachain.simba.management.BlockchainIdentities;
import com.simbachain.simba.management.TransactionCount;
import com.simbachain.simba.management.User;
import com.simbachain.wallet.Wallet;
import org.web3j.crypto.RawTransaction;

/**
 * Blocks Platform Implementation of SIMBA API as a Contract service
 */
public class ContractService extends Simba<AppConfig> implements FieldFiltered {

    public enum Headers {
        HTTP_HEADER_SENDER("txn-sender"), HTTP_HEADER_SENDER_TOKEN("txn-sender-token"),
        HTTP_HEADER_NONCE("txn-nonce"), HTTP_HEADER_DELEGATE("txn-delegate"),
        HTTP_HEADER_VALUE("txn-value"), HTTP_HEADER_RUNLOCAL("txn-force-run-local"),
        HTTP_HEADER_REQUEST_ID("x-request-id");

        private final String value;

        Headers(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    private Map<String, Wallet> wallets = new HashMap<>();

    /**
     * Constructor overrriden by subclasses.
     *
     * @param endpoint the URL of a particular contract API, e.g. https://api.simbachain.com/
     * @param contract the name of the contract or the appname, e.g. mycontract
     * @param config   used by subclasses.
     */
    public ContractService(String endpoint, String contract, AppConfig config) {
        super(endpoint, contract, config);
    }

    /**
     * Constructor with Wallet.
     *
     * @param endpoint the URL of a particular contract API, e.g. https://api.simbachain.com/
     * @param contract the name of the contract or the appname, e.g. mycontract
     * @param config   used by subclasses.
     * @param wallets  A map of wallets to use for client side signing keyed to their address.
     */
    public ContractService(String endpoint,
        String contract,
        AppConfig config,
        Map<String, Wallet> wallets) {
        this(endpoint, contract, config);
        this.wallets = wallets;
    }

    @Override
    protected com.simbachain.simba.Metadata loadMetadata() throws SimbaException {
        return this.get(getApiPath(), jsonResponseHandler(new TypeReference<Metadata>() {
        }));
    }

    public Metadata getContractInfo() {
        return getMetadata();
    }

    public Wallet getWallet(String address) {
        return this.wallets.get(address);
    }

    public void setWallet(Wallet wallet) throws SimbaException {
        this.wallets.put(wallet.getAddress(), wallet);
    }

    public String getApiPath() {
        return Urls.url(getEndpoint(), Urls.PathName.CONTRACT_API, getConfig().getAppName(),
            getContract());
    }

    public User whoami() throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.WHOAMI),
            jsonResponseHandler(User.class));
    }

    public BlockchainIdentities getIdentities() throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.WALLET),
            jsonResponseHandler(BlockchainIdentities.class));
    }

    public long getTransactionCount(String blockchain) throws SimbaException {
        TransactionCount count = this.get(
            Urls.url(getEndpoint(), Urls.PathName.TXN_COUNT, blockchain),
            jsonResponseHandler(TransactionCount.class));
        return count.getCount();
    }

    public long getTransactionCount(String blockchain, String address) throws SimbaException {
        TransactionCount count = this.get(
            Urls.url(getEndpoint(), Urls.PathName.ADDRESS_TXN_COUNT, blockchain, address),
            jsonResponseHandler(TransactionCount.class));
        return count.getCount();
    }

    @Override
    public com.simbachain.simba.Transaction getTransaction(String txnId) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getTransaction: " + "txnId = [" + txnId + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.ORG_TXN,
            getConfig().getOrganisationId(), txnId);
        Transaction txn = this.get(endpoint, jsonResponseHandler(new TypeReference<Transaction>() {
        }));
        String method = txn.getMethod();
        com.simbachain.simba.Method m = getMetadata().getMethod(method);
        if (m != null) {
            txn.setMethodParameters(m.getParameterMap());
        }
        return txn;
    }

    public <R> CallReturn<R> callGetter(String method, Class<R> cls) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.callGetter: " + "method = [" + method + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_METHOD,
            getConfig().getAppName(), getContract(), method);
        ReturnObject<R> data = this.get(endpoint,
            jsonResponseHandler(new TypeReference<ReturnObject<R>>() {
            }));
        CallReturn<R> methodResponse = new CallReturn<>(data.getRequestId(),
            handleCast(data.getValue(), cls));
        methodResponse.setStatus(data.getState());
        methodResponse.setError(data.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.callGetter: returning " + methodResponse);
        }
        return methodResponse;
    }

    public <R> CallReturn<R> callGetter(String method, Class<R> cls, JsonData params)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.callGetter: " + "method = [" + method + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_METHOD,
            asQueryParameters(params), getConfig().getAppName(), getContract(), method);
        ReturnObject<R> data = this.get(endpoint,
            jsonResponseHandler(new TypeReference<ReturnObject<R>>() {
            }));
        CallReturn<R> methodResponse = new CallReturn<>(data.getRequestId(),
            handleCast(data.getValue(), cls));
        methodResponse.setStatus(data.getState());
        methodResponse.setError(data.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.callGetter: returning " + methodResponse);
        }
        return methodResponse;
    }

    @SuppressWarnings ("unchecked")
    private <R> R handleCast(Object val, Class<R> cls) {
        if (val != null) {
            if (!cls.isAssignableFrom(val.getClass())) {
                if (BigInteger.class.isAssignableFrom(cls)) {
                    return (R) new BigInteger(val.toString());
                }
                if (Long.class.isAssignableFrom(cls)) {
                    return (R) new Long(val.toString());
                }
                if (Integer.class.isAssignableFrom(cls)) {
                    return (R) new Integer(val.toString());
                }
                if (Short.class.isAssignableFrom(cls)) {
                    return (R) new Short(val.toString());
                }
            }
        }
        return (R) val;
    }

    @SuppressWarnings ("unchecked")
    protected void validateConstructorParameters(JsonData parameters) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.validateParameters: " + "parameters = [" + parameters + "]");
        }
        if (getMetadata() == null) {
            throw new SimbaException("No metadata. You may need to call init() first.",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }
        if (parameters != null) {
            Map<String, Object> data = parameters.asMap();
            if (data.get("args") != null && data.get("args") instanceof Map) {
                Map<String, Object> args = (Map<String, Object>) data.get("args");
                if (args != null) {
                    Metadata md = getMetadata();
                    Method construct = md.getContract()
                                         .getConstructor();
                    if (construct != null) {
                        List<Parameter> params = construct.getParams();
                        if (params.size() != args.size()) {
                            throw new SimbaException(String.format(
                                "Unexpected number of constructor args provided. Expected %s but got %s",
                                params.size(), args.size()),
                                SimbaException.SimbaError.MESSAGE_ERROR);
                        }
                        for (Parameter param : params) {
                            if (args.get(param.getName()) == null) {
                                throw new SimbaException(
                                    String.format("constructor arg %s not provided",
                                        param.getName()), SimbaException.SimbaError.MESSAGE_ERROR);
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public CallResponse callMethod(String method, JsonData parameters, UploadFile... files)
        throws SimbaException {
        return this.callMethod(method, parameters, new HashMap<>(), files);
    }

    @Override
    public CallResponse callMethodSync(String method, JsonData parameters, UploadFile... files)
        throws SimbaException {
        return this.callMethodSync(method, parameters, new HashMap<>(), files);
    }

    public CallResponse signAndSubmit(String transactionId, Map<String, Object> raw, Wallet wallet)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.signAndSubmit: "
                + "transactionId = ["
                + transactionId
                + "], raw = ["
                + raw
                + "]");
        }

        RawTransaction rawTransaction = Signing.createSigningTransaction(raw);
        Object chainId = raw.get("chainId");
        String signedTransaction;
        if (chainId instanceof Long) {
            signedTransaction = wallet.sign(rawTransaction, (Long) chainId);
        } else {
            signedTransaction = wallet.sign(rawTransaction);
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.APP_TXN, getConfig().getAppName(),
            transactionId);
        Transaction txn = this.post(endpoint, JsonData.with("transaction", signedTransaction),
            jsonResponseHandler(new TypeReference<Transaction>() {
            }));
        CallResponse methodResponse = new CallResponse(txn.getId());
        methodResponse.setStatus(txn.getState()
                                    .toString());
        methodResponse.setError(txn.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.callMethod: returning " + methodResponse);
        }
        return methodResponse;
    }

    @Override
    public CallResponse callMethod(String method,
        JsonData parameters,
        Map<String, String> headers,
        UploadFile... files) throws SimbaException {
        if (log.isDebugEnabled()) {
            Object f = files.length == 0 ? "" : Arrays.asList(files);
            log.debug("ENTER: ContractService.callMethod: "
                + "method = ["
                + method
                + "], parameters = ["
                + parameters
                + "], headers = ["
                + headers
                + "], files = ["
                + f
                + "]");
        }
        validateParameters(getMetadata(), method, parameters, files.length > 0);

        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_METHOD,
            getConfig().getAppName(), getContract(), method);
        Transaction txn = this.post(endpoint, parameters,
            jsonResponseHandler(new TypeReference<Transaction>() {
            }), headers, files);
        if (txn.getState() == com.simbachain.simba.Transaction.State.PENDING
            && headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue()) != null) {
            Wallet wallet = this.wallets.get(
                headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue()));
            if (wallet == null) {
                throw new SimbaException(String.format("No wallet matching %s",
                    headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue())),
                    SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            Map<String, Object> raw = txn.getRawTransaction();
            return signAndSubmit(txn.getId(), raw, wallet);
        } else {
            CallResponse methodResponse = new CallResponse(txn.getId());
            methodResponse.setStatus(txn.getState()
                                        .toString());
            methodResponse.setError(txn.getError());
            if (log.isDebugEnabled()) {
                log.debug("EXIT: ContractService.callMethod: returning " + methodResponse);
            }
            return methodResponse;
        }
    }

    @Override
    public CallResponse callMethodSync(String method,
        JsonData parameters,
        Map<String, String> headers,
        UploadFile... files) throws SimbaException {
        if (log.isDebugEnabled()) {
            Object f = files.length == 0 ? "" : Arrays.asList(files);
            log.debug("ENTER: ContractService.callMethodSync: "
                + "method = ["
                + method
                + "], parameters = ["
                + parameters
                + "], headers = ["
                + headers
                + "], files = ["
                + f
                + "]");
        }
        validateParameters(getMetadata(), method, parameters, files.length > 0);

        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_METHOD_SYNC,
            getConfig().getAppName(), getContract(), method);
        Transaction txn = this.post(endpoint, parameters,
            jsonResponseHandler(new TypeReference<Transaction>() {
            }), headers, files);
        if (txn.getState() == com.simbachain.simba.Transaction.State.SUBMITTED
            && headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue()) != null) {
            Wallet wallet = this.wallets.get(
                headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue()));
            if (wallet == null) {
                throw new SimbaException(String.format("No wallet matching %s",
                    headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue())),
                    SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            Map<String, Object> raw = txn.getRawTransaction();
            return signAndSubmit(txn.getId(), raw, wallet);
        } else {
            CallResponse methodResponse = new CallResponse(txn.getId());
            methodResponse.setStatus(txn.getState()
                                        .toString());
            methodResponse.setError(txn.getError());
            if (log.isDebugEnabled()) {
                log.debug("EXIT: ContractService.callMethodSync: returning " + methodResponse);
            }
            return methodResponse;
        }
    }

    @Override
    public Manifest getBundleMetadataForTransaction(String bundleHash) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getBundleMetadataForTransaction: "
                + "bundleHash = ["
                + bundleHash
                + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.BUNDLE_MANIFEST,
            getConfig().getAppName(), getContract(), bundleHash);
        Manifest m = this.get(endpoint, jsonResponseHandler(new TypeReference<Manifest>() {
        }));
        m.setHash(bundleHash);
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.getBundleMetadataForTransaction: returning " + m);
        }
        return m;
    }

    @Override
    public long getBundleForTransaction(String bundleHash, OutputStream outputStream)
        throws SimbaException {
        return getBundleForTransaction(bundleHash, outputStream, true);

    }

    @Override
    public long getBundleFileForTransaction(String bundleHash,
        String fileName,
        OutputStream outputStream) throws SimbaException {
        return getBundleFileForTransaction(bundleHash, fileName, outputStream, true);
    }

    @Override
    public long getBundleForTransaction(String bundleHash, OutputStream outputStream, boolean close)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getBundleForTransaction: "
                + "bundleHash = ["
                + bundleHash
                + "], outputStream = ["
                + outputStream
                + "], close = ["
                + close
                + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.BUNDLE, getConfig().getAppName(),
            getContract(), bundleHash);
        return get(endpoint, streamResponseHandler(outputStream, close));
    }

    @Override
    public long getBundleFileForTransaction(String bundleHash,
        String fileName,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getBundleFileForTransaction: "
                + "bundleHash = ["
                + bundleHash
                + "], fileName = ["
                + fileName
                + "], outputStream = ["
                + outputStream
                + "], close = ["
                + close
                + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.BUNDLE_FILE,
            getConfig().getAppName(), getContract(), bundleHash, fileName);
        return get(endpoint, streamResponseHandler(outputStream, close));
    }
    
    public PagedResult<TransactionEvent> getTransactionEvents(String eventName, Query.Params params)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getTransactionEvents: "
                + "eventName = ["
                + eventName
                + "], params = ["
                + params
                + "]");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_EVENTS, params,
            getConfig().getAppName(), getContract(), eventName);
        PagedResult<TransactionEvent> result = this.get(endpoint,
            jsonResponseHandler(new TypeReference<PagedResult<TransactionEvent>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.getTransactionEvents: returning " + result);
        }
        return result;
    }
    
    public PagedResult<TransactionEvent> nextEvents(PagedResult<com.simbachain.simba.Transaction> results)
        throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        PagedResult<TransactionEvent> result = this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<TransactionEvent>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.nextEvents: returning " + result);
        }
        return result;
    }
    
    public PagedResult<TransactionEvent> previousEvents(PagedResult<com.simbachain.simba.Transaction> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        PagedResult<TransactionEvent> result = this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<TransactionEvent>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.previousEvents: returning " + result);
        }
        return result;
    }

    @Override
    public PagedResult<Transaction> getTransactions() throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getTransactions: " + "");
        }
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.APP_TXNS, getConfig().getAppName());
        PagedResult<Transaction> result = this.get(endpoint,
            jsonResponseHandler(new TypeReference<PagedResult<Transaction>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.getTransactions: returning " + result);
        }
        return result;
    }

    @Override
    public PagedResult<Transaction> getTransactions(String method, Query.Params params)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getTransactions: "
                + "method = ["
                + method
                + "], params = ["
                + params
                + "]");
        }
        com.simbachain.simba.Method m = getMetadata().getMethod(method);
        validateQueryParameters(getMetadata(), method, params);
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_METHOD, params,
            getConfig().getAppName(), getContract(), method);
        PagedResult<Transaction> result = this.get(endpoint,
            jsonResponseHandler(new TypeReference<PagedResult<Transaction>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.getTransactions: returning " + result);
        }
        return result;
    }

    @Override
    public PagedResult<Transaction> next(PagedResult<com.simbachain.simba.Transaction> results)
        throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        PagedResult<Transaction> result = this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<Transaction>>() {
            }));
        return result;
    }

    @Override
    public PagedResult<Transaction> previous(PagedResult<com.simbachain.simba.Transaction> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        PagedResult<Transaction> result = this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<Transaction>>() {
            }));

        return result;
    }

    @Override
    public PagedResult<Transaction> getTransactions(String method,
        Query.Params params,
        List<String> fields) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.getTransactions: "
                + "method = ["
                + method
                + "], params = ["
                + params
                + "], fields = ["
                + fields
                + "]");
        }
        validateQueryParameters(getMetadata(), method, params);
        String endpoint = Urls.url(getEndpoint(), Urls.PathName.CONTRACT_METHOD,
            createQueryString(params, fields), getConfig().getAppName(), getContract(), method);
        PagedResult<Transaction> result = this.get(endpoint,
            jsonResponseHandler(new TypeReference<PagedResult<Transaction>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: ContractService.getTransactions: returning " + result);
        }
        return result;
    }

    @Override
    protected Map<String, String> getApiHeaders() throws SimbaException {
        Map<String, String> apiHeaders = new HashMap<>();
        AccessToken token = getConfig().getAuthConfig()
                                       .getTokenProvider()
                                       .getToken();
        apiHeaders.put("Authorization", token.getType() + " " + token.getToken());
        return apiHeaders;
    }

    protected Query.Params createQueryString(Query.Params params, List<String> fields) {
        if (fields != null && fields.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fields.size(); i++) {
                sb.append(fields.get(i));
                if (i < fields.size() - 1) {
                    sb.append(",");
                }
            }
            params = params.str("fields", sb.toString());
        }
        return params;
    }

    public String generateContractPackage(String packageName, String outputDirectory)
        throws IOException {
        File f = new File(outputDirectory);
        if (!f.exists()) {
            boolean created = f.mkdirs();
            if (!created) {
                throw new IOException("Could not create output directory: " + outputDirectory);
            }
            if (!f.canWrite()) {
                throw new IOException("Cannot write to output directory: " + outputDirectory);
            }
        }
        Builder builder = new Builder(packageName, outputDirectory, this.getContractInfo());
        return builder.build();
    }

    private Query.Params asQueryParameters(JsonData parameters) throws SimbaException {
        Query.Params params = Query.empty();
        Map<String, Object> map = parameters.asMap();
        for (String s : map.keySet()) {
            Object o = map.get(s);
            if (o instanceof String) {
                params = params.str(s, (String) o);
            } else if (o instanceof Number) {
                params = params.num(s, (Number) o);
            } else if (o instanceof Boolean) {
                params = params.bool(s, (Boolean) o);
            } else {
                throw new SimbaException("Only primitive types are supported as query parameters",
                    SimbaException.SimbaError.EXECUTION_ERROR);
            }
        }
        return params;
    }

}
