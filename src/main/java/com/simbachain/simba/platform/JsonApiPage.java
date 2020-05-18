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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Ided;
import com.simbachain.simba.PagedResult;

/**
 *
 */
public class JsonApiPage<O extends Ided> extends JsonApiList<O> {
    
    @JsonProperty
    private Map<String, String> links = new HashMap<>();
    @JsonProperty
    private Map<String, Object> meta = new HashMap<>();

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public PagedResult<O> toPagedResult() {
        PagedResult<O> result = new PagedResult<>();
        result.setNext(getLinks().get("next"));
        result.setPrevious(getLinks().get("prev"));
        List<JsonApiData<O>> results = getData();
        List<O> actual = new ArrayList<>();
        for (JsonApiData<O> data : results) {
            O obj = data.getAttributes();
            obj.setId(data.getId());
            actual.add(obj);
        }
        result.setResults(actual);
        result.setCount(actual.size());
        return result;
    }
}
