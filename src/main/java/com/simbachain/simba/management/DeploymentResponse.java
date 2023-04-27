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

package com.simbachain.simba.management;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Ided;

/**
 * Response to a deployment.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeploymentResponse implements Ided {

    @JsonProperty ("id")
    private String id;
    
    @JsonProperty ("current_txn")
    private String currentTransaction;

    @JsonProperty ("primary")
    private Map<String, Object> primary;

    @JsonProperty
    private String state;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(String currentTransaction) {
        this.currentTransaction = currentTransaction;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDeployedArtifactId() {
        return (String)primary.get("deployed_artifact_id");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeploymentResponse{");
        sb.append("primary='")
          .append(primary)
          .append('\'');
        sb.append(", state='")
          .append(state)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
