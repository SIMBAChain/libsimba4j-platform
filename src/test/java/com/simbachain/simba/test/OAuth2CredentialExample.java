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
import com.simbachain.auth.blocks.BlocksConfig;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.management.OrganisationConfig;
import com.simbachain.simba.platform.management.OrganisationService;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Obtain a set of client ID and Secrets from Blocks
 */
public class OAuth2CredentialExample {

    public static void main(String[] args) throws SimbaException {
        Dotenv dotenv = Dotenv.load();

        String client_id = dotenv.get("CLIENT_ID");
        String client_secret = dotenv.get("CLIENT_SECRET");
        String tokenurl = dotenv.get("TOKEN_URL");
        String host = dotenv.get("HOST");
        String org = dotenv.get("ORG");

        System.out.println("========= USER/PASSWORD LOGIN ===========");
        BlocksConfig authConfig = new BlocksConfig(client_id, client_secret, tokenurl);
        OrganisationService orgService = new OrganisationService(
            host, new OrganisationConfig(org, authConfig));
        System.out.println(orgService.whoami());
        ContractService simba1 = orgService.newContractService("myapi", "car");
        System.out.println(simba1.getMetadata());
    }
}
