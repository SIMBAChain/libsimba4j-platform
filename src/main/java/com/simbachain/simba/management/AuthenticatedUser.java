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

package com.simbachain.simba.management;

import java.util.HashMap;
import java.util.Map;

import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.auth.AuthConfig;
import com.simbachain.simba.Balance;
import com.simbachain.simba.SimbaClient;
import com.simbachain.simba.Urls;

/**
 *
 */
public class AuthenticatedUser extends SimbaClient {

    private final AuthConfig config;

    public AuthenticatedUser(String endpoint, AuthConfig config) {
        super(endpoint);
        this.config = config;
        this.client = createClient();
    }

    @Override
    protected Map<String, String> getApiHeaders() throws SimbaException {
        Map<String, String> apiHeaders = new HashMap<>();
        AccessToken token = this.config.getTokenProvider()
                                       .getToken();
        apiHeaders.put("Authorization", token.getType() + " " + token.getToken());
        return apiHeaders;
    }

    public User whoami() throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.WHOAMI),
            jsonResponseHandler(User.class));
    }

    public Balance getBalance(String blockchain) throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.BALANCE, blockchain),
            jsonResponseHandler(Balance.class));
    }

    public BlockchainIdentities getIdentities() throws SimbaException {
        return this.get(Urls.url(getEndpoint(), Urls.PathName.WALLET),
            jsonResponseHandler(BlockchainIdentities.class));
    }
}
