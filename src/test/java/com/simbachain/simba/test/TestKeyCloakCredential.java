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
import com.simbachain.auth.keycloak.KcAuthConfig;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.platform.AppConfig;
import com.simbachain.simba.platform.ContractService;
import com.simbachain.simba.platform.management.AuthenticatedUser;
import com.simbachain.simba.platform.management.BlockchainIdentities;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class TestKeyCloakCredential
{
    @Test
    public void testKeycloakCredentials() throws SimbaException {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String clientId = dotenv.get("KC_CLIENT_ID");
        String clientSecret = dotenv.get("KC_CLIENT_SECRET");
        String host = dotenv.get("KC_HOST");
        String authHost = dotenv.get("KC_AUTH_HOST");
        String realm = dotenv.get("KC_REALM");
        String org = dotenv.get("KC_ORG");
        String app = "MountainApp";
        String contract = "mountainapi";

        KcAuthConfig authConfig = new KcAuthConfig(clientId, clientSecret, authHost, realm,
                "email", "profile", "roles", "web-origins");
        AuthenticatedUser user = new AuthenticatedUser(host, authConfig);
        System.out.println("Authenticated user: " + user.whoami());
        AppConfig appConfig = new AppConfig(app, org, authConfig);
        ContractService simba = new ContractService(host, contract, appConfig);
        simba.init();
        System.out.println("Metadata: " + simba.getMetadata());
        System.out.println("Contract user: " + simba.whoami());
        BlockchainIdentities ids = simba.getIdentities();
        System.out.println("Wallet: " + ids.getWallet().getIdentities("ethereum", "ganache"));
        System.out.println("Num transactions: " + simba.getTransactionCount("ganache"));

        JsonData inputData = JsonData.with("elevationInMetres", 1230).and("__Mountain", "MtBaldy");
        CallResponse result = simba.callMethod("climb", inputData);
        System.out.println("Got back response: " + result);
    }
}
