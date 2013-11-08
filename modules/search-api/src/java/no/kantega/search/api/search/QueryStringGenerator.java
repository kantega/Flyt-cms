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

package no.kantega.search.api.search;

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

    public static String getPrevPageUrl(SearchQuery query, int currentPage, boolean shouldAppendFilterQueries) {
        return getPageUrl(query, currentPage - 1, shouldAppendFilterQueries);
    }

    public static String getNextPageUrl(SearchQuery query, int currentPage, boolean shouldAppendFilterQueries) {
        return getPageUrl(query, currentPage + 1, shouldAppendFilterQueries);
    }

    private static String getPageUrl(SearchQuery query, int pageNumber, boolean shouldAppendFilterQueries) {
        StringBuilder queryStringBuilder = getUrl(query, shouldAppendFilterQueries);
        queryStringBuilder.append("&");
        queryStringBuilder.append(String.format(keyValueFormat, PAGENO_PARAM, pageNumber));
        return queryStringBuilder.toString();
    }

    private static StringBuilder getUrl(SearchQuery query, boolean shouldAppendFilterQueries) {
        StringBuilder queryStringBuilder = new StringBuilder(query.getSearchContext().getSearchUrl());
        queryStringBuilder.append("?");
        queryStringBuilder.append(String.format(keyValueFormat, QUERY_PARAM, getEncodedQuery(query.getOriginalQuery())));

        if (shouldAppendFilterQueries) {
            appendFilterQueries(query, queryStringBuilder);
        }

        return queryStringBuilder;
    }

    public static Map<Integer, String> getPageUrls(SearchResponse searchResponse, int standingOnPage, boolean shouldAppendFilterQueries) {
        Map<Integer, String> pageUrls = new LinkedHashMap<>();
        SearchQuery query = searchResponse.getQuery();
        int startPage = ((standingOnPage / 10) * 10);
        int endPage = startPage + 9;

        int numberOfHits = searchResponse.getNumberOfHits().intValue();
        int resultsPerPage = query.getResultsPerPage();
        if (endPage * resultsPerPage >= numberOfHits) {
            endPage = ((numberOfHits - 1) / resultsPerPage);
        }
        if (startPage > 1) {
            startPage--;
        }
        for (int i = startPage; i <= endPage; i++) {
            pageUrls.put(i, getPageUrl(query, i, shouldAppendFilterQueries));
        }
        return pageUrls;
    }

    public static String getFacetUrl(String facetfield, String facetvalue, SearchQuery searchQuery, boolean shouldAppendFilterQueries) {
        StringBuilder queryStringBuilder = getUrl(searchQuery, shouldAppendFilterQueries);
        queryStringBuilder.append("&");
        queryStringBuilder.append(FILTER_PARAM);
        queryStringBuilder.append("=");
        queryStringBuilder.append(getEncodedQuery(String.format(keyValueFormat, facetfield, facetvalue)));

        return queryStringBuilder.toString();
    }

    private static void appendFilterQueries(SearchQuery query, StringBuilder queryStringBuilder) {
        List<String> filterQueries = query.getFilterQueries();

        for (String filterQuery : filterQueries) {
            queryStringBuilder.append("&");
            queryStringBuilder.append(String.format(keyValueFormat, FILTER_PARAM, getEncodedQuery(filterQuery)));
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
