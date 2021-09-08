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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.simbachain.auth.azure.AzConfig;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.management.Application;
import com.simbachain.simba.platform.management.ContractArtifact;
import com.simbachain.simba.platform.management.ContractDesign;
import com.simbachain.simba.platform.management.DeployedContract;
import com.simbachain.simba.platform.management.DeploymentSpec;
import com.simbachain.simba.platform.management.OrganisationConfig;
import com.simbachain.simba.platform.management.OrganisationService;
import com.simbachain.wallet.FileWallet;
import com.simbachain.wallet.Wallet;
import io.github.cdimascio.dotenv.Dotenv;

/**
 *
 */
public class ClientCompileDeployExample {

    public static void main(String[] args)
        throws IOException, ExecutionException, InterruptedException {
        Dotenv dotenv = Dotenv.load();

        String tenant_id = dotenv.get("TENANT_ID");
        String client_id = dotenv.get("CLIENT_ID");
        String client_secret = dotenv.get("CLIENT_SECRET");
        String appId = dotenv.get("APP_ID");
        String host = dotenv.get("HOST");
        String org = dotenv.get("ORG");
        String clientAddress = dotenv.get("PUBLIC_ADDRESS");
        
        System.out.println("========= CREDENTIAL LOGIN ===========");
        AzConfig config = new AzConfig(client_id, client_secret, tenant_id, appId,
            AzConfig.Flow.CLIENT_CREDENTIAL);
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(
            host, orgConfig);
        InputStream in = ClientCompileDeployExample.class.getResourceAsStream("/supply.sol");

        String apiName = "supply_" + System.currentTimeMillis();
        ContractDesign design = orgService.compileContract(in, apiName, "sasa");
        System.out.println(design);
        ContractArtifact artifact = orgService.createArtifact(design.getId());

        PagedResult<Application> apps = orgService.getApplications();
        List<? extends Application> results = apps.getResults();
        for (Application result : results) {
            System.out.println(result.getName());
        }

        DeploymentSpec spec = new DeploymentSpec();
        spec.setApiName(apiName);
        spec.setBlockchain("Quorum");
        spec.setStorage("azure");
        spec.setAppName("neo-supplychain");

        Wallet wallet = new FileWallet("./wallets", "pk-test");
        wallet.loadOrCreatePrivateKeyWallet("s3cr3t",
            dotenv.get("PRIVATE_KEY"));
        orgService.setWallet(wallet);

        Future<DeployedContract> future = orgService.deployContract(artifact.getId(), spec);
        DeployedContract contract = future.get();
        System.out.println(contract);

        ContractService contractService = orgService.newContractService("neo-supplychain", apiName);


        JsonData supplyData = JsonData.with("price", 120)
                                      .and("dateTime", System.currentTimeMillis())
                                      .and("supplier", JsonData.with("__Supplier", "Supplier3.32"))
                                      .and("purchaser", JsonData.with("__Supplier", "Supplier2.11"))
                                      .and("part", JsonData.with("__Part", "Part542"));

        Map<String, String> headers = new HashMap<>();
        headers.put(ContractService.Headers.HTTP_HEADER_SENDER.getValue(), clientAddress);
        long nonce = contractService.getTransactionCount("Quorum", clientAddress);
        headers.put(ContractService.Headers.HTTP_HEADER_NONCE.getValue(), Long.toString(nonce + 1));
        
        CallResponse ret = contractService.callMethod("supply", supplyData, headers);
        System.out.println("Got back response: " + ret);
    }
}
