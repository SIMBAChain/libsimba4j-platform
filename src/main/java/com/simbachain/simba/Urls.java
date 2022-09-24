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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Urls {

    protected static Logger log = LoggerFactory.getLogger(Urls.class.getName());

    private Urls() {
    }

    public static class Paging {

        private final int offset;
        private final int limit;

        public Paging(int offset, int limit) {
            this.offset = offset;
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }

        public static Paging paging(int offset, int limit) {
            return new Paging(offset, limit);
        }
    }

    public enum PathName {
        WHOAMI, WALLET, APPLICATIONS, TXN_COUNT, ADDRESS_TXN_COUNT, CONTRACT_DESIGNS,
        CONTRACT_ARTIFACTS, DEPLOYED_CONTRACTS, BLOCKCHAINS, STORAGE, ORG_TXNS, APP_TXNS, DEPLOY,
        CONTRACT_API, CONTRACT_METHOD, CONTRACT_METHOD_SYNC, BUNDLE_MANIFEST, BUNDLE, BUNDLE_FILE,
        BALANCE, ADDRESS_BALANCE, FUND
    }

    private static final Object[][] urlPaths = {{PathName.WHOAMI, "user/whoami/"},
                                                {PathName.WALLET, "user/wallet/"},
                                                {PathName.TXN_COUNT, "user/transactions/%s/count/"},
                                                {PathName.ADDRESS_TXN_COUNT,
                                                 "user/transactions/%s/count/%s/"},
                                                {PathName.APPLICATIONS,
                                                 "v2/organisations/%s/applications/"},
                                                {PathName.CONTRACT_DESIGNS,
                                                 "v2/organisations/%s/contract_designs/"},
                                                {PathName.CONTRACT_ARTIFACTS,
                                                 "v2/organisations/%s/contract_artifacts/"},
                                                {PathName.DEPLOYED_CONTRACTS,
                                                 "v2/organisations/%s/deployed_contracts/"},
                                                {PathName.BLOCKCHAINS,
                                                 "v2/organisations/%s/blockchains/"},
                                                {PathName.STORAGE, "v2/organisations/%s/storage/"},
                                                {PathName.ORG_TXNS,
                                                 "v2/organisations/%s/transactions/"},
                                                {PathName.APP_TXNS, "v2/apps/%s/transactions/"},
                                                {PathName.DEPLOY,
                                                 "v2/organisations/%s/contract_artifacts/%s/deploy/"},
                                                {PathName.CONTRACT_API,
                                                 "v2/apps/%s/contract/%s/info/"},
                                                {PathName.CONTRACT_METHOD,
                                                 "v2/apps/%s/contract/%s/%s/"},
                                                {PathName.CONTRACT_METHOD_SYNC,
                                                 "v2/apps/%s/sync/contract/%s/%s/"},
                                                {PathName.BUNDLE,
                                                 "v2/apps/%s/contract/%s/bundle/%s/"},
                                                {PathName.BUNDLE_MANIFEST,
                                                 "v2/apps/%s/contract/%s/bundle/%s/manifest/"},
                                                {PathName.BUNDLE_FILE,
                                                 "v2/apps/%s/contract/%s/bundle/%s/filename/%s/"},
                                                {PathName.BALANCE, "/user/account/%s/balance/"},
                                                {PathName.ADDRESS_BALANCE,
                                                 "/user/account/%s/balance/%s/"},
                                                {PathName.FUND, "/user/account/%s/fund/"}};

    public static final Map<PathName, String> pathMap = Stream.of(urlPaths)
                                                              .collect(Collectors.toMap(
                                                                  data -> (PathName) data[0],
                                                                  data -> (String) data[1]));

    public static String url(String host,
        PathName path,
        Query.Params query,
        Paging paging,
        String... args) {
        for (String arg : args) {
            log.debug("args: " + arg);
        }
        String pathPart = pathMap.get(path);
        if (args.length > 0) {
            pathPart = String.format(pathPart, (Object[]) args);
        }
        if (query != null) {
            pathPart = pathPart + query.toJsonApiString();
        }
        String pageInfo = "";
        if (paging != null) {
            pageInfo = String.format("offset=%d&limit=%d", paging.getOffset(), paging.getLimit());
            if (query == null) {
                pageInfo = "?" + pageInfo;
            } else {
                pageInfo = "&" + pageInfo;
            }
        }
        log.debug("path: " + pathPart + " pageInfo: " + pageInfo);
        return host + pathPart + pageInfo;
    }

    public static String url(String host, PathName path, String... args) {
        return url(host, path, null, null, args);
    }

    public static String url(String host, PathName path, Paging paging, String... args) {
        return url(host, path, null, paging, args);
    }

    public static String url(String host, PathName path, Query.Params query, String... args) {
        return url(host, path, query, null, args);
    }

}
