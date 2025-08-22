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

package com.simbachain.simba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class Method {

    @JsonProperty
    private List<String> emits = new ArrayList<>();
    @JsonProperty
    private Boolean accessor = Boolean.FALSE;
    @JsonProperty
    private String visibility = "public";
    @JsonProperty
    private Map<String, Object> documentation = new HashMap<>();
    @JsonProperty
    private List<Parameter> params = new ArrayList<Parameter>();
    @JsonProperty
    private List<Parameter> returns = new ArrayList<Parameter>();

    public List<String> getEmits() {
        return emits;
    }

    public void setEmits(List<String> emits) {
        this.emits = emits;
    }

    public Boolean getAccessor() {
        return accessor;
    }

    public void setAccessor(Boolean accessor) {
        this.accessor = accessor;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Map<String, Object> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Map<String, Object> documentation) {
        this.documentation = documentation;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }

    public List<Parameter> getReturns() {
        return returns;
    }

    public void setReturns(List<Parameter> returns) {
        this.returns = returns;
    }

    @JsonIgnore
    public com.simbachain.simba.Parameter getParameter(String name) {
        for (Parameter param : params) {
            if (param.getName()
                     .equals(name)) {
                return param;
            }
        }
        return null;
    }

    @JsonIgnore
    public Set<String> getParameterNames() {
        Set<String> set = new HashSet<>();
        for (Parameter param : params) {
            set.add(param.getName());
        }
        return set;
    }

    @JsonIgnore
    public Map<String, com.simbachain.simba.Parameter> getParameterMap() {
        Map<String, com.simbachain.simba.Parameter> ret = new HashMap<>();
        for (Parameter param : params) {
            ret.put(param.getName(), param);
        }
        return ret;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContractMethod{");
        sb.append("emits=")
          .append(emits);
        sb.append(", accessor=")
          .append(accessor);
        sb.append(", visibility='")
          .append(visibility)
          .append('\'');
        sb.append(", documentation=")
          .append(documentation);
        sb.append(", params=")
          .append(params);
        sb.append(", returns=")
          .append(returns);
        sb.append('}');
        return sb.toString();
    }
}
