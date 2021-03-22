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

package com.simbachain.simba.platform.management;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Ided;

/**
 *  An Application object.
 */
public class Application implements Ided {
    
    @JsonProperty
    private String id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("created_on")
    private Date createdOn;
    
    @JsonProperty("components")
    private List<Map<String, String>> contracts;

    @JsonProperty ("openapi")
    private String openapi;

    @JsonProperty
    private Map<String, String> organisation;

    @JsonProperty
    private Map<String, Object> metadata = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Map<String, String>> getContracts() {
        return contracts;
    }

    public void setContracts(List<Map<String, String>> contracts) {
        this.contracts = contracts;
    }

    public Map<String, String> getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Map<String, String> organisation) {
        this.organisation = organisation;
    }

    public String getOpenapi() {
        return openapi;
    }

    public void setOpenapi(String openapi) {
        this.openapi = openapi;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Application{");
        sb.append("id='")
          .append(id)
          .append('\'');
        sb.append(", name='")
          .append(name)
          .append('\'');
        sb.append(", displayName='")
          .append(displayName)
          .append('\'');
        sb.append(", createdOn=")
          .append(createdOn);
        sb.append(", contracts=")
          .append(contracts);
        sb.append(", organisation='")
          .append(organisation)
          .append('\'');
        sb.append(", metadata=")
          .append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
