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

import com.simbachain.SimbaConfigFile;
import com.simbachain.SimbaException;
import com.simbachain.auth.blocks.BlocksConfig;
import com.simbachain.simba.AppConfig;
import com.simbachain.simba.ContractService;
import com.simbachain.simba.management.AuthenticatedUser;
import com.simbachain.simba.management.BlockchainIdentities;

/**
 *
 */
public class BlocksCredentialExample
{
    public static void main(String[] args) throws SimbaException {
        SimbaConfigFile config = new SimbaConfigFile();

        String clientId = config.getAuthClientId();
        String clientSecret = config.getAuthClientSecret();
        String app = config.get("APP_ID");
        String authHost = config.getAuthBAseUrl();
        String host = config.getApiBaseUrl();
        String org = config.get("ORG");
        String contract = "cif";

        BlocksConfig authConfig = new BlocksConfig(clientId, clientSecret, authHost);
        AuthenticatedUser user = new AuthenticatedUser(host, authConfig);
        System.out.println("Authenticated user: " + user.whoami());
        AppConfig appConfig = new AppConfig(app, org, authConfig);
        ContractService simba = new ContractService(host, contract, appConfig);
        simba.init();
        System.out.println("Metadata: " + simba.getMetadata());
        System.out.println("Contract user: " + simba.whoami());
        BlockchainIdentities ids = simba.getIdentities();
        System.out.println("IDs: " + ids);
    }
}
