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

package com.simbachain.auth.blocks;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.simbachain.SimbaException;
import com.simbachain.auth.AccessToken;
import com.simbachain.auth.AccessTokenProvider;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 */
public class BlocksTokenProvider extends AccessTokenProvider<BlocksConfig> {

    public BlocksTokenProvider(BlocksConfig config) {
        super(config);
    }

    private Map<String, String> post(CloseableHttpClient client,
        String endpoint,
        Map<String, String> data,
        Map<String, String> headers) throws Exception {

        HttpPost httpPost = new HttpPost(endpoint);

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params));
        if (headers != null) {
            for (String s : headers.keySet()) {
                httpPost.setHeader(s, headers.get(s));
            }
        }
        return client.execute(httpPost, jsonResponseHandler());
    }

    private ResponseHandler<Map<String, String>> jsonResponseHandler() {
        return response -> {
            int status = response.getStatusLine()
                                 .getStatusCode();
            if (status < 200 || status >= 300) {
                throw new SimbaException("Error getting auth token",
                    SimbaException.SimbaError.AUTHENTICATION_ERROR);
            }
            String responseString = "";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseString = EntityUtils.toString(entity);
            }
            TypeReference<Map<String, String>> tf = new TypeReference<Map<String, String>>() {
            };
            return mapper.readValue(responseString, tf);
        };
    }

    @Override
    public AccessToken getToken() throws SimbaException {
        CloseableHttpClient client = this.getConfig()
                                         .getClientFactory()
                                         .getClient();
        try {
            AccessToken token = this.getCachedToken(this.getConfig()
                                                        .getClientId());
            if (token != null) {
                return token;
            }

            Map<String, String> data = new HashMap<>();
            data.put("grant_type", "client_credentials");

            Map<String, String> headers = new HashMap<>();
            String userCredentials = getConfig().getClientId()
                + ":"
                + getConfig().getClientSecret();
            String basicAuth = "Basic " + new String(Base64.getEncoder()
                                                           .encode(userCredentials.getBytes()));
            headers.put("Authorization", basicAuth);
            headers.put("Content-Type", "application/x-www-form-urlencoded");

            Map<String, String> result = this.post(client, getConfig().getTokenUrl(), data, headers);
            
            long now = System.currentTimeMillis();
            long expires = now + (Long.parseLong(result.get("expires_in")) * 1000);
            token = new AccessToken(result.get("access_token"), result.get("token_type"),
                new Date(expires));
            cacheToken(getConfig().getClientId(), token);

            return token;
        } catch (Exception e) {
            throw new SimbaException(e.getMessage(), SimbaException.SimbaError.AUTHENTICATION_ERROR,
                e);
        }
    }
}
