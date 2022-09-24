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

package com.simbachain.simba.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.simbachain.SimbaConfigFile;
import com.simbachain.auth.blocks.BlocksConfig;
import com.simbachain.simba.Balance;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.ContractService;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.management.Application;
import com.simbachain.simba.management.AuthenticatedUser;
import com.simbachain.simba.management.Blockchain;
import com.simbachain.simba.management.BlockchainIdentities;
import com.simbachain.simba.management.ContractArtifact;
import com.simbachain.simba.management.ContractDesign;
import com.simbachain.simba.management.DeployedContract;
import com.simbachain.simba.management.DeploymentSpec;
import com.simbachain.simba.management.NewApplication;
import com.simbachain.simba.management.OrganisationConfig;
import com.simbachain.simba.management.OrganisationService;
import com.simbachain.simba.management.Storage;

/**
 *
 */
public class CompileDeployExample {

    public static void main(String[] args)
        throws IOException, ExecutionException, InterruptedException {
        SimbaConfigFile conf = new SimbaConfigFile();

        String clientId = conf.getAuthClientId();
        String clientSecret = conf.getAuthClientSecret();
        String authHost = conf.getAuthBAseUrl();
        String host = conf.getApiBaseUrl();
        String org = "libsimba-1660833413";
        
        long now = System.currentTimeMillis();

        BlocksConfig config = new BlocksConfig(clientId, clientSecret, authHost);

        AuthenticatedUser user = new AuthenticatedUser(host, config);
        System.out.println("Authenticated user: " + user.whoami());
        Balance balance = user.getBalance("Quorum");
        System.out.println("balance: " + balance);
        BlockchainIdentities identities = user.getIdentities();
        System.out.println("wallet: " + identities.getWallet());
        
        
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(
            host, orgConfig);
        
        NewApplication newApp = orgService.createApplication("libsimba4J-" + now, "Libsimba4J-" + now);
        String appName = newApp.getName();
        System.out.println("new App name:" + appName);
        PagedResult<ContractDesign> cds = orgService.getContractDesigns();
        List<? extends ContractDesign> cdsResults = cds.getResults();
        for (ContractDesign result : cdsResults) {
            System.out.println(result.getName());
        }

        PagedResult<ContractArtifact> cas = orgService.getContractArtifacts();
        List<? extends ContractArtifact> casResults = cas.getResults();
        for (ContractArtifact result : casResults) {
            System.out.println(result.getName());
        }
        System.out.println("=================== List Deployed Contracts ===================");
        PagedResult<DeployedContract> dcs = orgService.getDeployedContracts(Query.ex("api_name", "supply_1658670508692"), 10, 0);
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

        PagedResult<Blockchain> bcs = orgService.getBlockchains();
        List<? extends Blockchain> bcsResults = bcs.getResults();
        for (Blockchain result : bcsResults) {
            System.out.println(result.getName());
        }

        PagedResult<Storage> sts = orgService.getStorages();
        List<? extends Storage> stsResults = sts.getResults();
        for (Storage result : stsResults) {
            System.out.println(result.getName());
        }

        InputStream in = CompileDeployExample.class.getResourceAsStream("/supply.sol");

        String apiName = "supply_" + System.currentTimeMillis();
        ContractDesign design = orgService.compileContract(in, apiName);
        System.out.println(design);
        ContractArtifact artifact = orgService.createArtifact(design.getId());

        DeploymentSpec spec = new DeploymentSpec();
        spec.setApiName(apiName);
        spec.setBlockchain("Quorum");
        spec.setStorage("azure");
        spec.setAppName(newApp.getName());


        Future<DeployedContract> future = orgService.deployContract(artifact.getId(), spec);
        DeployedContract contract = future.get();
        System.out.println(contract);

        ContractService contractService = orgService.newContractService(newApp.getName(), apiName);
        System.out.println("CompileDeployExample.main MD: " + contractService.getMetadata());


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
        

        System.out.println("CompileDeployExample.main MD: " + contractService.getMetadata());
        PagedResult<Transaction> txnResults = contractService.getTransactions("supply",
            Query.empty(), fields);
        List<? extends Transaction> txns = txnResults.getResults();
        for (Transaction transaction : txns) {
            System.out.printf("returned transaction: %s%n", transaction);
        }
        // Call next, if there are more results.
        while (txnResults.getNext() != null) {
            txnResults = contractService.next(txnResults);
            txns = txnResults.getResults();
            for (Transaction transaction : txns) {
                System.out.printf("next returned transaction: %s%n", transaction);
            }
        }

        txnResults = contractService.getTransactions("supply", Query.contains("inputs.part.__Part", "Part542"), fields);
        txns = txnResults.getResults();
        for (Transaction transaction : txns) {
            System.out.println(String.format("returned transaction: %s", transaction));
        }
        // Call next, if there are more results.
        while (txnResults.getNext() != null) {
            txnResults = contractService.next(txnResults);
            txns = txnResults.getResults();
            for (Transaction transaction : txns) {
                System.out.printf("next returned transaction: %s%n", transaction);
            }
        }
    }
}