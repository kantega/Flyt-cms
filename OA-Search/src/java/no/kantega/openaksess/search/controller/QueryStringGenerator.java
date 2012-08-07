/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.openaksess.search.controller;

import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringGenerator {
    public static final String FILTER_PARAM = "filter";

    public static final String QUERY_PARAM = "q";
    private final static String keyValueFormat = "%s=%s";


    private static final String encoding = "utf-8";
    private final static String PAGENO_PARAM = "page";

    public static String getPrevPageUrl(SearchQuery query, int currentPage) {
        return getPageUrl(query, currentPage - 1);
    }

    public static String getNextPageUrl(SearchQuery query, int currentPage) {
        return getPageUrl(query, currentPage + 1);
    }

    public static String getPageUrl(SearchQuery query, int pageNumber) {
        StringBuilder queryStringBuilder = getUrl(query);
        queryStringBuilder.append(String.format(keyValueFormat, PAGENO_PARAM, pageNumber));
        return queryStringBuilder.toString();
    }

    private static StringBuilder getUrl(SearchQuery query) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append(String.format(keyValueFormat, QUERY_PARAM, getEncodedQuery(query.getOriginalQuery())));
        appendFilterQueries(query, queryStringBuilder);

        return queryStringBuilder;
    }

    public static Map<Integer, String> getPageUrls(SearchResponse searchResponse, int standingOnPage, String urlPrefix) {
        LinkedHashMap<Integer, String> pageUrls = new LinkedHashMap<Integer, String>();
        SearchQuery query = searchResponse.getQuery();
        int page = standingOnPage + 1;
        int startPage = ((page / 10) * 10) + 1;
        int endPage = startPage + 9;

        long numberOfHits = searchResponse.getNumberOfHits();
        int resultsPerPage = query.getResultsPerPage();
        if (endPage * resultsPerPage >= numberOfHits) {
            endPage = (int) ((numberOfHits - 1) / numberOfHits);
            endPage++;
        }
        if (startPage > 1) {
            startPage--;
        }
        for (int i = startPage; i <= endPage; i++) {
            pageUrls.put(i, urlPrefix + getPageUrl(query, i));
        }
        return pageUrls;
    }

    public static String getFacetUrl(String facet, SearchResponse searchResponse) {
        StringBuilder queryStringBuilder = getUrl(searchResponse.getQuery());
        queryStringBuilder.append("&");
        queryStringBuilder.append(facet);
        return queryStringBuilder.toString();
    }


    private static void appendFilterQueries(SearchQuery query, StringBuilder queryStringBuilder) {
        List<String> filterQueries = query.getFilterQueries();

        for (String filterQuery : filterQueries) {
            queryStringBuilder.append("&");
            queryStringBuilder.append(String.format(keyValueFormat, FILTER_PARAM, filterQuery));
        }
    }

    private static String getEncodedQuery(String query) {
        try {
            return URLEncoder.encode(query, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
