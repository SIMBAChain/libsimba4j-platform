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

package com.simbachain.simba.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paweladamski.httpclientmock.HttpClientMock;
import com.simbachain.simba.JsonData;

import static org.hamcrest.text.MatchesPattern.matchesPattern;

/**
 *
 */
public class MockClient {
    
    private final Map<String,Object> templates;
    private final ObjectMapper stringMapper;
    private final Map<String, Pattern> patterns;
    private final HttpClientMock httpClientMock;

    public MockClient() {
        this.templates = MockClient.loadTypes();
        this.stringMapper = new ObjectMapper();
        this.patterns = this.getPatterns();
        this.httpClientMock = new HttpClientMock();
    }

    public static Map<String,Object> loadTypes() {
        try {
            InputStream in = MockClient.class.getResourceAsStream("/types.json");
            return new ObjectMapper().readValue(in, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadMetadata(String name) {
        InputStream in = MockClient.class.getResourceAsStream(String.format("/%s.json", name));
        return new BufferedReader(
            new InputStreamReader(in, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    public Map<String, Pattern> getPatterns() {
        Map<String, Pattern> patterns = new HashMap<>();
        patterns.put("LOGIN", Pattern.compile(".*/o/token.*"));
        patterns.put("ME", Pattern.compile("/user/whoami/"));
        patterns.put("WALLET", Pattern.compile("/user/wallet/"));
        patterns.put("ORG", Pattern.compile(".*/v2/organisations/[\\w-]+/$"));
        patterns.put("BALANCE", Pattern.compile(".*/user/account/[\\w-]+/balance/$"));
        patterns.put("ADDRESS_BALANCE", Pattern.compile(".*/user/account/[\\w-]+/balance/[\\w-]+/$"));
        patterns.put("BLOCKCHAINS", Pattern.compile(".*/v2/organisations/[\\w-]+/blockchains/$"));
        patterns.put("STORAGE", Pattern.compile(".*/v2/organisations/[\\w-]+/storage/$"));
        patterns.put("APPS", Pattern.compile(".*/v2/organisations/[\\w-]+/applications/$"));
        patterns.put("APP", Pattern.compile(".*/v2/organisations/[\\w-]+/applications/[\\w-]+/$"));
        patterns.put("DESIGNS", Pattern.compile(".*/v2/organisations/[\\w-]+/contract_designs/$"));
        patterns.put("ARTIFACTS", Pattern.compile(".*/v2/organisations/[\\w-]+/contract_artifacts/$"));
        patterns.put("DEPLOYMENT", Pattern.compile(".*/v2/organisations/[\\w-]+/deployments/[\\w-]+/$"));
        patterns.put("DEPLOYMENTS", Pattern.compile(".*/v2/organisations/[\\w-]+/deployments/$"));
        patterns.put("CONTRACTS", Pattern.compile(".*/v2/organisations/[\\w-]+/deployed_contracts/$"));
        patterns.put("CONTRACT", Pattern.compile(".*/v2/organisations/[\\w-]+/deployed_contracts/[\\w-]+/$"));
        patterns.put("TXN", Pattern.compile(".*/v2/organisations/[\\w-]+/transactions/[\\w-]+/$"));
        patterns.put("SUBMIT_METHOD", Pattern.compile(".*/v2/apps/[\\w-]+/contract/[\\w-]+/[\\w-]+/$"));
        patterns.put("QUERY_METHOD", Pattern.compile(".*/v2/apps/[\\w-]+/contract/[\\w-]+/[\\w-]+/\\\\?.*$"));
        patterns.put("GETTER_METHOD", Pattern.compile(".*/v2/apps/[\\w-]+/contract/[\\w-]+/get[\\w-]+/\\\\?.*$"));
        patterns.put("APP_TXN", Pattern.compile(".*/v2/apps/%s/transactions/[\\w-]+/$"));
        patterns.put("CONTRACT_METADATA", Pattern.compile(".*/v2/apps/[\\w-]+/contract/[\\w-]+/info/$"));
        patterns.put("MANIFEST", Pattern.compile(".*/v2/apps/[\\w-]+/contract/[\\w-]+/bundle/[\\w-]+/manifest/$"));
        //.*/v2/apps/[\w-]+/contract/[\w-]+/bundle/[\w-]+/manifest/
        return patterns;
    }

    protected HttpClientMock configureMock(HttpClientMock clientMock) throws IOException {
        clientMock.onPost()
                      .withPath(matchesPattern(this.patterns.get("LOGIN")))
                      .doReturnJSON("{\"access_token\": \"1234567890\", \"token_type\": \"Bearer\", \"expires_in\": 200}");
        
        clientMock.onGet()
                      .withPath(matchesPattern(this.patterns.get("ME")))
                      .doReturnJSON(this.makeObject("user"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("BLOCKCHAINS")))
                  .doReturnJSON(this.makeResults("blockchain"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("STORAGE")))
                  .doReturnJSON(this.makeResults("storage"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("ORG")))
                  .doReturnJSON(this.makeResults("organisation"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("APPS")))
                  .doReturnJSON(this.makeResults("list:application"));

        clientMock.onPost()
                  .withPath(matchesPattern(this.patterns.get("APPS")))
                  .doReturnJSON(this.makeObject("application", Stream.of(new Object[][] {
                      { "name", "libsimba" }
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onPost()
                  .withPath(matchesPattern(this.patterns.get("DESIGNS")))
                  .doReturnJSON(this.makeObject("contract_design", Stream.of(new Object[][] {
                      { "name", "libsimba" },
                      {"id", "1234"}
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("DESIGNS")))
                  .doReturnJSON(this.makeResults("list:contract_design"));

        clientMock.onPost()
                  .withPath(matchesPattern(this.patterns.get("ARTIFACTS")))
                  .doReturnJSON(this.makeObject("contract_artifact", Stream.of(new Object[][] {
                      { "name", "libsimba" },
                      {"id", "1234"}
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("ARTIFACTS")))
                  .doReturnJSON(this.makeResults("contract_artifact"));

        clientMock.onPost()
                  .withPath(matchesPattern(this.patterns.get("DEPLOYMENTS")))
                  .doReturnJSON(this.makeObject("deployment", Stream.of(new Object[][] {
                      { "state", "COMPLETED" },
                      {"primary", JsonData.with("deployed_artifact_id", "1234").asMap()}
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("DEPLOYMENT")))
                  .doReturnJSON(this.makeObject("deployment", Stream.of(new Object[][] {
                      { "state", "COMPLETED" },
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("TXN")))
                  .doReturnJSON(this.makeObject("transaction", Stream.of(new Object[][] {
                      { "state", "COMPLETED" }
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("TXN")))
                  .doReturnJSON(this.makeObject("transaction", Stream.of(new Object[][] {
                      { "state", "COMPLETED" }
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onPost()
                  .withPath(matchesPattern(this.patterns.get("SUBMIT_METHOD")))
                  .doReturnJSON(this.makeObject("transaction", Stream.of(new Object[][] {
                      { "state", "ACCEPTED" }
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("QUERY_METHOD")))
                  .doReturnJSON(this.makeResults("transaction", 2));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("GETTER_METHOD")))
                  .doReturnJSON(this.makeObject("getter"));

        clientMock.onPost()
                  .withPath(matchesPattern(this.patterns.get("APP_TXN")))
                  .doReturnJSON(this.makeObject("transaction", Stream.of(new Object[][] {
                      { "state", "SUBMITTED" }
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("BALANCE")))
                  .doReturnJSON(this.makeObject("balance", Stream.of(new Object[][] {
                      { "balance", 100000 }
                  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]))));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("WALLET")))
                  .doReturnJSON(this.makeObject("wallet"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("CONTRACTS")))
                  .doReturnJSON(this.makeResults("deployed_contract", 2));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("CONTRACT")))
                  .doReturnJSON(this.makeObject("deployed_contract"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("CONTRACT_METADATA")))
                  .doReturnJSON(MockClient.loadMetadata("supplychain"));

        clientMock.onGet()
                  .withPath(matchesPattern(this.patterns.get("MANIFEST")))
                  .doReturnJSON(this.makeObject("manifest"));

        return clientMock;
    }
    
    public HttpClientMock getHttpClientMock() {
        try {
            return this.configureMock(httpClientMock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public String makeObject(String model, Map<String, Object> replacements)
        throws IOException {
        Map<String,Object> type = (Map<String, Object>) this.templates.get(model);
        if(type == null) {
            throw new IOException("Unknown type: " + model);
        }
        Map<String,Object> ret = new HashMap<>(type);
        if (replacements != null) {
            for (String s : replacements.keySet()) {
                if (type.get(s) != null) {
                    ret.put(s, replacements.get(s));
                }
            }
        }
        return this.stringMapper.writeValueAsString(ret);
    }
    
    public String makeObject(String model)
        throws IOException {
        return makeObject(model, null);
    }

    @SuppressWarnings("unchecked")
    public String makeList(String model, int length)
        throws IOException {
        Map<String,Object> type = (Map<String, Object>) this.templates.get(model);
        if(type == null) {
            throw new IOException("Unknown type: " + model);
        }
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Map<String,Object> item = new HashMap<>(type);
            list.add(item);
        }
        return this.stringMapper.writeValueAsString(list);
    }

    public String makeList(String model)
        throws IOException {
        return makeList(model, 3);
    }

    @SuppressWarnings("unchecked")
    public String makeResults(String model, int length)
        throws IOException {
        Map<String,Object> type = (Map<String, Object>) this.templates.get(model);
        if(type == null) {
            throw new IOException("Unknown type: " + model);
        }
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Map<String,Object> item = new HashMap<>(type);
            list.add(item);
        }
        Map<String, Object> results = new HashMap<>();
        results.put("count", length);
        results.put("next", null);
        results.put("previous", null);
        results.put("results", list);
        return this.stringMapper.writeValueAsString(results);
    }

    public String makeResults(String model)
        throws IOException {
        return makeResults(model, 3);
    }
}
