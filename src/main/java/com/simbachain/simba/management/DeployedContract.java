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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Ided;

@JsonIgnoreProperties (ignoreUnknown = true)
public class DeployedContract implements Ided {

    @JsonProperty
    private String id;

    @JsonProperty ("api_name")
    private String apiName;

    @JsonProperty
    private String version;

    @JsonProperty
    private String artifact;

    @JsonProperty
    private String blockchain;

    @JsonProperty
    private String storage;

    @JsonProperty ("contract_type")
    private String contractType;

    @JsonProperty
    private String address;

    @JsonProperty ("asset_type")
    private String assetType;

    @JsonProperty ("display_name")
    private String displayName;

    @JsonProperty ("created_on")
    private Date createdOn;

    @JsonProperty ("updated_on")
    private Date updatedOn;

    @JsonProperty
    private String organisation;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

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

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeployedContract{");
        sb.append("id='")
          .append(id)
          .append('\'');
        sb.append(", apiName='")
          .append(apiName)
          .append('\'');
        sb.append(", version='")
          .append(version)
          .append('\'');
        sb.append(", artifact='")
          .append(artifact)
          .append('\'');
        sb.append(", blockchain='")
          .append(blockchain)
          .append('\'');
        sb.append(", storage='")
          .append(storage)
          .append('\'');
        sb.append(", contractType='")
          .append(contractType)
          .append('\'');
        sb.append(", address='")
          .append(address)
          .append('\'');
        sb.append(", assetType='")
          .append(assetType)
          .append('\'');
        sb.append(", displayName='")
          .append(displayName)
          .append('\'');
        sb.append(", createdOn=")
          .append(createdOn);
        sb.append(", updatedOn=")
          .append(updatedOn);
        sb.append(", organisation='")
          .append(organisation)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
