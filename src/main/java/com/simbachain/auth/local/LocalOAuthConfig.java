/*
 * Copyright (c) 2023 SIMBA Chain Inc.
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

package com.simbachain.auth.local;

import com.simbachain.auth.AccessTokenProvider;
import com.simbachain.auth.AuthConfig;
import com.simbachain.simba.HttpClientFactory;

/**
 *
 */
public class LocalOAuthConfig extends AuthConfig {

    private final String user;
    private final String tokenUrl;

    public LocalOAuthConfig(HttpClientFactory clientFactory,
        String clientId,
        String clientSecret,
        String user,
        String tokenUrl) {
        super(clientFactory, clientId, clientSecret);
        this.user = user;
        this.tokenUrl = tokenUrl;
    }

    public LocalOAuthConfig(String clientId,
        String clientSecret,
        String user,
        String tokenUrl) {
        super(clientId, clientSecret);
        this.user = user;
        this.tokenUrl = tokenUrl;
    }

    public String getUser() {
        return user;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    @Override
    public AccessTokenProvider<LocalOAuthConfig> getTokenProvider() {
        return new LocalTokenProvider(this);
    }
}
