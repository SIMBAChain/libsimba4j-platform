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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.simbachain.auth.azure.AzConfig;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.management.Application;
import com.simbachain.simba.platform.management.Blockchain;
import com.simbachain.simba.platform.management.ContractArtifact;
import com.simbachain.simba.platform.management.ContractDesign;
import com.simbachain.simba.platform.management.DeployedContract;
import com.simbachain.simba.platform.management.DeploymentSpec;
import com.simbachain.simba.platform.management.OrganisationConfig;
import com.simbachain.simba.platform.management.OrganisationService;
import com.simbachain.simba.platform.management.Storage;
import io.github.cdimascio.dotenv.Dotenv;

/**
 *
 */
public class CompileDeployExample {

    public static void main(String[] args)
        throws IOException, ExecutionException, InterruptedException {
        Dotenv dotenv = Dotenv.load();
        
        String tenant_id = dotenv.get("TENANT_ID");
        String client_id = dotenv.get("CLIENT_ID");
        String client_secret = dotenv.get("CLIENT_SECRET");
        String appId = dotenv.get("APP_ID");
        String host = dotenv.get("HOST");
        String org = dotenv.get("ORG");

        System.out.println("========= CREDENTIAL LOGIN ===========");
        AzConfig config = new AzConfig(client_id, client_secret, tenant_id, appId,
            AzConfig.Flow.CLIENT_CREDENTIAL);
        OrganisationConfig orgConfig = new OrganisationConfig(org, config);
        OrganisationService orgService = new OrganisationService(
            host, orgConfig);
        
        InputStream in = CompileDeployExample.class.getResourceAsStream("/supply.sol");

        String apiName = "supply_" + System.currentTimeMillis();
        ContractDesign design = orgService.compileContract(in, apiName, "sasa");
        System.out.println(design);
        ContractArtifact artifact = orgService.createArtifact(design.getId());

        PagedResult<Application> apps = orgService.getApplications(20, 0);
        System.out.println("Available Applications:");
        List<? extends Application> results = apps.getResults();
        for (Application result : results) {
            System.out.println(result.getName());
        }

        DeploymentSpec spec = new DeploymentSpec();
        spec.setApiName(apiName);
        spec.setBlockchain("Quorum");
        spec.setStorage("azure");
        spec.setAppName("neo-supplychain");

        Future<DeployedContract> future = orgService.deployContract(artifact.getId(), spec);
        DeployedContract contract = future.get();
        System.out.println(contract);

        PagedResult<ContractDesign> cds = orgService.getContractDesigns();
        List<? extends ContractDesign> cdsResults = cds.getResults();
        System.out.println("Contract Designs:");
        for (ContractDesign result : cdsResults) {
            System.out.println(result.getName());
        }
        while (cds.getNext() != null) {
            cds = orgService.nextContractDesigns(cds);
            cdsResults = cds.getResults();
            for (ContractDesign cd : cdsResults) {
                System.out.println(cd.getName());
            }
        }

        PagedResult<ContractArtifact> cas = orgService.getContractArtifacts();
        List<? extends ContractArtifact> casResults = cas.getResults();
        System.out.println("Contract Artifacts:");
        for (ContractArtifact result : casResults) {
            System.out.println(result.getName());
        }
        while (cas.getNext() != null) {
            cas = orgService.nextContractArtifacts(cas);
            casResults = cas.getResults();
            for (ContractDesign cd : casResults) {
                System.out.println(cd.getName());
            }
        }

        PagedResult<DeployedContract> dcs = orgService.getDeployedContracts();
        List<? extends DeployedContract> dcsResults = dcs.getResults();
        System.out.println("Deployed Contracts:");
        for (DeployedContract result : dcsResults) {
            System.out.println(result.getApiName());
        }
        while (dcs.getNext() != null) {
            dcs = orgService.nextDeployedContracts(dcs);
            dcsResults = dcs.getResults();
            for (DeployedContract dc : dcsResults) {
                System.out.println(dc.getApiName());
            }
        }

        PagedResult<Blockchain> bcs = orgService.getBlockchains();
        List<? extends Blockchain> bcsResults = bcs.getResults();
        System.out.println("Available Blockchains:");
        for (Blockchain result : bcsResults) {
            System.out.println(result.getName());
        }

        PagedResult<Storage> sts = orgService.getStorages();
        List<? extends Storage> stsResults = sts.getResults();
        System.out.println("Available Storage:");
        for (Storage result : stsResults) {
            System.out.println(result.getName());
        }

        ContractService contractService = orgService.newContractService("neo-supplychain", apiName);
        String path = contractService.generateContractPackage("com.supplychain", "./");
        System.out.println(path);


        JsonData supplyData = JsonData.with("price", 120)
                                      .and("dateTime", System.currentTimeMillis())
                                      .and("supplier", JsonData.with("__Supplier", "Supplier3.32"))
                                      .and("purchaser", JsonData.with("__Supplier", "Supplier2.11"))
                                      .and("part", JsonData.with("__Part", "Part542"));
        
        CallResponse ret = contractService.callMethod("supply", supplyData);
        System.out.println("Got back response: " + ret);

    }

}
