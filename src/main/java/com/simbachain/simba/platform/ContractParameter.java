/*
 * Copyright (c) 2020 SIMBA Chain Inc.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Parameter;

/**
 *
 */
public class ContractParameter implements Parameter {
    
    @JsonProperty
    private String name;
    @JsonProperty
    private String type;
    @JsonProperty("default_value")
    private String defaultValue;
    @JsonProperty("simba_type")
    private String simbaType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getSimbaType() {
        return simbaType;
    }

    public void setSimbaType(String simbaType) {
        this.simbaType = simbaType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Param{");
        sb.append("name='")
          .append(name)
          .append('\'');
        sb.append(", type='")
          .append(type)
          .append('\'');
        sb.append(", defaultValue='")
          .append(defaultValue)
          .append('\'');
        sb.append(", simbaType='")
          .append(simbaType)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
