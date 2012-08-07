package no.kantega.openaksess.search.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gdata.util.common.base.Pair;
import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.Searcher;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Performs search for Aksess content.
 */
public class ContentSearchController implements AksessController {

    private Searcher searcher;

    private boolean searchAllSites = false;
    private SiteCache siteCache;
    private boolean showOnlyVisibleContent = true;
    private boolean showOnlyPublishedContent = true;
    private List<String> facetFields = Arrays.asList("documentTypeName", "location");

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Object> model = new HashMap<String, Object>();
        String query = getQuery(request);
        if (isNotEmpty(query)) {
            SearchResponse searchResponse = performSearch(request, query);
            model.put("searchResponse", searchResponse);

            String urlPrefix = "?";
            Map<String, Object> links = new HashMap<String, Object>();

            links.put("facetUrls", getFacetUrls(urlPrefix, searchResponse));
            // TODO Facet names
            String prevPageUrl = QueryStringGenerator.getPrevPageUrl(searchResponse.getQuery(), searchResponse.getCurrentPage());
            if (isNotBlank(prevPageUrl)) {
                links.put("prevPageUrl", urlPrefix + prevPageUrl);
            }
            String nextPageUrl = QueryStringGenerator.getNextPageUrl(searchResponse.getQuery(), searchResponse.getCurrentPage());
            if (isNotBlank(nextPageUrl)) {
                links.put("nextPageUrl", urlPrefix + nextPageUrl);
            }
            links.put("pageUrls", QueryStringGenerator.getPageUrls(searchResponse, searchResponse.getCurrentPage(), urlPrefix));
            model.put("links", links);
        }

        return model;
    }

    private Multimap<String, String> getFacetUrls(String urlPrefix, SearchResponse searchResponse) {
        Multimap<String,String> facetUrls = ArrayListMultimap.create();
        for (Map.Entry<String, List<Pair<String, Long>>> facetFieldEntry : searchResponse.getFacetFields().entrySet()) {
            for(Pair<String, Long> facetFieldValue : facetFieldEntry.getValue()){
                String facetName = facetFieldEntry.getKey();
                facetUrls.put(facetName, urlPrefix + QueryStringGenerator.getFacetUrl(facetName + ":" + facetFieldValue.first, searchResponse));
            }
        }
        for(Pair<String, Integer> facetQuery : searchResponse.getFacetQueries()){
            facetUrls.put(facetQuery.first, urlPrefix + QueryStringGenerator.getFacetUrl(facetQuery.first, searchResponse));
        }
        return facetUrls;
    }

    private SearchResponse performSearch(HttpServletRequest request, String query) {
        AksessSearchContext searchContext = getSearchContext(request);
        return searcher.search(getSearchQuery(request, query, searchContext));
    }

    private SearchQuery getSearchQuery(HttpServletRequest request, String query, AksessSearchContext searchContext) {
        SearchQuery searchQuery = new SearchQuery(searchContext, query, getFilterQueries(request, searchContext));

        searchQuery.setFacetFields(facetFields);

        searchQuery.setFacetQueries(asList(
                "lastModified:[NOW/DAY-7DAYS TO NOW]",
                "lastModified:[NOW/MONTH-1MONTH TO NOW/DAY-7DAYS]",
                "lastModified:[NOW/YEAR-1YEAR TO NOW/MONTH-1MONTH]",
                "lastModified:[NOW/YEAR-3YEARS TO NOW/YEAR-1YEAR]",
                "lastModified:[* TO NOW/YEAR-3YEARS]"));
        return searchQuery;
    }

    private List<String> getFilterQueries(HttpServletRequest request, AksessSearchContext searchContext) {
        List<String> filterQueries = Arrays.asList(ServletRequestUtils.getStringParameters(request, QueryStringGenerator.FILTER_PARAM));

        if(!searchAllSites){
            filterQueries.add("siteId:" + searchContext.getSiteId());
        }
        if(showOnlyVisibleContent){
            filterQueries.add("visibilityStatus:" + ContentVisibilityStatus.getName(ContentVisibilityStatus.ACTIVE));
        }
        if(showOnlyPublishedContent){
            filterQueries.add("contentStatus:" + ContentStatus.getContentStatusAsString(ContentStatus.PUBLISHED));
        }

        return filterQueries;
    }

    private String getQuery(HttpServletRequest request) {
        return ServletRequestUtils.getStringParameter(request, QueryStringGenerator.QUERY_PARAM, "");
    }

    private AksessSearchContext getSearchContext(HttpServletRequest request) {
        return new AksessSearchContext(SecuritySession.getInstance(request), findSiteId(request));
    }

    private int findSiteId(HttpServletRequest request) {
        int retVal = 1;

        Content content = (Content)request.getAttribute("aksess_this");
        if (content != null) {
            retVal = content.getAssociation().getSiteId();
        } else {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                retVal = site.getId();
            }
        }
        return retVal;
    }

    public String getDescription() {
        return "Performs search for Aksess content";
    }

    public void setSearchAllSites(boolean searchAllSites) {
        this.searchAllSites = searchAllSites;
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }

    public void setShowOnlyVisibleContent(boolean showOnlyVisibleContent) {
        this.showOnlyVisibleContent = showOnlyVisibleContent;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public void setShowOnlyPublishedContent(boolean showOnlyPublishedContent) {
        this.showOnlyPublishedContent = showOnlyPublishedContent;
    }
}
