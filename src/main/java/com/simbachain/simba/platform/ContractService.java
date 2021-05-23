/*
 * Copyright (c) 2021 SIMBA Chain Inc.
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

package com.simbachain.simba.platform;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.type.TypeReference;
import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.simba.Balance;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.Funds;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.Manifest;
import com.simbachain.simba.Metadata;
import com.simbachain.simba.Method;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Simba;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.platform.gen.Builder;
import com.simbachain.simba.platform.management.BlockchainIdentities;
import com.simbachain.simba.platform.management.TransactionCount;
import com.simbachain.simba.platform.management.User;
import com.simbachain.wallet.Wallet;
import org.web3j.crypto.RawTransaction;

/**
 * Enterprise Platform Implementation of SIMBA API as a Contract service
 */
public class ContractService extends Simba<AppConfig> implements FieldFiltered {
    
    public enum Headers {
        HTTP_HEADER_SENDER("txn-sender"),
        HTTP_HEADER_SENDER_TOKEN("txn-sender-token"),
        HTTP_HEADER_NONCE("txn-nonce"),
        HTTP_HEADER_DELEGATE("txn-delegate"),
        HTTP_HEADER_RUNLOCAL("txn-runlocal"),
        HTTP_HEADER_REQUEST_ID("x-request-id");
        
        private final String value;

        Headers(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    private Wallet wallet;

    /**                     
     * Constructor overrriden by subclasses.
     *
     * @param endpoint the URL of a particular contract API, e.g. https://api.simbachain.com/
     * @param contract the name of the contract or the appname, e.g. mycontract
     * @param config   used by subclasses.
     */
    public ContractService(String endpoint, String contract, AppConfig config) {
        super(endpoint, contract, config);
        this.setvPath("v2/");
    }

    /**
     * Constructor with Wallet.
     *
     * @param endpoint the URL of a particular contract API, e.g. https://api.simbachain.com/
     * @param contract the name of the contract or the appname, e.g. mycontract
     * @param config   used by subclasses.
     * @param wallet A wallet to use for client side signing.                 
     */
    public ContractService(String endpoint, String contract, AppConfig config, Wallet wallet) {
        this(endpoint, contract, config);
        this.wallet = wallet;
    }

    @Override
    protected Metadata loadMetadata() throws SimbaException {
        ContractInfo result = this.get(getApiPath(), jsonResponseHandler(new TypeReference<ContractInfo>() {
        }));
        return result;
    }
    
    public ContractInfo getContractInfo() {
        return (ContractInfo)getMetadata();
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getApiPath() {
        return String.format("%s%sapps/%s/contract/%s/info", getEndpoint(), getvPath(), getConfig().getAppName(), getContract());
    }

    public User whoami() throws SimbaException {
        return this.get(String.format("%suser/whoami/", getEndpoint()),
            jsonResponseHandler(User.class));
    }

    public BlockchainIdentities getIdentities() throws SimbaException {
        return this.get(String.format("%suser/wallet/", getEndpoint()),
            jsonResponseHandler(BlockchainIdentities.class));
    }

    public long getTransactionCount(String blockchain) throws SimbaException {
        TransactionCount count = this.get(String.format("%suser/transactions/%s/count/", getEndpoint(), blockchain),
            jsonResponseHandler(TransactionCount.class));
        return count.getCount();
    }

    public long getTransactionCount(String blockchain, String address) throws SimbaException {
        TransactionCount count = this.get(
            String.format("%suser/transactions/%s/count/%s/", getEndpoint(), blockchain, address),
            jsonResponseHandler(TransactionCount.class));
        return count.getCount();
    }

    @Override
    public Transaction getTransaction(String txnId) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getTransaction: " + "txnId = [" + txnId + "]");
        }
        PlatformTransaction txn = this.get(
            String.format("%s%sorganisations/%s/transactions/%s", getEndpoint(), getvPath(), getConfig().getOrganisationId(), txnId),
            jsonResponseHandler(new TypeReference<PlatformTransaction>() {
            }));
        String method = txn.getMethod();
        Method m = getMetadata().getMethod(method);
        if (m != null) {
            txn.setMethodParameters(m.getParameterMap());
        }
        return txn;
    }

