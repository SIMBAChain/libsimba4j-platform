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

import com.simbachain.SimbaException;
import com.simbachain.auth.azure.AzConfig;
import com.simbachain.simba.platform.AppConfig;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.management.OrganisationConfig;
import com.simbachain.simba.platform.management.OrganisationService;
import io.github.cdimascio.dotenv.Dotenv;

/**
 *
 */
public class AzCredentialExample {

    public static void main(String[] args) throws SimbaException {
        Dotenv dotenv = Dotenv.load();
        
        String tenant_id = dotenv.get("TENANT_ID");
        String client_id = dotenv.get("CLIENT_ID");
        String client_secret = dotenv.get("CLIENT_SECRET");
        String appId = dotenv.get("APP_ID");
        String host = dotenv.get("HOST");
        String org = dotenv.get("ORG");
        String contract = "supplychain_1614267039";

        System.out.println("========= CREDENTIAL LOGIN ===========");
        AzConfig authConfig = new AzConfig(client_id, client_secret, tenant_id, appId, AzConfig.Flow.CLIENT_CREDENTIAL, true);
        AppConfig appConfig = new AppConfig("neo-supplychain", org, authConfig);
        ContractService simba = new ContractService(host, contract, appConfig);
        simba.init();
        System.out.println(simba.getMetadata());
        System.out.println(simba.whoami());


        System.out.println("========= USER/PASSWORD LOGIN ===========");
        AzConfig userConfig = new AzConfig(dotenv.get("USER_EMAIL"), dotenv.get("USER_PASSWORD"),
            tenant_id, appId, AzConfig.Flow.USER_PASSWORD, false);
        OrganisationService orgService = new OrganisationService(
            host, new OrganisationConfig(org, userConfig));
        System.out.println(orgService.whoami());
        ContractService simba1 = orgService.newContractService("test", "game-1155");
        System.out.println(simba1.getMetadata());
    }
}
