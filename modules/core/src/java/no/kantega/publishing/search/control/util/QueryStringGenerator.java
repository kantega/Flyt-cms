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

package no.kantega.publishing.search.control.util;

import no.kantega.publishing.search.service.SearchServiceQuery;
import no.kantega.publishing.search.service.SearchServiceResultImpl;
import no.kantega.publishing.search.service.SearchServiceResult;
import no.kantega.search.result.HitCount;
import no.kantega.search.result.SearchResultExtendedImpl;
import no.kantega.search.index.Fields;

import java.util.*;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;


public class QueryStringGenerator {
    private String encoding;

    public QueryStringGenerator(String encoding) {
        this.encoding = encoding;
    }

    private String queryString(SearchServiceQuery query, Map<String, String> newParams) {
        List<String> paramNames = new ArrayList<String>(query.getParamNames());
        Map<String, String> params = new HashMap<String, String>();
        for (String name : paramNames) {
            String value = query.getStringParam(name);
            if (value != null) {
                params.put(name, value);
            }
        }
        params.putAll(newParams);
        return queryString(params);
    }

    public String queryString(Map<String, String> params) {
        StringBuilder queryStringBuilder = new StringBuilder();
        for (String name : params.keySet()) {
            String p = params.get(name);
            if (p != null) {
                try {
                    p = URLEncoder.encode(p, encoding);
                } catch (UnsupportedEncodingException e) {

                }
                queryStringBuilder.append(name).append("=").append(p).append("&");
            }
        }
        if (queryStringBuilder.length() > 0) {
            queryStringBuilder.deleteCharAt(queryStringBuilder.length() - 1);
        }
        return queryStringBuilder.toString();
    }

    public String prevPage(SearchServiceQuery query, int currentPage) {
        return prevPage(query, new String[0], currentPage);
    }

    public String prevPage(SearchServiceQuery query, String[] ignores, int currentPage) {
        String prevPageUrl = "";
        if (currentPage > 0) {
            String[] keys;
            String[] values;
            keys = new String[1];
            values = new String[1];
            keys[0] = SearchServiceQuery.METAPARAM_PAGE;
            values[0] = (currentPage - 1) + "";
            prevPageUrl = replaceParams(query, ignores, keys, values);
        }
        return prevPageUrl;
    }

    public String nextPage(SearchServiceQuery query, int currentPage, int hitsPerPage, int documentCount) {
        return nextPage(query, new String[0], currentPage, hitsPerPage, documentCount);
    }

    public String nextPage(SearchServiceQuery query, String[] ignores, int currentPage, int hitsPerPage, int documentCount) {
        String nextPageUrl = "";
        if ((currentPage + 1) * hitsPerPage < documentCount) {
            String[] keys;
            String[] values;
            keys = new String[1];
            values = new String[1];
            keys[0] = SearchServiceQuery.METAPARAM_PAGE;
            values[0] = (currentPage + 1) + "";
            nextPageUrl = replaceParams(query, ignores, keys, values);
        }
        return nextPageUrl;
    }

    public String addParam(SearchServiceQuery query, String key, String value) {
        return replaceParam(query, key, value);
    }

    public String replaceParam(SearchServiceQuery query, String key, String value) {
        Map<String, String> newParams = new HashMap<String, String>();
        newParams.put(key, value);
        return queryString(query, newParams);
    }

