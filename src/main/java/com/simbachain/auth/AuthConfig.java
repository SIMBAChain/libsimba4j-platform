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

package com.simbachain.auth;

import com.simbachain.simba.SimbaConfig;

/**
 * OAuth focussed simba config.
 */
public abstract class AuthConfig extends SimbaConfig {

    private final String clientId;
    private final String clientSecret;
    private final boolean writeToFile;
    private final String tokenDir;

    public AuthConfig(String clientId, String clientSecret, boolean writeToFile, String tokenDir) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.writeToFile = writeToFile;
        this.tokenDir = tokenDir;
    }

    public AuthConfig(String clientId, String clientSecret) {
        this(clientId, clientSecret, false, null);
    }

    public AuthConfig getAuthConfig() {
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public boolean isWriteToFile() {
        return writeToFile;
    }

    public String getTokenDir() {
        return tokenDir;
    }

    public abstract AccessTokenProvider<? extends AuthConfig> getTokenProvider();
}
