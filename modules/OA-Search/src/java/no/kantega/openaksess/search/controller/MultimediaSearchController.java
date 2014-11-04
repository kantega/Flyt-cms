package no.kantega.openaksess.search.controller;

import no.kantega.openaksess.search.provider.transformer.MultimediaTransformer;
import no.kantega.openaksess.search.query.AksessSearchContextCreator;
import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.controls.AksessController;
import no.kantega.search.api.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.springframework.web.bind.ServletRequestUtils.*;

@Controller
@RequestMapping("/oasearch")
public class MultimediaSearchController implements AksessController {

    @Autowired
    AksessSearchContextCreator aksessSearchContextCreator;

    @Autowired
    Searcher searcher;

    @Value("${oa.usefuzzysearch:false}")
    private boolean useFuzzySearch;

    private String searchResponseModelKey = "searchResponse";
    private boolean includePaginationLinks = true;
    private List<String> facetQueries = emptyList();
    private List<String> facetFields = emptyList();

    @RequestMapping("/multimediasearch")
    public @ResponseBody
    Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        String query = getQuery(request);
        if (isNotEmpty(query)) {
            SearchResponse searchResponse = performSearch(request, query);
            model.put(searchResponseModelKey, searchResponse);

            if(includePaginationLinks){
                addLinks(model, searchResponse);
            }
        } else {
            model.put(searchResponseModelKey, emptyResponse(request));
        }

        return model;
    }

    private SearchResponse performSearch(HttpServletRequest request, String query) {
        AksessSearchContext searchContext = aksessSearchContextCreator.getSearchContext(request);
        return searcher.search(getSearchQuery(request, query, searchContext));
    }

    private SearchQuery getSearchQuery(HttpServletRequest request, String query, AksessSearchContext searchContext) {
        SearchQuery searchQuery = new SearchQuery(searchContext, query, getFilterQueries(request));
        searchQuery.setFuzzySearch(useFuzzySearch);
        searchQuery.setPageNumber(getIntParameter(request, "page", 0));
        searchQuery.setResultsPerPage(getIntParameter(request, "resultsprpage", SearchQuery.DEFAULT_RESULTS_PER_PAGE));
        searchQuery.setOffset(getIntParameter(request, "offset", 0));

        addFacetFields(request, searchQuery, searchContext);

        searchQuery.setGroupField(getGroupField(request));
        searchQuery.setGroupQueries(getGroupQueries(request));
        searchQuery.setBoostByPublishDate(getBoostByPublishDate(request));
        return customizeQuery(searchQuery, searchContext, request);
    }

    /**
     * @param request - Current HttpServletRequest.
     * @return whether boostByPublishDate should be true or false for the current search query.
     * default returns the value of request parameter boostByPublishDate, or false if absent.
     */
    public boolean getBoostByPublishDate(HttpServletRequest request) {
        return getBooleanParameter(request, "boostByPublishDate", false);
    }

    /**
     * @param request - Current HttpServletRequest.
     * @return the field to group results by.
     */
    public String getGroupField(HttpServletRequest request) {
        return getStringParameter(request, "groupfield", null);
    }

    /**
     * @param request - Current HttpServletRequest.
     * @return the group queries to add to the current search query.
     */
    public String[] getGroupQueries(HttpServletRequest request) {
        return getStringParameters(request, "groupquery");
    }

    private void addFacetFields(HttpServletRequest request, SearchQuery searchQuery, AksessSearchContext searchContext) {
        boolean excludeDefaultFacets = getBooleanParameter(request, "excludedefaultfacets", false);
        searchQuery.setFacetFields(getFacetFields(request, searchContext, excludeDefaultFacets));

        if (!excludeDefaultFacets) {
            searchQuery.setFacetQueries(facetQueries);
        }
    }

    /**
     * Get the facet fields to be used for the current request.
     * @param request - Current HttpServletRequest.
     * @param excludeDefaultFacets true if the fields defined in the field facetFields should be included in
     *                             the facesFields-list.
     * @return all facetfields to be applied.
     */
    public List<String> getFacetFields(HttpServletRequest request, AksessSearchContext searchContext, boolean excludeDefaultFacets) {
        List<String> fields = excludeDefaultFacets? Collections.<String>emptyList() : facetFields;
        String parameterfacetFields = getStringParameter(request, "facetFields", null);
        if(parameterfacetFields != null){
            fields = Arrays.asList(parameterfacetFields.split(","));
        }
        return fields;
    }

    /**
     * Get the filter queries to be used for the current request.
     * @param request - Current HttpServletRequest.
     * @return all filter queries to be applied.
     */
    public List<String> getFilterQueries(HttpServletRequest request) {
        List<String> filterQueries = new ArrayList<>(Arrays.asList(getStringParameters(request, QueryStringGenerator.FILTER_PARAM)));
        filterQueries.add("indexedContentType:" + MultimediaTransformer.HANDLED_DOCUMENT_TYPE);
        return filterQueries;
    }

    public String getQuery(HttpServletRequest request) {
        return getStringParameter(request, QueryStringGenerator.QUERY_PARAM, "");
    }

    private void addLinks(Map<String, Object> model, SearchResponse searchResponse) {
        Map<String, Object> links = new HashMap<>();
        model.put("links", links);
        int currentPage = searchResponse.getCurrentPage();
        if (currentPage > 0) {
            String prevPageUrl = QueryStringGenerator.getPrevPageUrl(searchResponse.getQuery(), currentPage, searchResponse.getQuery().isAppendFiltersToPageUrls());
            links.put("prevPageUrl", prevPageUrl);
        }

        int numberOfPages = searchResponse.getNumberOfPages();
        if (currentPage < (numberOfPages - 1)) {
            String nextPageUrl = QueryStringGenerator.getNextPageUrl(searchResponse.getQuery(), currentPage, searchResponse.getQuery().isAppendFiltersToPageUrls());
            links.put("nextPageUrl", nextPageUrl);
        }

        if (numberOfPages > 1) {
            links.put("pageUrls", QueryStringGenerator.getPageUrls(searchResponse, currentPage, searchResponse.getQuery().isAppendFiltersToPageUrls()));
        }
    }

    public SearchQuery customizeQuery(SearchQuery searchQuery, AksessSearchContext searchContext, HttpServletRequest request){
        return searchQuery;
    }

    public String getDescription() {
        return "Performs search for Aksess content";
    }

    private SearchResponse emptyResponse(HttpServletRequest request) {
        return new SearchResponse(new SearchQuery(aksessSearchContextCreator.getSearchContext(request), ""), 0L, 0, Collections.<SearchResult>emptyList());
    }

    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }

    public void setFacetFields(List<String> facetFields) {
        this.facetFields = facetFields;
    }
}
