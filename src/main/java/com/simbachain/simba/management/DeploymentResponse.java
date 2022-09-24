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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Response to a deployment.
 */
public class DeploymentResponse {
    
    @JsonProperty ("transaction_hash")
    private String transactionHash;

    @JsonProperty ("transaction_id")
    private String transactionId;

    @JsonProperty ("deployment_id")
    private String deploymentId;
    
    @JsonProperty ("instance_id")
    private String instanceId;

    @JsonProperty
    private String state;

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeploymentResponse{");
        sb.append("transactionHash='")
          .append(transactionHash)
          .append('\'');
        sb.append(", transactionId='")
          .append(transactionId)
          .append('\'');
        sb.append(", deploymentId='")
          .append(deploymentId)
          .append('\'');
        sb.append(", instanceId='")
          .append(instanceId)
          .append('\'');
        sb.append(", state='")
          .append(state)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
