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

import com.simbachain.auth.AccessTokenProvider;
import com.simbachain.auth.AuthConfig;

/**
 * Azure config that uses OAuth.
 */
public class AzConfig extends AuthConfig {

    public enum Flow {
        USER_PASSWORD, CLIENT_CREDENTIAL
    }
    
    private static String DEFAULT_SERVER = "https://login.microsoftonline.com"; 

    private final String tenantId;
    private final String appId;
    private final Flow flow;
    private final String server;

    public AzConfig(String clientId,
        String clientSecret,
        String tenantId,
        String appId,
        Flow flow,
        String server,
        boolean writeToFile) {
        super(clientId, clientSecret, writeToFile);
        this.tenantId = tenantId;
        this.appId = appId;
        this.flow = flow;
        this.server = server;
    }

    public AzConfig(String clientId,
        String clientSecret,
        String tenantId,
        String appId,
        Flow flow, boolean writeToFile) {
        this(clientId, clientSecret, tenantId, appId, flow, DEFAULT_SERVER, writeToFile);
    }

    public AzConfig(String clientId,
        String clientSecret,
        String tenantId,
        String appId,
        Flow flow) {
        this(clientId, clientSecret, tenantId, appId, flow, DEFAULT_SERVER, false);
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getServer() {
        return server;
    }

    public String getAppId() {
        return appId;
    }

    @Override
    public AccessTokenProvider getTokenProvider() {
        if (flow == Flow.CLIENT_CREDENTIAL) {
            return new AzCredentialAccessTokenProvider(this);
        } else {
            return new AzUserAccessTokenProvider(this);
        }
    }
}
