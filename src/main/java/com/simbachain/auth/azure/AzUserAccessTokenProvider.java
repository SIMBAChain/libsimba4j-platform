/*
 * Copyright (c) 2022 SIMBA Chain Inc.
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

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.auth.AccessTokenProvider;
import com.simbachain.auth.FileAccessToken;

/**
 * Implementation of AccessTokenProvider that talks to Azure.
 */
public class AzUserAccessTokenProvider implements AccessTokenProvider {

    private final AzConfig credentials;
    private final AccessToken token;

    public AzUserAccessTokenProvider(AzConfig credentials) {
        this.credentials = credentials;
        if (this.credentials.isWriteToFile()) {
            this.token = new FileAccessToken(String.format(".%s", this.credentials.getClientId()));
        } else {
            this.token = new AccessToken();
        }
    }

    public AccessToken getToken() throws SimbaException {

        String server = credentials.getServer();
        if (!server.endsWith("/")) {
            server += "/";
        }

        try {
            long now = System.currentTimeMillis();
            if (now >= this.token.getExpiry()) {
                this.token.invalidate();
            }
            if (!this.token.isValid()) {
                PublicClientApplication pca = PublicClientApplication.builder(
                    credentials.getAppId())
                                                                     .authority(
                                                                         String.format("%s%s",
                                                                             server,
                                                                             credentials.getTenantId()))
                                                                     .build();

                String scope = String.format("api://%s/scaas.access", credentials.getAppId());
                UserNamePasswordParameters parameters = UserNamePasswordParameters.builder(
                    Collections.singleton(scope), credentials.getClientId(),
                    credentials.getClientSecret()
                               .toCharArray())
                                                                                  .build();
                CompletableFuture<IAuthenticationResult> future = pca.acquireToken(parameters);
                IAuthenticationResult result = future.get();
                long expiry = result.expiresOnDate()
                                    .getTime();
                this.token.refresh(result.accessToken(), "Bearer", expiry - 5000);
            }
            return this.token;

        } catch (Exception e) {
            throw new SimbaException(e.getMessage(), SimbaException.SimbaError.AUTHENTICATION_ERROR,
                e);
        }
    }

}
