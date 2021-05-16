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

package com.simbachain.simba.platform.management;

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
import com.simbachain.simba.JsonData;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.SimbaClient;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.platform.AppConfig;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.PlatformTransaction;
import com.simbachain.simba.platform.Signing;
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
        this.client = createClient();
    }

    public OrganisationConfig getConfig() {
        return config;
    }

    public String getvPath() {
        return "v2/";
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public User whoami() throws SimbaException {
        return this.get(String.format("%suser/whoami/", getEndpoint()),
            jsonResponseHandler(User.class));
    }

    public PagedResult<Application> getApplications() throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/applications/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Application>>() {
            }));
    }

    public PagedResult<ContractDesign> getContractDesigns() throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/contract_designs/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<ContractDesign>>() {
            }));
    }

    public PagedResult<ContractArtifact> getContractArtifacts() throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/contract_artifacts/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<ContractArtifact>>() {
            }));
    }

    public PagedResult<DeployedContract> getDeployedContracts() throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/deployed_contracts/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<DeployedContract>>() {
            }));
    }

    public PagedResult<Blockchain> getBlockchains() throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/blockchains/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Blockchain>>() {
            }));
    }

    public PagedResult<Storage> getStorages() throws SimbaException {
        return this.get(String.format("%s%sorganisations/%s/storage/", getEndpoint(), getvPath(),
            getConfig().getOrganisationId()),
            jsonResponseHandler(new TypeReference<PagedResult<Storage>>() {
            }));
    }

    public Application getApplication(String applicationId) throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/applications/%s/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId(), applicationId),
            jsonResponseHandler(Application.class));
    }

    public ContractDesign getContractDesign(String designId) throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/contract_designs/%s/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId(), designId),
            jsonResponseHandler(ContractDesign.class));
    }

    public ContractArtifact getContractArtifact(String artifactId) throws SimbaException {
        return this.get(
            String.format("%s%sorganisations/%s/contract_artifacts/%s/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId(), artifactId),
            jsonResponseHandler(ContractArtifact.class));
    }

    public DeployedContract getDeployedContract(String id) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaPlatform.getDeployedContract: " + "id = [" + id + "]");
        }
        return this.get(
            String.format("%s%sorganisations/%s/deployed_contracts/%s", getEndpoint(), getvPath(),
                getConfig().getOrganisationId(), id), jsonResponseHandler(DeployedContract.class));
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
        return this.post(
            String.format("%s%sorganisations/%s/contract_designs/", getEndpoint(), getvPath(),
                getConfig().getOrganisationId()), data, jsonResponseHandler(ContractDesign.class));
    }

    public ContractArtifact createArtifact(String designId) throws SimbaException {
        JsonData data = JsonData.with("design_id", designId);
        return this.post(
            String.format("%s%sorganisations/%s/contract_artifacts/create/", getEndpoint(),
                getvPath(), getConfig().getOrganisationId()), data,
            jsonResponseHandler(ContractArtifact.class));
    }

    public Future<DeployedContract> deployContract(String artifactId,
        DeploymentSpec spec,
        Map<String, String> headers) throws SimbaException {
        JsonData data = spec.toJsonData();
        DeploymentResponse response = this.post(
            String.format("%s%sorganisations/%s/contract_artifacts/%s/deploy/", getEndpoint(),
                getvPath(), getConfig().getOrganisationId(), artifactId), data,
            jsonResponseHandler(DeploymentResponse.class), headers);
        if (this.wallet != null
            && response.getTransactionHash() == null
            && headers.get(ContractService.Headers.HTTP_HEADER_SENDER.getValue()) != null) {
            String txnId = response.getTransactionId();
            PlatformTransaction txn = this.get(
                String.format("%s%sorganisations/%s/transactions/%s", getEndpoint(), getvPath(),
                    getConfig().getOrganisationId(), txnId),
                jsonResponseHandler(new TypeReference<PlatformTransaction>() {
                }));
            Map<String, String> raw = txn.getRawTransaction();
            RawTransaction rawTransaction = Signing.createSigningTransaction(raw);
            String signedTransaction = this.wallet.sign(rawTransaction);
            String endpoint = String.format("%s%sapps/%s/transactions/%s/", getEndpoint(),
                getvPath(), spec.getAppName(), txnId);
            txn = this.post(endpoint, JsonData.with("transaction", signedTransaction),
                jsonResponseHandler(new TypeReference<PlatformTransaction>() {
                }));
            if (txn.getState()
                   .equals(Transaction.State.SUBMITTED)) {
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
            long end = now + (totalSeconds * 1000);
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
