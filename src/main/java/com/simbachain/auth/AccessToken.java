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

/**
 * Represents an OAuth 2 access token.
 */
public class AccessToken {

    private String token;
    private String type;
    private long expiry;
    private boolean valid;

    public AccessToken(String token, String type, long expiry) {
        this.token = token;
        this.type = type;
        this.expiry = expiry;
    }

    public AccessToken() {
        this("", "", 0L);
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public long getExpiry() {
        return expiry;
    }

    public boolean isValid() {
        return valid;
    }

    public void refresh(String token, String type, long expiry) {
        this.token = token;
        this.type = type;
        this.expiry = expiry;
        this.valid = true;
    }

    public void invalidate() {
        this.valid = false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccessToken{");
        sb.append("token='")
          .append(token)
          .append('\'');
        sb.append(", type='")
          .append(type)
          .append('\'');
        sb.append(", expiry=")
          .append(expiry);
        sb.append(", valid=")
          .append(valid);
        sb.append('}');
        return sb.toString();
    }
}
