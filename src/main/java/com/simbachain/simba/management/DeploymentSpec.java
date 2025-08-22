/*
 * Copyright (c) 2025 SIMBA Chain Inc.
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

package com.simbachain.simba.management;

import java.util.Map;

import com.simbachain.simba.JsonData;
import com.simbachain.simba.Jsonable;

/**
 * Class used to define deployment specification when deploying an artifact.
 */
public class DeploymentSpec implements Jsonable {

    private String artifactId;
    private String blockchain;
    private String storage;
    private String apiName;
    private String appName;
    private String displayName;
    private String deployedAddress;
    private Map<String, Object> args;

    public String getArtifactId() {
        return artifactId;
    }

    public DeploymentSpec withArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public DeploymentSpec withBlockchain(String blockchain) {
        this.blockchain = blockchain;
        return this;
    }

    public String getStorage() {
        return storage;
    }

    public DeploymentSpec withStorage(String storage) {
        this.storage = storage;
        return this;
    }

    public String getApiName() {
        return apiName;
    }

    public DeploymentSpec withApiName(String apiName) {
        this.apiName = apiName;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public DeploymentSpec withAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public DeploymentSpec DisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDeployedAddress() {
        return deployedAddress;
    }

    public DeploymentSpec withDeployedAddress(String deployedAddress) {
        this.deployedAddress = deployedAddress;
        return this;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public DeploymentSpec withArgs(Map<String, Object> args) {
        this.args = args;
        return this;
    }

    @Override
    public JsonData toJsonData() {
        JsonData data = JsonData.with("blockchain", blockchain)
                                .and("storage", storage)
                                .and("api_name", apiName)
                                .and("artifact_id", artifactId);
        if (displayName != null) {
            data = data.and("display_name", displayName);
        }
        if (appName != null) {
            data = data.and("app_name", appName);
        }
        if (deployedAddress != null) {
            data = data.and("deployed_address", deployedAddress);
        }
        if (args != null) {
            data = data.and("args", args);
        }
        return data;
    }
}
