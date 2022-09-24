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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockchainIdentities {

    @JsonProperty
    private Wallet wallet;

    public Wallet getWallet() {
        if (wallet == null) {
            return Wallet.emptyWallet();
        }
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BlockchainIdentities{");
        sb.append("wallet=")
          .append(wallet);
        sb.append('}');
        return sb.toString();
    }

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class Identity {

        @JsonProperty ("pub")
        private String address;

        @JsonProperty ("priv")
        private String privateKey;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Identity{");
            sb.append("address='")
              .append(address)
              .append('\'');
            sb.append(", privateKey='")
              .append(privateKey)
              .append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Wallet {

        @JsonProperty
        private String principal;

        @JsonProperty
        private String alias;

        @JsonProperty
        private Map<String, Map<String, Map<String, List<Identity>>>> identities;

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Map<String, Map<String, Map<String, List<Identity>>>> getIdentities() {
            return identities;
        }

        public void setIdentities(Map<String, Map<String, Map<String, List<Identity>>>> identities) {
            this.identities = identities;
        }

        @JsonIgnore
        public List<Identity> getIdentities(String blockchainType, String blockchainName) {
            List<Identity> ret = new ArrayList<>();
            Map<String, Map<String, Map<String, List<Identity>>>> ids = getIdentities();
            Map<String, Map<String, List<Identity>>> typeMap = ids.get(blockchainType);
            if (typeMap != null) {
                Map<String, List<Identity>> nodes = typeMap.get(blockchainName);
                if (nodes != null) {
                    for (List<Identity> value : nodes.values()) {
                        ret.addAll(value);
                    }
                }
            }
            return ret;
        }

        @JsonIgnore
        public Map<String, List<Identity>> getIdentityMap(String blockchainType,
            String blockchainName) {
            Map<String, Map<String, Map<String, List<Identity>>>> ids = getIdentities();
            Map<String, Map<String, List<Identity>>> typeMap = ids.get(blockchainType);
            if (typeMap != null) {
                Map<String, List<Identity>> nodes = typeMap.get(blockchainName);
                if (nodes != null) {
                    return nodes;
                }
            }
            return new HashMap<>();
        }

        @JsonIgnore
        public static Wallet emptyWallet() {
            Wallet w = new Wallet();
            w.setAlias("");
            w.setPrincipal("");
            w.setIdentities(new HashMap<>());
            return w;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Wallet{");
            sb.append("principal='")
              .append(principal)
              .append('\'');
            sb.append(", alias='")
              .append(alias)
              .append('\'');
            sb.append(", identities=")
              .append(identities);
            sb.append('}');
            return sb.toString();
        }
    }

}
