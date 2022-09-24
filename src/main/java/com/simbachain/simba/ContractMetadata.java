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

package com.simbachain.simba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class ContractMetadata {

    @JsonProperty
    private String version;

    @JsonProperty
    private String name;

    @JsonProperty ("abstract")
    private boolean isAbstract;

    @JsonProperty
    private Map<String, Method> methods = new HashMap<>();

    @JsonProperty
    private Map<String, Method> events = new HashMap<>();

    @JsonProperty
    private Method constructor;

    @JsonProperty
    private Map<String, ContractType> types = new HashMap<>();

    @JsonProperty
    private Map<String, Object> enums = new HashMap<>();

    @JsonProperty
    private Map<String, Object> source = new HashMap<>();

    @JsonProperty
    private Map<String, Object> documentation = new HashMap<>();

    @JsonProperty
    private Map<String, Object> meta = new HashMap<>();

    @JsonProperty
    private Map<String, Object> auth = new HashMap<>();

    @JsonProperty
    private Map<String, Object> assets = new HashMap<>();

    @JsonProperty
    private List<String> inheritance = new ArrayList<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAbstract() {
        return isAbstract;
    }

    public void setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, Method> methods) {
        this.methods = methods;
    }

    public Map<String, Method> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Method> events) {
        this.events = events;
    }

    public Method getConstructor() {
        return constructor;
    }

    public void setConstructor(Method constructor) {
        this.constructor = constructor;
    }

    public Map<String, ContractType> getTypes() {
        return types;
    }

    public void setTypes(Map<String, ContractType> types) {
        this.types = types;
    }

    public Map<String, Object> getEnums() {
        return enums;
    }

    public void setEnums(Map<String, Object> enums) {
        this.enums = enums;
    }

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source = source;
    }

    public Map<String, Object> getAuth() {
        return auth;
    }

    public void setAuth(Map<String, Object> auth) {
        this.auth = auth;
    }

    public Map<String, Object> getAssets() {
        return assets;
    }

    public void setAssets(Map<String, Object> assets) {
        this.assets = assets;
    }

    public Map<String, Object> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Map<String, Object> documentation) {
        this.documentation = documentation;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public List<String> getInheritance() {
        return inheritance;
    }

    public void setInheritance(List<String> inheritance) {
        this.inheritance = inheritance;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContractMetadata{");
        sb.append("version='")
          .append(version)
          .append('\'');
        sb.append(", name='")
          .append(name)
          .append('\'');
        sb.append(", isAbstract=")
          .append(isAbstract);
        sb.append(", methods=")
          .append(methods);
        sb.append(", events=")
          .append(events);
        sb.append(", constructor=")
          .append(constructor);
        sb.append(", types=")
          .append(types);
        sb.append(", source=")
          .append(source);
        sb.append(", auth=")
          .append(auth);
        sb.append(", assets=")
          .append(assets);
        sb.append('}');
        return sb.toString();
    }
}
