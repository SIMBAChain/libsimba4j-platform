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

package com.simbachain.auth.blocks;

import com.simbachain.auth.AccessTokenProvider;
import com.simbachain.auth.AuthConfig;

/**
 *
 */
public class BlocksConfig extends AuthConfig {

    private final String tokenUrl;

    public BlocksConfig(String clientId, String clientSecret, String authHost, boolean writeToFile, String tokenDir) {
        super(clientId, clientSecret, writeToFile, tokenDir);
        if (!authHost.endsWith("/")) {
            authHost = authHost + "/";
        }
        this.tokenUrl = String.format("%s/o/token/", authHost);
    }
    
    public BlocksConfig(String clientId, String clientSecret, String authHost) {
        this(clientId, clientSecret, authHost, false, null);
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    @Override
    public AccessTokenProvider<BlocksConfig> getTokenProvider() {
        return new BlocksTokenProvider(this);
    }
}