    public String replaceParams(SearchServiceQuery query, String[] keys, String[] values) {
        Map<String, String> newParams = new HashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            newParams.put(keys[i], values[i]);
        }
        return queryString(query, newParams);
    }

    public String replaceParams(SearchServiceQuery query, String[] oldKeys, String key, String value) {
        return replaceParams(query, oldKeys, new String[]{ key }, new String[]{ value });
    }

    public String replaceParams(SearchServiceQuery query, String[] oldKeys, String[] keys, String[] values) {
        List<String> paramNames = new ArrayList<String>(query.getParamNames());
        for (String s : oldKeys) {
            paramNames.remove(s);
        }
        Map<String, String> params = new HashMap<String, String>();
        for (String name : paramNames) {
            if (query.getStringParam(name) != null) {
                params.put(name, query.getStringParam(name));
            }
        }
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], values[i]);
        }
        return queryString(params);
    }

    public String removeParam(SearchServiceQuery query, String key) {
        List<String> paramNames = new ArrayList<String>(query.getParamNames());
        paramNames.remove(key);
        Map<String, String> params = new HashMap<String, String>();
        for (String name : paramNames) {
            if (query.getStringParam(name) != null) {
                params.put(name, query.getStringParam(name));
            }
        }
        return queryString(params);
    }

    public String createLastModifiedUrl(SearchServiceQuery query, HitCount hitCount) {
        String retVal = null;
        if (query.getDateParamAsString(SearchServiceQuery.PARAM_LAST_MODIFIED_FROM) == null
                || query.getDateParamAsString(SearchServiceQuery.PARAM_LAST_MODIFIED_TO) == null) {
            String termTrimmed = hitCount.getTerm().replaceAll("[\\[\\]]", "");
            String[] terms = termTrimmed.split("TO");
            if (terms.length == 2) {
                String[] keys = new String[]{ hitCount.getField() + "_fra", hitCount.getField() + "_til" };
                String[] values = new String[]{ terms[0].trim(), terms[1].trim() };
                retVal = replaceParams(query, keys, values);
            }
        }
        return retVal;
    }

    /**
     * Get URL to previous page
     * @param query
     * @param result
     * @return
     */
    public String getPrevPageUrl(SearchServiceQuery query, SearchServiceResultImpl result) {
        String prevPageUrl = prevPage(query, result.getCurrentPage());
        return prevPageUrl == null || "".equals(prevPageUrl) ? null : prevPageUrl;
    }

    /**
     * Get URL to next page
     * @param query
     * @param result
     * @return
     */
    public String getNextPageUrl(SearchServiceQuery query, SearchServiceResultImpl result) {
        String nextPageUrl = nextPage(query, result.getCurrentPage(), query.getHitsPerPage(), result.getSearchResult().getNumberOfHits());
        return nextPageUrl == null || "".equals(nextPageUrl) ? null : nextPageUrl;
    }

    /**
     * Generate URL for navigation to pages in search result
     * @param urlPrefix
     * @param query - query
     * @param result - result
     * @return - list of URLs
     */
    public LinkedHashMap<String, String> createPageUrls(String urlPrefix, SearchServiceQuery query, SearchServiceResultImpl result) {
        LinkedHashMap<String, String> pageUrls = new LinkedHashMap<String, String>();
        int currentpage = result.getCurrentPage() + 1;
        int startPage = ((currentpage / 10) * 10) + 1;
        int endPage = startPage + 9;
        if (endPage * query.getHitsPerPage() >= result.getSearchResult().getNumberOfHits()) {
            endPage = (result.getSearchResult().getNumberOfHits() - 1) / query.getHitsPerPage();
            endPage++;
        }
        if (startPage > 1) {
            startPage--;
        }
        for (int i = startPage; i <= endPage; i++) {
            String[] keys = new String[]{ SearchServiceQuery.METAPARAM_PAGE };
            String[] values = new String[]{ "" + (i-1) };
            pageUrls.put("" + i, urlPrefix + replaceParams(query, keys, values));
        }
        return pageUrls;
    }

/**
     * Get links for drilldown
     * @param urlPrefix
     * @param query
     * @param result
     * @return
     */
    public Map<String, String> getHitCountUrls(String urlPrefix, SearchServiceQuery query, SearchServiceResult result) {
        Map<String, String> hitCounts = new HashMap<String, String>();
        SearchServiceResultImpl serviceResult = (SearchServiceResultImpl)result;

        if (serviceResult.getSearchResult() instanceof SearchResultExtendedImpl) {
            SearchResultExtendedImpl sr = (SearchResultExtendedImpl)serviceResult.getSearchResult();

            for (HitCount hitCount : sr.getHitCounts()) {
                if (Fields.LAST_MODIFIED.equals(hitCount.getField())) {
                    String url = createLastModifiedUrl(query, hitCount);
                    if (url != null) {
                        String name = hitCount.getField() + "." + hitCount.getTerm();
                        hitCounts.put(name, urlPrefix + url);
                    }
                } else {
                    if (query.getStringParam(hitCount.getField()) == null || Fields.CONTENT_PARENTS.equals(hitCount.getField())) {
                        String name = hitCount.getField() + "." + hitCount.getTerm();
                        hitCounts.put(name, urlPrefix + replaceParam(query, hitCount.getField(), hitCount.getTerm()));
                    }
                }
            }
        }
        return hitCounts;
    }    
}
