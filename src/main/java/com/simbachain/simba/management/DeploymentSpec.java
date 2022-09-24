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

package com.simbachain.simba.management;

import java.util.Map;

import com.simbachain.simba.JsonData;
import com.simbachain.simba.Jsonable;

/**
 *  Class used to define deployment specification when deploying an artifact.
 */
public class DeploymentSpec implements Jsonable {
    
    private String blockchain;
    private String storage;
    private String apiName;
    private String appName;
    private String displayName;
    private String assetType;
    private String preTxnHook;
    private Boolean singleton = false;
    private String deployedAddress;
    private Map<String, Object> args;

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getPreTxnHook() {
        return preTxnHook;
    }

    public void setPreTxnHook(String preTxnHook) {
        this.preTxnHook = preTxnHook;
    }

    public Boolean getSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public String getDeployedAddress() {
        return deployedAddress;
    }

    public void setDeployedAddress(String deployedAddress) {
        this.deployedAddress = deployedAddress;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    @Override
    public JsonData toJsonData() {
        JsonData data = JsonData.with("blockchain", blockchain)
            .and("storage", storage)
            .and("api_name", apiName)
            .and("singleton", singleton);
        if (displayName != null) {
            data = data.and("display_name", displayName);
        }
        if (appName != null) {
            data = data.and("app_name", appName);
        }
        if (assetType != null) {
            data = data.and("asset_type", assetType);
        }
        if (preTxnHook != null) {
            data = data.and("pre_txn_hook", preTxnHook);
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
