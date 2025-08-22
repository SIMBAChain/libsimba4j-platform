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

package com.simbachain.simba.management;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.simbachain.simba.JsonData;
import com.simbachain.simba.Jsonable;

/**
 * Class used to define deployment specification when deploying an artifact.
 */
public class CompilationSpec implements Jsonable {

    private String name;
    private String code;
    private boolean encodeCode = true; 
    private String language = "solidity";
    private String model = "aat";
    private String targetContract;
    private List<String> binaryTargets;
    private Map<String, Object> libraries;
    
    public CompilationSpec withName(String name) {
        this.name = name;
        return this;
    }

    public CompilationSpec withEncode(boolean encode) {
        this.encodeCode = encode;
        return this;
    }

    public CompilationSpec withLanguage(String language) {
        this.language = language;
        return this;
    }

    public CompilationSpec withModel(String model) {
        this.model = model;
        return this;
    }

    public CompilationSpec withTargetContract(String targetContract) {
        this.targetContract = targetContract;
        return this;
    }

    public CompilationSpec withBinaryTargets(List<String> binaryTargets) {
        this.binaryTargets = binaryTargets;
        return this;
    }

    public CompilationSpec withLibraries(Map<String, Object> libraries) {
        this.libraries = libraries;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEncodeCode() {
        return encodeCode;
    }

    public String getLanguage() {
        return language;
    }
    
    public String getModel() {
        return model;
    }

    public String getTargetContract() {
        return targetContract;
    }

    public List<String> getBinaryTargets() {
        return binaryTargets;
    }


    public Map<String, Object> getLibraries() {
        return libraries;
    }

    @Override
    public JsonData toJsonData() {
        if (encodeCode) {
             code  = Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8));
        }
        JsonData data = JsonData.with("name", name)
                                .and("code", code)
                                .and("model", model)
                                .and("language", language)
                                .and("mode", "code");
        if (targetContract != null) {
            data = data.and("target_contract", targetContract);
        }
        if (binaryTargets != null) {
            data = data.and("binary_targets", binaryTargets);
        }
        if (libraries != null) {
            data = data.and("libraries", libraries);
        }
        return data;
    }
}
