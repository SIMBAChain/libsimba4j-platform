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

package com.simbachain.simba.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Metadata;
import com.simbachain.simba.Method;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractInfo implements Metadata {

    @JsonProperty ("api_name")
    private String apiName;
    @JsonProperty
    private String faucet;
    @JsonProperty
    private String name;
    @JsonProperty
    private String network;
    @JsonProperty ("network_type")
    private String networkType;
    @JsonProperty
    private boolean poa;
    @JsonProperty ("simba_faucet")
    private boolean simbaFaucet = false;
    @JsonProperty
    private ContractMetadata contract;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getFaucet() {
        return faucet;
    }

    public void setFaucet(String faucet) {
        this.faucet = faucet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetworkType() {
        return networkType;
    }

    @Override
    public boolean isPoa() {
        return poa;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public void setPoa(boolean poa) {
        this.poa = poa;
    }

    public boolean isSimbaFaucet() {
        return simbaFaucet;
    }

    @Override
    public String getType() {
        return "platform";
    }

    @Override
    public Method getMethod(String name) {
        return getContract().getMethods().get(name);
    }

    @Override
    public String getFileIndicator() {
        return "_bundleHash";
    }

    public void setSimbaFaucet(boolean simbaFaucet) {
        this.simbaFaucet = simbaFaucet;
    }

    public ContractMetadata getContract() {
        return contract;
    }

    public void setContract(ContractMetadata contract) {
        this.contract = contract;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContractInfo{");
        sb.append("apiName='")
          .append(apiName)
          .append('\'');
        sb.append(", faucet='")
          .append(faucet)
          .append('\'');
        sb.append(", name='")
          .append(name)
          .append('\'');
        sb.append(", network='")
          .append(network)
          .append('\'');
        sb.append(", networkType='")
          .append(networkType)
          .append('\'');
        sb.append(", poa=")
          .append(poa);
        sb.append(", simbaFaucet=")
          .append(simbaFaucet);
        sb.append(", contract=")
          .append(contract);
        sb.append('}');
        return sb.toString();
    }
}