    public <R> CallReturn<R> callGetter(String method, Class<R> cls) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.callGetter: " + "method = [" + method + "]");
        }

        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), method);
        ReturnObject<R> data = this.get(endpoint, jsonResponseHandler(new TypeReference<ReturnObject<R>>() {
        }));
        CallReturn<R> methodResponse = new CallReturn<>(data.getRequestId(), handleCast(data.getValue(), cls));
        methodResponse.setStatus(data.getState());
        methodResponse.setError(data.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.callGetter: returning " + methodResponse);
        }
        return methodResponse;
    }

    public <R> CallReturn<R> callGetter(String method, Class<R> cls, JsonData params)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.callGetter: " + "method = [" + method + "]");
        }

        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/?%s", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), method, asQueryParameters(params));
        ReturnObject<R> data = this.get(endpoint, jsonResponseHandler(new TypeReference<ReturnObject<R>>() {
        }));
        CallReturn<R> methodResponse = new CallReturn<>(data.getRequestId(), handleCast(data.getValue(), cls));
        methodResponse.setStatus(data.getState());
        methodResponse.setError(data.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.callGetter: returning " + methodResponse);
        }
        return methodResponse;
    }

    public <R> CallReturn<R> callGetter(String method, InstanceId id, Class<R> cls)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.callGetter: "
                + "method = ["
                + method
                + "]");
        }
        String idType = id.getType() == InstanceId.Type.ADDRESS ? "address" : "asset";

        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/%s/%s/", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), idType, id.getValue(), method);
        ReturnObject<R> data = this.get(endpoint, jsonResponseHandler(new TypeReference<ReturnObject<R>>() {
            }));
        CallReturn<R> methodResponse = new CallReturn<>(data.getRequestId(), handleCast(data.getValue(), cls));
        methodResponse.setStatus(data.getState());
        methodResponse.setError(data.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.callGetter: returning " + methodResponse);
        }
        return methodResponse;
    }
    
    @SuppressWarnings("unchecked")
    private <R> R handleCast(Object val, Class<R> cls) {
        if(!cls.isAssignableFrom(val.getClass())) {
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
        return (R) val;
    }

    public CallResponse callInstanceMethod(String method, InstanceId id, JsonData parameters,
        UploadFile... files)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            if(log.isDebugEnabled()) {
                log.debug("ENTER: SimbaPlatform.callInstanceMethod: "
                    + "method = ["
                    + method
                    + "], id = ["
                    + id
                    + "], parameters = ["
                    + parameters
                    + "]");
            }
        }
        validateParameters(getMetadata(), method, parameters, false);
        String idType = id.getType() == InstanceId.Type.ADDRESS ? "address" : "asset";

        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/%s/%s/", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), idType, id.getValue(), method);
        PlatformTransaction txn = this.post(endpoint, parameters, jsonResponseHandler(new TypeReference<PlatformTransaction>() {
            }), files);
        CallResponse methodResponse = new CallResponse(txn.getRequestId());
        methodResponse.setStatus(txn.getState()
                                    .toString());
        methodResponse.setError(txn.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.callInstanceMethod: returning " + methodResponse);
        }
        return methodResponse;
    }

    public NewInstanceResponse callNewInstance(JsonData parameters) throws SimbaException {
        if (log.isDebugEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug("ENTER: SimbaPlatform.callNewInstance: "
                    + "parameters = ["
                    + parameters
                    + "]");
            }
        }
        validateConstructorParameters(parameters);
        String endpoint = String.format("%s%sapps/%s/new/%s/", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract());
        NewInstanceResponse instanceResponse = this.post(endpoint, parameters, jsonResponseHandler(new TypeReference<NewInstanceResponse>() {
        }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.callNewInstance: returning " + instanceResponse);
        }
        return instanceResponse;
    }

    protected void validateConstructorParameters(JsonData parameters)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.validateParameters: "
                + "parameters = ["
                + parameters
                + "]");
        }
        if (getMetadata() == null) {
            throw new SimbaException("No metadata. You may need to call init() first.",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }
        if(parameters != null) {
            Map<String, Object> data = parameters.asMap();
            Map<String, Object> args = (Map<String, Object>) data.get("args");
            if(args != null) {
                if (getMetadata() instanceof ContractInfo) {
                    ContractInfo md = (ContractInfo) getMetadata();
                    ContractMethod construct = md.getContract()
                                                 .getConstructor();
                    if (construct != null) {
                        List<ContractParameter> params = construct.getParams();
                        if(params.size() != args.size()) {
                            throw new SimbaException(String.format(
                                "Unexpected number of constructor args provided. Expected %s but got %s",
                                params.size(), args.size()),
                                SimbaException.SimbaError.MESSAGE_ERROR);
                        }
                        for (ContractParameter param : params) {
                            if (args.get(param.getName()) == null) {
                                throw new SimbaException(
                                    String.format("constructor arg %s not provided", param.getName()),
                                    SimbaException.SimbaError.MESSAGE_ERROR);
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
    
    public CallResponse signAndSubmit(String transactionId, Map<String, String> raw) throws SimbaException {
        if(log.isDebugEnabled()) {
            log.debug("ENTER: ContractService.signAndSubmit: "
                + "transactionId = ["
                + transactionId + "], raw = ["
                + raw + "]");
        }

        RawTransaction rawTransaction = Signing.createSigningTransaction(raw);
        String signedTransaction = this.wallet.sign(rawTransaction);
        String endpoint = String.format("%s%sapps/%s/transactions/%s/", getEndpoint(), getvPath(),
            getConfig().getAppName(), transactionId);
        PlatformTransaction txn = this.post(endpoint, JsonData.with("transaction", signedTransaction),
            jsonResponseHandler(new TypeReference<PlatformTransaction>() {
        }));
        CallResponse methodResponse = new CallResponse(txn.getId());
        methodResponse.setStatus(txn.getState()
                                    .toString());
        methodResponse.setError(txn.getError());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.callMethod: returning " + methodResponse);
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
            log.debug("ENTER: SimbaPlatform.callMethod: "
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

        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), method);
        PlatformTransaction txn = this.post(endpoint, parameters, jsonResponseHandler(new TypeReference<PlatformTransaction>() {
            }), headers, files);
        if (txn.getState() == Transaction.State.PENDING && this.wallet != null) {
            Map<String, String> raw = txn.getRawTransaction();
            return signAndSubmit(txn.getId(), raw);
        } else {
            CallResponse methodResponse = new CallResponse(txn.getId());
            methodResponse.setStatus(txn.getState()
                                        .toString());
            methodResponse.setError(txn.getError());
            if (log.isDebugEnabled()) {
                log.debug("EXIT: SimbaPlatform.callMethod: returning " + methodResponse);
            }
            return methodResponse;
        }
    }

    @Override
    public Manifest getBundleMetadataForTransaction(String transactionIdOrHash)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getBundleMetadataForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "]");
        }
        Manifest m = this.get(
            String.format("%s%sapps/%s/contract/%s/bundle/%s/manifest", getEndpoint(), getvPath(),
                getConfig().getAppName(), getContract(), transactionIdOrHash), jsonResponseHandler(
                new TypeReference<Manifest>() {
                }));
        m.setHash(transactionIdOrHash);
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.getBundleMetadataForTransaction: returning " + m);
        }
        return m;
    }

    @Override
    public long getBundleForTransaction(String transactionIdOrHash, OutputStream outputStream)
        throws SimbaException {
        return getBundleForTransaction(transactionIdOrHash, outputStream, true);
        
    }

    @Override
    public long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream) throws SimbaException {
        return getBundleFileForTransaction(transactionIdOrHash, fileName, outputStream, true);
    }

    @Override
    public long getBundleForTransaction(String transactionIdOrHash,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getBundleForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "], outputStream = ["
                + outputStream
                + "], close = ["
                + close
                + "]");
        }
        String endpoint = String.format("%s%sapps/%s/contract/%s/bundle/%s/", getEndpoint(),
            getvPath(), getConfig().getAppName(), getContract(), transactionIdOrHash);
        return get(endpoint, streamResponseHandler(outputStream, close));
    }

    @Override
    public long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getBundleFileForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "], fileName = ["
                + fileName
                + "], outputStream = ["
                + outputStream
                + "], close = ["
                + close
                + "]");
        }
        String endpoint = String.format("%s%sapps/%s/contract/%s/bundle/%s/filename/%s/", getEndpoint(),
            getvPath(), getConfig().getAppName(), getContract(), transactionIdOrHash, fileName);
        return get(endpoint, streamResponseHandler(outputStream, close));
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedResult<Transaction> getTransactions() throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getTransactions: " + "");
        }
        PagedResult<? extends Transaction> result = this.get(
            String.format("%s%sorganisations/%s/transactions/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<PlatformTransaction>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.getTransactions: returning " + result);
        }
        return (PagedResult<Transaction>) result;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public PagedResult<Transaction> getTransactions(String method, Query.Params params)
        throws SimbaException {
            if (log.isDebugEnabled()) {
                log.debug("ENTER: SimbaPlatform.getTransactions: "
                    + "method = ["
                    + method
                    + "], params = ["
                    + params
                    + "]");
            }
            Method m = getMetadata().getMethod(method);
            validateQueryParameters(getMetadata(), method, params);
        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/%s", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), method, params.toJsonApiString());
        PagedResult<? extends Transaction> result = this.get(endpoint,
            jsonResponseHandler(new TypeReference<PagedResult<PlatformTransaction>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.getTransactions: returning " + result);
        }
        return (PagedResult<Transaction>) result;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public PagedResult<Transaction> next(PagedResult<Transaction> results) throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        PagedResult<? extends Transaction> result = this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<PlatformTransaction>>() {
            }));
        return (PagedResult<Transaction>) result;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public PagedResult<Transaction> previous(PagedResult<Transaction> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        PagedResult<? extends Transaction> result = this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<PlatformTransaction>>() {
            }));
        
        return (PagedResult<Transaction>) result;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public PagedResult<Transaction> getTransactions(String method,
        Query.Params params,
        List<String> fields) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getTransactions: "
                + "method = ["
                + method
                + "], params = ["
                + params
                + "], fields = [" 
                + fields + "]");
        }
        validateQueryParameters(getMetadata(), method, params);
        String endpoint = String.format("%s%sapps/%s/contract/%s/%s/%s", getEndpoint(), getvPath(),
            getConfig().getAppName(), getContract(), method, createQueryString(params, fields));
        PagedResult<? extends Transaction> result = this.get(endpoint,
            jsonResponseHandler(new TypeReference<PagedResult<PlatformTransaction>>() {
            }));
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaPlatform.getTransactions: returning " + result);
        }
        return (PagedResult<Transaction>) result;
    }

    @Override
    public Funds addFunds() throws SimbaException {
        return null;
    }

    @Override
    public Balance getBalance() throws SimbaException {
        return null;
    }
    
    public DeployedContractInstance getDeployedContractInstance(String id) throws SimbaException {
        if(log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getDeployedContractInstance: " + "id = [" + id + "]");
        }
        DeployedContractInstance txn = this.get(
            String.format("%s%sorganisations/%s/instances/%s", getEndpoint(), getvPath(),
                getConfig().getOrganisationId(), id),
            jsonResponseHandler(new TypeReference<DeployedContractInstance>() {
            }));
        return txn;
    }

    @Override
    protected Map<String, String> getApiHeaders() throws SimbaException {
        Map<String, String> apiHeaders = new HashMap<>();
        AccessToken token = getConfig().getAuthConfig().getTokenProvider()
                                       .getToken();
        apiHeaders.put("Authorization", token.getType() + " " + token.getToken());
        return apiHeaders;
    }
    
    protected String createQueryString(Query.Params params, List<String> fields) {
        String paramString = params.toJsonApiString();
        if (fields == null || fields.size() == 0) {
            return paramString;
        } else {
            StringBuilder sb = new StringBuilder("fields=");
            for (int i = 0; i < fields.size(); i++) {
                sb.append(fields.get(i));
                if(i < fields.size() - 1){
                    sb.append(",");
                }
            }
            if (paramString.length() > 0) {
                sb.insert(0, "&");
                sb.insert(0, paramString);
            } else{
                sb.insert(0, "?");
            }
            return sb.toString();
        }
    }

    private class DeployedContractCallable implements Callable<DeployedContractInstance> {

        private String id;
        private long poll;
        private int totalSeconds;

        private DeployedContractCallable(String id,
            long poll,
            int totalSeconds) {
            this.id = id;
            this.poll = poll;
            this.totalSeconds = totalSeconds;
        }

        @Override
        public DeployedContractInstance call() throws Exception {
            DeployedContractInstance txn = null;
            long now = System.currentTimeMillis();
            long end = now + (totalSeconds * 1000);
            while (now < end) {
                txn = getDeployedContractInstance(id);
                if (txn != null && txn.getContractAddress() != null) {
                    return txn;
                }
                Thread.sleep(poll);
                now = System.currentTimeMillis();
            }
            return txn;
        }
    }

    public Future<DeployedContractInstance> waitForContractInstanceDeployment(String id,
        long interval,
        int totalSeconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<DeployedContractInstance> txn = executor.submit(
            new DeployedContractCallable(id, interval, totalSeconds));
        executor.shutdown();
        return txn;

    }

    public Future<DeployedContractInstance> waitForContractInstanceDeployment(String id) {
        return waitForContractInstanceDeployment(id, 1000, 30);

    }

    public String generateContractPackage(String packageName, String outputDirectory)
        throws IOException {
        File f = new File(outputDirectory);
        if(!f.exists()) {
            boolean created = f.mkdirs();
            if (!created) {
                throw new IOException("Could not create output directory: " + outputDirectory);
            }
            if(!f.canWrite()) {
                throw new IOException("Cannot write to output directory: " + outputDirectory);
            }
        }
        Builder builder = new Builder(packageName, outputDirectory, this.getContractInfo());
        return builder.build();
    }

    private String asQueryParameters(JsonData parameters) throws SimbaException {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map = parameters.asMap();
        for (String s : map.keySet()) {
            Object o = map.get(s);
            if (o instanceof String || o instanceof Number || o instanceof Boolean) {
                if(sb.length() > 0){
                    sb.append("&");
                }
                sb.append(s).append("=").append(o);
            } else {
                throw new SimbaException("Only primitive types are supported as query parameters",
                    SimbaException.SimbaError.EXECUTION_ERROR);
            }
        }
        return sb.toString();
    }

}
