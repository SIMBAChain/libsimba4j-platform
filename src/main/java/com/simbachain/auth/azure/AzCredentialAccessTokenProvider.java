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

package com.simbachain.auth.azure;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.auth.AccessTokenProvider;

/**
 * Implementation of AccessTokenProvider that talks to Azure.
 */
public class AzCredentialAccessTokenProvider implements AccessTokenProvider {

    private final AzConfig credentials;
    private AccessToken token = null;
    private long expires = 0L;

    public AzCredentialAccessTokenProvider(AzConfig credentials) {
        this.credentials = credentials;
    }

    public AccessToken getToken() throws SimbaException {

        String server = credentials.getServer();
        if (!server.endsWith("/")) {
            server += "/";
        }

        try {
            long now = System.currentTimeMillis();
            if (now >= this.expires) {
                this.token = null;
            }
            if (this.token == null) {
                ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                    credentials.getClientId(),
                    ClientCredentialFactory.create(credentials.getClientSecret()))
                                                                                 .authority(
                                                                                     String.format(
                                                                                         "%s%s",
                                                                                         server,
                                                                                         credentials.getTenantId()))
                                                                                 .build();

                String scope = String.format("%s/.default", credentials.getAppId());
                ClientCredentialParameters clientCredentialParam
                    = ClientCredentialParameters.builder(Collections.singleton(scope))
                                                .build();
                CompletableFuture<IAuthenticationResult> future = app.acquireToken(
                    clientCredentialParam);
                IAuthenticationResult result = future.get();
                long expiry = result.expiresOnDate()
                                    .getTime();
                this.expires = expiry - 5000;
                this.token = new AccessToken(result.accessToken(), "Bearer", expiry);
            }
            return this.token;

        } catch (Exception e) {
            throw new SimbaException(e.getMessage(), SimbaException.SimbaError.AUTHENTICATION_ERROR,
                e);
        }
    }

}
