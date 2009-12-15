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

package no.kantega.publishing.search.control;

import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.search.service.*;
import no.kantega.publishing.search.control.util.QueryStringGenerator;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.Aksess;
import no.kantega.search.query.hitcount.HitCountQuery;
import no.kantega.search.query.hitcount.HitCountQueryDefaultImpl;
import no.kantega.search.query.hitcount.DateHitCountQuery;
import no.kantega.search.index.Fields;
import no.kantega.commons.log.Log;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import org.springframework.beans.factory.InitializingBean;

/**
 *
 */
public class ContentSearchController implements AksessController, InitializingBean {

    private String description = "Performs search for Aksess content";

    private SearchService searchService;
    private String queryStringEncoding = "iso-8859-1"; // Must be iso-8859-1 in Tomcat, utf-8 in Jetty

    private boolean hitCountDocumentType = true;
    private boolean hitCountParents = true;
    private boolean hitCountLastModified = true;

    private QueryStringGenerator queryStringGenerator;
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();

        Map<String, Object> model = performSearches(request);
        model.put("totalTime", System.currentTimeMillis() - start);
        return model;
    }

    private Map<String, Object> performSearches(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();

        Content content = (Content)request.getAttribute("aksess_this");

        SearchServiceQuery query = new SearchServiceQuery(request);
        if (content != null) {
            query.putSearchParam("thisId", "" + content.getAssociation().getId());
            query.putSearchParam(SearchServiceQuery.PARAM_SITE_ID, "" + content.getAssociation().getSiteId());
        }

        // Add hit counts
        addHitCountQueries(query, request, content);

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
     * @param request - HttpServletRequest
     * @param content - Content current page
     */
    protected void addHitCountQueries(SearchServiceQuery query, HttpServletRequest request, Content content) {
        if (hitCountDocumentType) {
            // Document types
            HitCountQuery hitCountDocType = new HitCountQueryDefaultImpl(Fields.DOCUMENT_TYPE_ID, HitCountHelper.getDocumentTypes(), true);
            query.addHitCountQuery(hitCountDocType);
        }

        if (hitCountParents) {
            // Parents
            int siteId = 1;
            if (content != null) {
                siteId = content.getAssociation().getSiteId();
            }
            HitCountQuery hitCountParents = new HitCountQueryDefaultImpl(Fields.CONTENT_PARENTS, HitCountHelper.getParents(siteId, request), true);
            query.addHitCountQuery(hitCountParents);
        }

        if (hitCountLastModified) {
            // Modified date
            query.addHitCountQuery(new DateHitCountQuery(Fields.LAST_MODIFIED, 5, null, null));
        }
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setHitCountDocumentType(boolean hitCountDocumentType) {
        this.hitCountDocumentType = hitCountDocumentType;
    }

    public void setHitCountParents(boolean hitCountParents) {
        this.hitCountParents = hitCountParents;
    }

    public void setHitCountLastModified(boolean hitCountLastModified) {
        this.hitCountLastModified = hitCountLastModified;
    }

    public void setQueryStringEncoding(String queryStringEncoding) {
        this.queryStringEncoding = queryStringEncoding;
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
