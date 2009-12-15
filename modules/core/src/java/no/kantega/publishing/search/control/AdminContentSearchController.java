/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.search.control;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.commons.log.Log;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.search.control.util.QueryStringGenerator;
import no.kantega.publishing.search.service.SearchService;
import no.kantega.publishing.search.service.SearchServiceQuery;
import no.kantega.publishing.search.service.SearchServiceResultImpl;
import no.kantega.publishing.common.Aksess;
import no.kantega.search.index.Fields;
import no.kantega.search.query.hitcount.DateHitCountQuery;
import no.kantega.search.query.hitcount.HitCountQueryDefaultImpl;
import no.kantega.search.query.hitcount.HitCountQuery;

import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class AdminContentSearchController implements Controller, InitializingBean {

    private String view;
    private SearchService searchService;
    private String queryStringEncoding = "iso-8859-1"; // Must be iso-8859-1 in Tomcat, utf-8 in Jetty

    private QueryStringGenerator queryStringGenerator;
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        Map<String, Object> model = performSearches(request);
        model.put("totalTime", System.currentTimeMillis() - start);
        return new ModelAndView(view, model);
    }

    private Map<String, Object> performSearches(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();

        SearchServiceQuery query = new SearchServiceQuery(request);

        // Add hit counts
        addHitCountQueries(query);

        String urlPrefix = "?";

        // Perform search

        // SearchServiceResultImpl should be renamed or something in future
        SearchServiceResultImpl result = (SearchServiceResultImpl)searchService.search(query);
        model.put("result", result);

        Map<String, Object> links = new HashMap<String, Object>();
        // QueryStrings for drilldown
        links.put("hitcounts", queryStringGenerator.getHitCountUrls(urlPrefix, query, result));

        // QueryString to previous and next page
        String prevPageUrl = queryStringGenerator.getPrevPageUrl(query, result);
        if (prevPageUrl != null) {
            links.put("prevPageUrl", urlPrefix + prevPageUrl);
        }
        String nextPageUrl = queryStringGenerator.getNextPageUrl(query, result);
        if (nextPageUrl != null) {
            links.put("nextPageUrl", urlPrefix + nextPageUrl);
        }

        // QueryStrings for pages
        links.put("pageUrls", queryStringGenerator.createPageUrls(urlPrefix, query, result));
        model.put("links", links);

        return model;
    }


    /**
     * Creates queries for hit counts, eg hits per category
     * @param query - SearchServiceQuery
     */
    protected void addHitCountQueries(SearchServiceQuery query) {
        String[] docTypes = HitCountHelper.getDocumentTypes();
        if (docTypes.length > 0) {
            // Document types
            HitCountQuery hitCountDocType = new HitCountQueryDefaultImpl(Fields.DOCUMENT_TYPE_ID, HitCountHelper.getDocumentTypes(), true);
            query.addHitCountQuery(hitCountDocType);
        }

        // Modified date
        query.addHitCountQuery(new DateHitCountQuery(Fields.LAST_MODIFIED, 5, null, null));
    }


    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setQueryStringEncoding(String queryStringEncoding) {
        this.queryStringEncoding = queryStringEncoding;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void afterPropertiesSet() throws Exception {
        try {
            queryStringEncoding = Aksess.getConfiguration().getString("querystring.encoding", queryStringEncoding);
            queryStringGenerator = new QueryStringGenerator(queryStringEncoding);
        } catch (ConfigurationException e) {
            Log.error(this.getClass().getName(), e, null, null);
        }
    }
}

