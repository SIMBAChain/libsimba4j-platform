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

package com.simbachain.simba.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.simbachain.auth.blocks.BlocksConfig;
import com.simbachain.simba.Balance;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.CallReturn;
import com.simbachain.simba.ContractService;
import com.simbachain.simba.HttpClientFactory;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.Manifest;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.SimbaClient;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.management.Application;
import com.simbachain.simba.management.AuthenticatedUser;
import com.simbachain.simba.management.Blockchain;
import com.simbachain.simba.management.BlockchainIdentities;
import com.simbachain.simba.management.CompilationSpec;
import com.simbachain.simba.management.ContractArtifact;
import com.simbachain.simba.management.ContractDesign;
import com.simbachain.simba.management.DeployedContract;
import com.simbachain.simba.management.DeploymentSpec;
import com.simbachain.simba.management.NewApplication;
import com.simbachain.simba.management.OrganisationConfig;
import com.simbachain.simba.management.OrganisationService;
import com.simbachain.simba.management.Storage;
import com.simbachain.wallet.Account;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestApi {

    public static class MockFactory extends HttpClientFactory {
        @Override
        protected CloseableHttpClient loadClient() {
            MockClient mockClient = new MockClient();
            return mockClient.getHttpClientMock();
        }
    }

    private static BlocksConfig config;
    private static String host;
    private static String org;
    
    @BeforeClass
    public static void setup() {
        String clientId = "foo";
        String clientSecret = "bar";
        String authHost = "https://localhost";
        config = new BlocksConfig(new MockFactory(), clientId, clientSecret, authHost);
        host = "https://localhost";
        org = "libsimba";
    }
    
    @Test
    public void testAuthenticatedUser() throws IOException, ExecutionException, InterruptedException {
        // whoami
        System.out.println("=================== whoami ===================");
        AuthenticatedUser user = new AuthenticatedUser(host, config);
        System.out.println("Authenticated user: " + user.whoami());
        Balance balance = user.getBalance("Quorum");
        System.out.println("balance: " + balance);
        // wallets
        System.out.println("=================== List Wallets ===================");
        BlockchainIdentities identities = user.getIdentities();
        System.out.println("wallet: " + identities.getWallet());
        List<BlockchainIdentities.Identity> idents = identities.getWallet()
                                                               .getIdentities("ethereum", "Quorum");
        for (BlockchainIdentities.Identity ident : idents) {
            System.out.println("balance for " + ident + " is " + user.getBalance("Quorum"));
        }
    }

    @Test
    public void testOrg() throws IOException {
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(host, orgConfig);


        System.out.println("=================== List Designs ===================");
        PagedResult<ContractDesign> cds = orgService.getContractDesigns();
        List<? extends ContractDesign> cdsResults = cds.getResults();
        for (ContractDesign result : cdsResults) {
            System.out.println(result.getName());
        }

        // get artifacts
        System.out.println("=================== List Artifacts ===================");
        PagedResult<ContractArtifact> cas = orgService.getContractArtifacts();
        List<? extends ContractArtifact> casResults = cas.getResults();
        for (ContractArtifact result : casResults) {
            System.out.println(result.getName());
        }
        System.out.println("=================== List Deployed Contracts ===================");
        PagedResult<DeployedContract> dcs = orgService.getDeployedContracts(10, 0);
        List<? extends DeployedContract> dcsResults = dcs.getResults();
        for (DeployedContract result : dcsResults) {
            System.out.println(result.getApiName());
        }

        System.out.println("=================== List Applications ===================");
        PagedResult<Application> apps = orgService.getApplications(Query.contains("name", "cif"));
        List<? extends Application> appsResults = apps.getResults();
        for (Application result : appsResults) {
            System.out.println(result.getName());
        }

        System.out.println("=================== List Blockchains ===================");
        PagedResult<Blockchain> bcs = orgService.getBlockchains();
        List<? extends Blockchain> bcsResults = bcs.getResults();
        for (Blockchain result : bcsResults) {
            System.out.println(result.getName());
        }

        System.out.println("=================== List Storage ===================");
        PagedResult<Storage> sts = orgService.getStorages();
        List<? extends Storage> stsResults = sts.getResults();
        for (Storage result : stsResults) {
            System.out.println(result.getName());
        }
    }
    
    @Test
    public void testContract() throws IOException, ExecutionException, InterruptedException {
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(host, orgConfig);
        // create app
        System.out.println("=================== Create Applications ===================");
        NewApplication newApp = orgService.createApplication("libsimba4J", "Libsimba4J");
        InputStream in = TestApi.class.getResourceAsStream("/supply.sol");

        System.out.println("=================== Compile Design ===================");
        String apiName = "supply";
        CompilationSpec compSpec = new CompilationSpec().withName(apiName);
        ContractDesign design = orgService.compileContract(in, compSpec);
        
        System.out.println("=================== Create Artifact ===================");
        ContractArtifact artifact = orgService.createArtifact(design.getId());

        DeploymentSpec spec = new DeploymentSpec()
            .withArtifactId(artifact.getId())
            .withApiName(apiName)
            .withBlockchain("Quorum")
            .withStorage("azure")
            .withAppName(newApp.getName());


        System.out.println("=================== Deploy Contract ===================");
        Future<DeployedContract> future = orgService.deployContract(spec);
        DeployedContract contract = future.get();
        System.out.println(contract);

        System.out.println("=================== Get Metadata ===================");
        ContractService contractService = orgService.newContractService(newApp.getName(), apiName);
        System.out.println("CompileDeployExample.main MD: " + contractService.getMetadata());


        System.out.println("=================== Submit ===================");
        JsonData supplyData = JsonData.with("price", 120)
                                      .and("dateTime", System.currentTimeMillis())
                                      .and("supplier", JsonData.with("__Supplier", "Supplier3.32"))
                                      .and("purchaser", JsonData.with("__Supplier", "Supplier2.11"))
                                      .and("part", JsonData.with("__Part", "Part542"));

        Map<String, String> headers = new HashMap<>();

        CallResponse ret = contractService.callMethod("supply", supplyData, headers);
        System.out.println("Got back response: " + ret);

        List<String> fields = new ArrayList<>();
        fields.add("method");
        fields.add("inputs");
        fields.add("state");

        System.out.println("=================== List Transactions ===================");
        System.out.println("CompileDeployExample.main MD: " + contractService.getMetadata());
        PagedResult<Transaction> txnResults = contractService.getTransactions("supply",
            Query.empty(), fields);
        List<? extends Transaction> txns = txnResults.getResults();
        for (Transaction transaction : txns) {
            System.out.printf("returned transaction: %s%n", transaction);
        }
        System.out.println("=================== Paging ===================");
        // Call next, if there are more results.
        while (txnResults.getNext() != null) {
            txnResults = contractService.next(txnResults);
            txns = txnResults.getResults();
            for (Transaction transaction : txns) {
                System.out.printf("next returned transaction: %s%n", transaction);
            }
        }
        System.out.println("=================== List Transactions ===================");
        txnResults = contractService.getTransactions("supply", Query.contains("inputs.part.__Part", "Part542"), fields);
        txns = txnResults.getResults();
        for (Transaction transaction : txns) {
            System.out.println(String.format("returned transaction: %s", transaction));
        }
        System.out.println("=================== Paging ===================");
        // Call next, if there are more results.
        while (txnResults.getNext() != null) {
            txnResults = contractService.next(txnResults);
            txns = txnResults.getResults();
            for (Transaction transaction : txns) {
                System.out.printf("next returned transaction: %s%n", transaction);
            }
        }

        System.out.println("=================== Submit with Bundle ===================");
        JsonData nonConformanceData = JsonData.with("dateTime", System.currentTimeMillis())
                                              .and("reason", "it's broken")
                                              .and("source", JsonData.with("__DataSource", "DataSource2.11"))
                                              .and("part", JsonData.with("__Part", "Part542"));

        headers = new HashMap<>();

        byte[] text = "Hello World".getBytes();
        SimbaClient.UploadFile uploadFile = new SimbaClient.UploadFile("Message", "text/plain", text);
        CallResponse bundleCall = contractService.callMethod("nonConformance", nonConformanceData,
            headers, uploadFile);
        System.out.println("Got back response: " + bundleCall);
        Future<Transaction> ftxn = contractService.waitForTransactionCompletion(bundleCall.getRequestIdentitier());
        Transaction txn = ftxn.get();
        System.out.println(txn);
        Map<String, Object> inputs = txn.getInputs();
        System.out.println(inputs);
        String bundleHash = (String) inputs.get("_bundleHash");
        System.out.println("Bundle Hash: " + bundleHash);
        File cwd = new File(System.getProperty("user.dir"));
        long now = System.currentTimeMillis();
        File dataDir = new File(cwd, "data-" + now);
        dataDir.mkdirs();
        File txtFile = new File(dataDir, "message.txt");
        BufferedOutputStream txtOut = new BufferedOutputStream(new FileOutputStream(txtFile));
        contractService.getBundleFileForTransaction(bundleHash, "Message", txtOut);
        InputStream downloaded = new FileInputStream(new File(dataDir, "message.txt"));
        long size = TestApi.compareBytes(downloaded, new ByteArrayInputStream(text));
        assertEquals(-1, size);
        System.out.println("common size: " + size);
        Manifest manifest = contractService.getBundleMetadataForTransaction(bundleHash);
        System.out.println("Manifest: " + manifest);


        new File(dataDir, "message.txt").delete();
        dataDir.delete();

        CallReturn<String> getterResponse = contractService.callGetter("getSupplier", String.class,
            JsonData.with("pk", "1234567890"));
        System.out.println("getter response: " + getterResponse.getReturnValue());
    }
    @Test
    public void testSelfSignedContract() throws IOException, ExecutionException, InterruptedException {
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(host, orgConfig);

        DeploymentSpec ssSpec = new DeploymentSpec()
            .withArtifactId("12345")
            .withApiName("supply-self-signed")
            .withBlockchain("Quorum")
            .withStorage("azure")
            .withAppName("libsimba4J");
        
        System.out.println("=================== Deploy Self Signed Contract ===================");
        Account acc = new Account("22aabb811efca4e6f4748bd18a46b502fa85549df9fa07da649c0a148d7d5530");
        orgService.setWallet(acc);
        Map<String, String> deployHeaders = new HashMap<>();
        deployHeaders.put(ContractService.Headers.HTTP_HEADER_SENDER.getValue(), acc.getAddress());

        Future<DeployedContract> ssFuture = orgService.deployContract(ssSpec, deployHeaders);
        DeployedContract ssContract = ssFuture.get();
        System.out.println("deployed contract address: " + ssContract.getAddress());

        ContractService ssContractService = orgService.newContractService("my-app", "supply-self-signed");


        JsonData ssSupplyData = JsonData.with("price", 120)
                                        .and("dateTime", System.currentTimeMillis())
                                        .and("supplier", JsonData.with("__Supplier", "Supplier3.33"))
                                        .and("purchaser", JsonData.with("__Supplier", "Supplier2.12"))
                                        .and("part", JsonData.with("__Part", "Part542"));

        Map<String, String> headers = new HashMap<>();
        headers.put(ContractService.Headers.HTTP_HEADER_SENDER.getValue(), acc.getAddress());

        CallResponse signedRet = ssContractService.callMethod("supply", ssSupplyData, headers);
        System.out.println("Got back response: " + signedRet);
        
    }

    @Test
    public void testHeaders() throws IOException, ExecutionException, InterruptedException {
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(host, orgConfig);

        DeploymentSpec ssSpec = new DeploymentSpec()
            .withArtifactId("12345")
            .withApiName("supply-headers")
            .withBlockchain("Quorum")
            .withStorage("azure")
            .withAppName("libsimba4J");

        System.out.println("=================== Deploy Contract ===================");
        Map<String, String> headers = new HashMap<>();
        headers.put(ContractService.Headers.HTTP_HEADER_DELEGATE.getValue(), MockClient.HEADERS_VALUE);

        Future<DeployedContract> ssFuture = orgService.deployContract(ssSpec, headers);
        DeployedContract ssContract = ssFuture.get();
        System.out.println("deployed contract address: " + ssContract.getAddress());

        ContractService ssContractService = orgService.newContractService("my-app", "supply-headers");


        JsonData ssSupplyData = JsonData.with("price", 120)
                                        .and("dateTime", System.currentTimeMillis())
                                        .and("supplier", JsonData.with("__Supplier", "Supplier3.33"))
                                        .and("purchaser", JsonData.with("__Supplier", "Supplier2.12"))
                                        .and("part", JsonData.with("__Part", "Part542"));


        headers = new HashMap<>();
        headers.put(ContractService.Headers.HTTP_HEADER_DELEGATE.getValue(), MockClient.HEADERS_VALUE);
        CallResponse ret = ssContractService.callMethod("supply", ssSupplyData, headers);
        System.out.println("Got back response: " + ret);
        assertEquals(ContractService.Headers.HTTP_HEADER_DELEGATE.getValue() + "=" + MockClient.HEADERS_VALUE, ret.getError());

        headers = new HashMap<>();
        headers.put(ContractService.Headers.HTTP_HEADER_VALUE.getValue(), MockClient.HEADERS_VALUE);
        ret = ssContractService.callMethod("supply", ssSupplyData, headers);
        System.out.println("Got back response: " + ret);
        assertEquals(ContractService.Headers.HTTP_HEADER_VALUE.getValue() + "=" + MockClient.HEADERS_VALUE, ret.getError());
        
        headers = new HashMap<>();
        headers.put(ContractService.Headers.HTTP_HEADER_NONCE.getValue(), MockClient.HEADERS_VALUE);
        ret = ssContractService.callMethod("supply", ssSupplyData, headers);
        System.out.println("Got back response: " + ret);
        assertEquals(ContractService.Headers.HTTP_HEADER_NONCE.getValue() + "=" + MockClient.HEADERS_VALUE, ret.getError());
    }
    
    public static long compareBytes(InputStream f1, InputStream f2) throws IOException {
        // return -1 if they match byte for byte 
        try (BufferedInputStream fis1 = new BufferedInputStream(f1);
            BufferedInputStream fis2 = new BufferedInputStream(f2)) {
            int ch;
            long pos = 1;
            while ((ch = fis1.read()) != -1) {
                if (ch != fis2.read()) {
                    return pos;
                }
                pos++;
            }
            if (fis2.read() == -1) {
                return -1;
            }
            else {
                return pos;
            }
        }
    }
}
