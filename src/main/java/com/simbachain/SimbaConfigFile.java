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

package com.simbachain;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Provides access to the following configuration values used across Simba client tools:
 * <p>
 * SIMBA_AUTH_CLIENT_ID is the user ID for the tool that they use to login.
 * </p><p>
 * SIMBA_AUTH_CLIENT_SECRET is the secret used to gain a token via the OAuth client_credentials flow.
 * </p><p>
 * SIMBA_API_BASE_URL is the root of the API URL
 * </p><p>
 * SIMBA_AUTH_BASE_URL is the root of the auth provider’s URL.
 * </p><p>
 * SIMBA_AUTH_SCOPE auth scope.
 * </p><p>
 * SIMBA_AUTH_REALM
 * </p>
 */
public class SimbaConfigFile {

    public static final String SIMBA_HOME = "SIMBA_HOME";
    public static final String SIMBA_LOGGING_CONF = "SIMBA_LOGGING_CONF";
    public static final String SIMBA_AUTH_CLIENT_ID = "SIMBA_AUTH_CLIENT_ID";
    public static final String SIMBA_AUTH_CLIENT_SECRET = "SIMBA_AUTH_CLIENT_SECRET";
    public static final String SIMBA_API_BASE_URL = "SIMBA_API_BASE_URL";
    public static final String SIMBA_AUTH_BASE_URL = "SIMBA_AUTH_BASE_URL";
    public static final String SIMBA_AUTH_SCOPE = "SIMBA_AUTH_SCOPE";
    public static final String SIMBA_AUTH_REALM = "SIMBA_AUTH_REALM";
    public static final String ENV_FILENAME = "simbachain.env";
    public static final String ENV_DOT_FILENAME = ".simbachain.env";
    public static final String ENV_DEFAULT = ".env";

    private final Dotenv dotenv;

    public SimbaConfigFile() {
        this.dotenv = this.loadConfig();
    }

    private File findFile(String root) {
        File f = new File(String.join(File.pathSeparator, root, ENV_DOT_FILENAME));
        if (f.exists()) {
            return f;
        }
        f = new File(String.join(File.pathSeparator, root, ENV_FILENAME));
        if (f.exists()) {
            return f;
        }
        f = new File(String.join(File.pathSeparator, root, ENV_DEFAULT));
        if (f.exists()) {
            return f;
        }
        return null;
    }

    private File findResource() {
        try {
            URL resource = getClass().getClassLoader()
                                     .getResource(ENV_DOT_FILENAME);
            if (resource != null) {
                return new File(resource.toURI());
            }
            resource = getClass().getClassLoader()
                                 .getResource(ENV_FILENAME);
            if (resource != null) {
                return new File(resource.toURI());
            }
            resource = getClass().getClassLoader()
                                 .getResource(ENV_DEFAULT);
            if (resource != null) {
                return new File(resource.toURI());
            }
            return null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private Dotenv loadConfig() {
        File f = this.findResource();
        if (f == null) {
            f = this.findFile(System.getProperty(SIMBA_HOME, System.getProperty("user.home")));
        }
        if (f != null) {
            return Dotenv.configure()
                         .directory(f.getParent())
                         .filename(f.getName())
                         .ignoreIfMissing()
                         .load();
        }
        return Dotenv.configure()
                     .ignoreIfMissing()
                     .load();
    }

    public String get(String key) {
        return dotenv.get(key);
    }

    public String get(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }

    public String getAuthClientId() {
        return dotenv.get(SIMBA_AUTH_CLIENT_ID);
    }

    public String getAuthClientSecret() {
        return dotenv.get(SIMBA_AUTH_CLIENT_SECRET);
    }

    public String getApiBaseUrl() {
        return dotenv.get(SIMBA_API_BASE_URL);
    }

    public String getAuthBAseUrl() {
        return dotenv.get(SIMBA_AUTH_BASE_URL);
    }

    public String getAuthScope() {
        return dotenv.get(SIMBA_AUTH_SCOPE);
    }

    public String getAuthRealm() {
        return dotenv.get(SIMBA_AUTH_REALM);
    }

    public String getLoggingConf() {
        return dotenv.get(SIMBA_LOGGING_CONF);
    }

}
