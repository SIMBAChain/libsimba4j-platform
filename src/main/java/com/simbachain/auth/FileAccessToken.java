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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an OAuth 2 access token.
 */
public class FileAccessToken extends AccessToken {
    
    protected Logger log = LoggerFactory.getLogger(getClass().getName());

    private final String path;
    
    public FileAccessToken(String path) {
        super();
        this.path = path;
        File tokenFile = new File(this.path);
        try {
            if (tokenFile.exists()) {
                log.info("FileAccessToken.FileAccessToken Token file exists.");
                BufferedReader reader = new BufferedReader(new FileReader(tokenFile));
                String tokenBits = reader.readLine();
                String[] parts = tokenBits.split("@");
                if(parts.length != 3) {
                    throw new Exception("Unexpected token file contents: " + tokenBits);
                }
                reader.close();
                long expires = Long.parseLong(parts[0]);
                if (expires > System.currentTimeMillis()) {
                    log.info("FileAccessToken.FileAccessToken refreshing from contents");
                    super.refresh(parts[2], parts[1], expires);
                } 
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh(String token, String type, long expiry) {
        log.info("FileAccessToken.refresh ENTER");
        super.refresh(token, type, expiry);
        File tokenFile = new File(this.path);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(tokenFile));
            writer.write(String.format("%s@%s@%s", expiry, type, token));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("FileAccessToken.refresh EXIT");
    }
    
    public void invalidate() {
        super.invalidate();
        boolean deleted = new File(this.path).delete();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccessToken{");
        sb.append("token='")
          .append(getToken())
          .append('\'');
        sb.append(", type='")
          .append(getType())
          .append('\'');
        sb.append(", expiry=")
          .append(getExpiry());
        sb.append(", valid=")
          .append(isValid());
        sb.append(", path=")
          .append(path);
        sb.append('}');
        return sb.toString();
    }
}
