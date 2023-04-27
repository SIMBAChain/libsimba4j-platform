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

package com.simbachain.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbachain.SimbaException;

/**
 * Provides an AccessToken. AuthConfig implementations provide
 * access to an instance of this interface in order for clients to be able
 * to grab a token.
 */
public abstract class AccessTokenProvider<C extends AuthConfig> {

    protected static final ObjectMapper mapper = new ObjectMapper();
    private final C config;
    private final Map<String, AccessToken> accessTokens = new HashMap<>();
    
    public AccessTokenProvider(C config) {
        this.config = config;    
    }
    
    /**
     * Get the token
     *
     * @return an AccessToken
     * @throws SimbaException if an error occurs
     */
    public abstract AccessToken getToken() throws SimbaException;

    protected C getConfig() {
        return config;
    }

    /**
     * Checks to see if a token has expired, by checking the 'expires' key
     *         Adds an offset to allow for delays when performing auth processes
     *
     * @param token the token to check for expiry. Should contain an 'expires' key
     * @return true if the token is expired
     */
    public boolean isTokenExpired(AccessToken token) {
        long offset = 60 * 1000;
        long now = System.currentTimeMillis();
        return now + offset >= token.getExpires().getTime();
    }

    /**
     * Saves the token data to a file or memory.
     * Checks the tokenDir for alternative token storage locations,
     * otherwise uses the current working path
     * Creates the token directory if it doesn't already exist.
     * Adds an "expires" key to the auth token data, set to time "now" added to the expires_in time
     * This is used later to discover if the token has expired
     *
     * @param clientId The ID for the client, token files are named clientId_token.json
     * @param token The token object to save
     */
    public void cacheToken(String clientId, AccessToken token) {
        if (this.getConfig().isWriteToFile()) {
            String tokenDir = this.getConfig().getTokenDir();
            if (tokenDir == null) {
                tokenDir = System.getProperty("user.dir");
            }
            File tokenDirectory = new File(tokenDir);
            tokenDirectory.mkdirs();
            File tokenFile = new File(tokenDirectory, clientId + "_token.json");
            
            try {
                String data = mapper.writeValueAsString(token);
                BufferedWriter writer = new BufferedWriter(new FileWriter(tokenFile));
                writer.write(data);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // store it in memory no matter what
        this.accessTokens.put(clientId, token);
    }

    /**
     * Checks a local directory for a file containing an auth token
     * If present, check the token hasn't expired, otherwise return it
     *
     * Checks the tokenDir config value for alternative token storage locations,
     * otherwise uses the current working path
     *
     * @param clientId The ID for the client, token files are named clientId_token.json
     * @return an AccessToken, retrieved from the token file or memory.
     */
    public AccessToken getCachedToken(String clientId) {
        // check memory first
        AccessToken token = this.accessTokens.get(clientId);
        if (token != null) {
            if(this.isTokenExpired(token)) {
                this.accessTokens.remove(clientId);    
            } else {
                return token;    
            }
        }
        if (!getConfig().isWriteToFile()) {
            // bail if we are not writing to file
            return null;    
        } else {
            String tokenDir = this.config.getTokenDir();
            if (tokenDir == null) {
                tokenDir = System.getProperty("user.dir");
            }
            File tokenDirectory = new File(tokenDir);
            tokenDirectory.mkdirs();
            File tokenFile = new File(tokenDirectory, clientId + "_token.json");
            if (tokenFile.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(tokenFile));
                    String tokenString = reader.readLine();
                    reader.close();
                    token = mapper.readValue(tokenString, AccessToken.class);
                    if (this.isTokenExpired(token)) {
                        // delete file if it's expired
                        tokenFile.delete();
                        return null;
                    }
                    return token;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
