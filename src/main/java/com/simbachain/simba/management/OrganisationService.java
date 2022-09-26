/*
 * Copyright (c) 2022 SIMBA Chain Inc.
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

package com.simbachain.simba.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.simba.AppConfig;
import com.simbachain.simba.ContractService;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Signing;
import com.simbachain.simba.SimbaClient;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.Urls;
import com.simbachain.wallet.Wallet;
import org.web3j.crypto.RawTransaction;

/**
 *
 */
public class OrganisationService extends SimbaClient {

    private final OrganisationConfig config;
    private Wallet wallet;

    public OrganisationService(String endpoint, OrganisationConfig config) {
        super(endpoint);
        this.config = config;
        this.client = config.getClientFactory().createClient();
    }

    public OrganisationConfig getConfig() {
        return config;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
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

    public NewApplication createApplication(String name, String displayName) throws SimbaException {
        JsonData data = JsonData.with("name", name)
                                .and("display_name", displayName);
        return this.post(
            Urls.url(getEndpoint(), Urls.PathName.APPLICATIONS, getConfig().getOrganisationId()),
            data, jsonResponseHandler(NewApplication.class));
    }

    public PagedResult<Application> getApplications(int limit, int offset) throws SimbaException {
        return this.get(
            Urls.url(getEndpoint(), Urls.PathName.APPLICATIONS, Urls.Paging.paging(offset, limit),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Application>>() {
            }));
    }

    public PagedResult<Application> getApplications(Query.Params params) throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.APPLICATIONS, params,
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Application>>() {
            }));
    }

    public PagedResult<Application> getApplications() throws SimbaException {
        return this.getApplications(10, 0);
    }

    public PagedResult<ContractDesign> getContractDesigns(int limit, int offset)
        throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.CONTRACT_DESIGNS,
                Urls.Paging.paging(offset, limit), getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<ContractDesign>>() {
            }));
    }

    public PagedResult<ContractDesign> getContractDesigns() throws SimbaException {
        return this.getContractDesigns(10, 0);
    }

    public PagedResult<ContractArtifact> getContractArtifacts(int limit, int offset)
        throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.CONTRACT_ARTIFACTS,
                Urls.Paging.paging(offset, limit), getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<ContractArtifact>>() {
            }));
    }

    public PagedResult<ContractArtifact> getContractArtifacts() throws SimbaException {
        return this.getContractArtifacts(10, 0);
    }

    public PagedResult<DeployedContract> getDeployedContracts(int limit, int offset)
        throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.DEPLOYED_CONTRACTS,
                Urls.Paging.paging(offset, limit), getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<DeployedContract>>() {
            }));
    }

    public PagedResult<DeployedContract> getDeployedContracts(Query.Params params,
        int limit,
        int offset) throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.DEPLOYED_CONTRACTS, params,
                Urls.Paging.paging(offset, limit), getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<DeployedContract>>() {
            }));
    }

    public PagedResult<DeployedContract> getDeployedContracts() throws SimbaException {
        return this.getDeployedContracts(10, 0);
    }

    public PagedResult<Blockchain> getBlockchains(int limit, int offset) throws SimbaException {
        return this.get(
            Urls.url(getEndpoint(), Urls.PathName.BLOCKCHAINS, Urls.Paging.paging(offset, limit),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Blockchain>>() {
            }));
    }

    public PagedResult<Blockchain> getBlockchains() throws SimbaException {
        return this.getBlockchains(10, 0);
    }

    public PagedResult<Storage> getStorages(int limit, int offset) throws SimbaException {
        return this.get(
            Urls.url(getEndpoint(), Urls.PathName.STORAGE, Urls.Paging.paging(offset, limit),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Storage>>() {
            }));
    }

    public PagedResult<Storage> getStorages() throws SimbaException {
        return this.getStorages(10, 0);
    }

    public <R> PagedResult<Application> previousApplications(PagedResult<Application> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        return this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<Application>>() {
            }));
    }

    public PagedResult<Application> nextApplications(PagedResult<Application> results)
        throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        return this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<Application>>() {
            }));
    }

    public <R> PagedResult<ContractDesign> previousContractDesigns(PagedResult<ContractDesign> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        return this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<ContractDesign>>() {
            }));
    }

    public PagedResult<ContractDesign> nextContractDesigns(PagedResult<ContractDesign> results)
        throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        return this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<ContractDesign>>() {
            }));
    }

    public PagedResult<ContractArtifact> previousContractArtifacts(PagedResult<ContractArtifact> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        return this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<ContractArtifact>>() {
            }));
    }

    public PagedResult<ContractArtifact> nextContractArtifacts(PagedResult<ContractArtifact> results)
        throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        return this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<ContractArtifact>>() {
            }));
    }

    public PagedResult<DeployedContract> previousDeployedContracts(PagedResult<DeployedContract> results)
        throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        return this.get(results.getPrevious(),
            jsonResponseHandler(new TypeReference<PagedResult<DeployedContract>>() {
            }));
    }

    public PagedResult<DeployedContract> nextDeployedContracts(PagedResult<DeployedContract> results)
        throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        return this.get(results.getNext(),
            jsonResponseHandler(new TypeReference<PagedResult<DeployedContract>>() {
            }));
    }

    public Application getApplication(String applicationId) throws SimbaException {
        return this.get(
            Urls.url(getEndpoint(), Urls.PathName.APPLICATIONS, getConfig().getOrganisationId(),
                applicationId), jsonResponseHandler(Application.class));
    }

    public ContractDesign getContractDesign(String designId) throws SimbaException {
        return this.get(
            Urls.url(getEndpoint(), Urls.PathName.CONTRACT_DESIGNS, getConfig().getOrganisationId(),
                designId), jsonResponseHandler(ContractDesign.class));
    }

    public ContractArtifact getContractArtifact(String artifactId) throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.CONTRACT_ARTIFACTS,
                getConfig().getOrganisationId(), artifactId),
            jsonResponseHandler(ContractArtifact.class));
    }

    public DeployedContract getDeployedContract(String id) throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.DEPLOYED_CONTRACTS,
            getConfig().getOrganisationId(), id), jsonResponseHandler(DeployedContract.class));
    }

    public ContractDesign compileContract(InputStream contract, String name) throws SimbaException {
        String contractCode = new BufferedReader(new InputStreamReader(contract)).lines()
                                                                                 .parallel()
                                                                                 .collect(
                                                                                     Collectors.joining(
                                                                                         "\n"));
        return compileContract(contractCode, name, "aat", true);
    }

    public ContractDesign compileContract(InputStream contract, String name, String model)
        throws SimbaException {
        String contractCode = new BufferedReader(new InputStreamReader(contract)).lines()
                                                                                 .parallel()
                                                                                 .collect(
                                                                                     Collectors.joining(
                                                                                         "\n"));
        return compileContract(contractCode, name, model, true);
    }

    public ContractDesign compileContract(String contractBase64Code, String name, String model)
        throws SimbaException {
        return compileContract(contractBase64Code, name, model, false);
    }

    public ContractDesign compileContract(String contractCode,
        String name,
        String model,
        boolean encodeCode) throws SimbaException {
        if (encodeCode) {
            contractCode = Base64.getEncoder()
                                 .encodeToString(contractCode.getBytes(StandardCharsets.UTF_8));
        }
        JsonData data = JsonData.with("code", contractCode)
                                .and("language", "solidity")
                                .and("name", name)
                                .and("model", model)
                                .and("mode", "code");
        return this.post(Urls.url(getEndpoint(), Urls.PathName.CONTRACT_DESIGNS,
            getConfig().getOrganisationId()), data, jsonResponseHandler(ContractDesign.class));
    }

    public ContractArtifact createArtifact(String designId) throws SimbaException {
        JsonData data = JsonData.with("design_id", designId);
        return this.post(Urls.url(getEndpoint(), Urls.PathName.CONTRACT_ARTIFACTS,
            getConfig().getOrganisationId()), data, jsonResponseHandler(ContractArtifact.class));
    }

    public Future<DeployedContract> deployContract(String artifactId,
        DeploymentSpec spec,
        Map<String, String> headers) throws SimbaException {
        JsonData data = spec.toJsonData();
        DeploymentResponse response = this.post(
            Urls.url(getEndpoint(), Urls.PathName.DEPLOY, getConfig().getOrganisationId(),
                artifactId), data, jsonResponseHandler(DeploymentResponse.class), headers);
        if (this.wallet != null
            && response.getTransactionHash() == null
            && headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue()) != null) {
            String txnId = response.getTransactionId();
            Transaction txn = this.get(
                Urls.url(getEndpoint(), Urls.PathName.APP_TXNS, spec.getAppName(), txnId),
                jsonResponseHandler(new TypeReference<Transaction>() {
                }));
            Map<String, String> raw = txn.getRawTransaction();
            RawTransaction rawTransaction = Signing.createSigningTransaction(raw);
            String signedTransaction = this.wallet.sign(rawTransaction);
            String endpoint = Urls.url(getEndpoint(), Urls.PathName.APP_TXNS, spec.getAppName(),
                txnId);
            txn = this.post(endpoint, JsonData.with("transaction", signedTransaction),
                jsonResponseHandler(new TypeReference<Transaction>() {
                }));
            if (txn.getState()
                   .equals(com.simbachain.simba.Transaction.State.SUBMITTED)) {
                response.setTransactionHash(txn.getTxnHash());
            }
        }
        return waitForContractDeployment(response.getInstanceId());
    }

    public Future<DeployedContract> deployContract(String artifactId, DeploymentSpec spec)
        throws SimbaException {
        Map<String, String> headers = new HashMap<>();
        if (this.wallet != null) {
            headers.put(ContractService.Headers.HTTP_HEADER_SENDER.getValue(),
                this.wallet.getAddress());
        }
        return deployContract(artifactId, spec, headers);
    }

    public ContractService newContractService(String appName, String contractName)
        throws SimbaException {
        ContractService service = new ContractService(getEndpoint(), contractName,
            new AppConfig(appName, contractName, config.getAuthConfig()));
        service.init();
        if (this.wallet != null) {
            service.setWallet(this.wallet);
        }
        return service;
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

    private class DeployedContractCallable implements Callable<DeployedContract> {

        private final String id;
        private final long poll;
        private final int totalSeconds;

        private DeployedContractCallable(String id, long poll, int totalSeconds) {
            this.id = id;
            this.poll = poll;
            this.totalSeconds = totalSeconds;
        }

        @Override
        public DeployedContract call() throws Exception {
            DeployedContract txn = null;
            long now = System.currentTimeMillis();
            long end = now + (totalSeconds * 1000L);
            while (now < end) {
                txn = getDeployedContract(id);
                if (txn != null && txn.getAddress() != null) {
                    return txn;
                }
                Thread.sleep(poll);
                now = System.currentTimeMillis();
            }
            return txn;
        }
    }

    public Future<DeployedContract> waitForContractDeployment(String id,
        long interval,
        int totalSeconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<DeployedContract> txn = executor.submit(
            new DeployedContractCallable(id, interval, totalSeconds));
        executor.shutdown();
        return txn;
    }

    public Future<DeployedContract> waitForContractDeployment(String id) {
        return waitForContractDeployment(id, 1000, 30);
    }
}