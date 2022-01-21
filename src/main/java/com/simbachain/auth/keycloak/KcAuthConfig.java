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

package com.simbachain.auth.keycloak;

import com.simbachain.auth.AccessTokenProvider;
import com.simbachain.auth.AuthConfig;

/**
 *
 */
public class KcAuthConfig extends AuthConfig {

    private final String host;
    private final String realm;
    private final String scopes;

    public KcAuthConfig(String clientId,
        String clientSecret,
        String host,
        String realm,
        String... scopes) {
        super(clientId, clientSecret);
        while (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        this.host = host;
        this.realm = realm;
        this.scopes = String.join(" ", scopes);
    }

    public String getHost() {
        return host;
    }

    public String getRealm() {
        return realm;
    }

    public String getScopes() {
        return scopes;
    }
    
    public String getTokenUrl() {
        return String.format("%s/auth/realms/%s/protocol/openid-connect/token", getHost(), getRealm());
    }

    @Override
    public AccessTokenProvider getTokenProvider() {
        return new KcTokenProvider(this);
    }
}
