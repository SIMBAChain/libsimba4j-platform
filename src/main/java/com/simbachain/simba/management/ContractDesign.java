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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Ided;
import com.simbachain.simba.Metadata;

/**
 *
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class ContractDesign implements Ided {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String version;

    @JsonProperty
    private String code;

    @JsonProperty
    private String language;

    @JsonProperty
    private String err;

    @JsonProperty
    private String mode;

    @JsonProperty
    private String model;

    @JsonProperty ("asset_type")
    private String assetType;

    @JsonProperty ("service_args")
    private Map<String, Object> serviceArgs;

    @JsonProperty ("created_on")
    private Date createdOn;

    @JsonProperty ("updated_on")
    private Date updatedOn;

    @JsonProperty
    private String organisation;

    @JsonProperty
    private Metadata metadata;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonIgnore
    public String decodedCode() {
        byte[] bytes = Base64.getDecoder()
                             .decode(getCode());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public Map<String, Object> getServiceArgs() {
        return serviceArgs;
    }

    public void setServiceArgs(Map<String, Object> serviceArgs) {
        this.serviceArgs = serviceArgs;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContractDesign{");
        sb.append("id='")
          .append(id)
          .append('\'');
        sb.append(", name='")
          .append(name)
          .append('\'');
        sb.append(", version='")
          .append(version)
          .append('\'');
        sb.append(", code='")
          .append(code)
          .append('\'');
        sb.append(", language='")
          .append(language)
          .append('\'');
        sb.append(", err='")
          .append(err)
          .append('\'');
        sb.append(", mode='")
          .append(mode)
          .append('\'');
        sb.append(", model='")
          .append(model)
          .append('\'');
        sb.append(", assetType='")
          .append(assetType)
          .append('\'');
        sb.append(", serviceArgs=")
          .append(serviceArgs);
        sb.append(", createdOn=")
          .append(createdOn);
        sb.append(", updatedOn=")
          .append(updatedOn);
        sb.append(", organisation='")
          .append(organisation)
          .append('\'');
        sb.append(", metadata=")
          .append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
