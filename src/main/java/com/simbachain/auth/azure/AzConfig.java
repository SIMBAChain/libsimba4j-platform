/*
 * Copyright (c) 2020 SIMBA Chain Inc.
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

import com.simbachain.auth.AccessTokenProvider;
import com.simbachain.auth.OAuthConfig;

/**
 * Azure config that uses OAuth.
 */
public class AzConfig extends OAuthConfig {
    
    private String tennantId;
    private String server = "https://login.microsoftonline.com/";

    public AzConfig(String clientId, String clientSecret, String tennantId, String server) {
        super(clientId, clientSecret);
        this.tennantId = tennantId;
        this.server = server;
    }

    public AzConfig(String clientId, String clientSecret, String tennantId) {
        super(clientId, clientSecret);
        this.tennantId = tennantId;
    }

    public String getTennantId() {
        return tennantId;
    }

    public String getServer() {
        return server;
    }

    @Override
    public AccessTokenProvider getTokenProvider() {
        return new AzAccessTokenProvider(this);
    }
}
