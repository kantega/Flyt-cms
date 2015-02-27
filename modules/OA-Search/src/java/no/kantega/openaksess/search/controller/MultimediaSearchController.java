package no.kantega.openaksess.search.controller;

import no.kantega.openaksess.search.model.AutocompleteMultimedia;
import no.kantega.openaksess.search.provider.transformer.MultimediaTransformer;
import no.kantega.openaksess.search.query.AksessSearchContextCreator;
import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.controls.AksessController;
import no.kantega.search.api.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.web.bind.ServletRequestUtils.*;

@Controller
@RequestMapping("/multimediasearch")
public class MultimediaSearchController implements AksessController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    AksessSearchContextCreator aksessSearchContextCreator;

    @Autowired
    Searcher searcher;

    @Value("${oa.usefuzzysearch:false}")
    private boolean useFuzzySearch;

    /**
     * How many suggestions should autocomplete give?
     */
    @Value("${multimediasearch.autocomplete.suggestions:15}")
    private int multimediaAutocompleteSuggestions;

    private List<String> facetQueries = emptyList();
    private List<String> facetFields = emptyList();

    @RequestMapping("/search")
    public @ResponseBody
    Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        String query = getQuery(request);
        int numberOfHits = 0;
        List<Multimedia> mediaList = new ArrayList<>();

        if (isNotEmpty(query)) {
            SearchResponse searchResponse = performSearch(request, query);
            numberOfHits = searchResponse.getNumberOfHits().intValue();
            mediaList = getListFromSearchResponse(request, searchResponse);
        }

        model.put("numberOfHits", numberOfHits);
        model.put("mediaList", mediaList);
        return model;
    }

    @RequestMapping("/autocomplete")
    public @ResponseBody List<AutocompleteMultimedia> autocomplete(HttpServletRequest request) {
        List<AutocompleteMultimedia> autocompleteList = new ArrayList<>(multimediaAutocompleteSuggestions);
        String term = request.getParameter("term");
        if (isNotBlank(term)) {
            AksessSearchContext searchContext = aksessSearchContextCreator.getSearchContext(request);

            SearchResponse searchResponse = searcher.search(getAutocompleteQuery(term, searchContext));
            List<Multimedia> multimediaList = getListFromSearchResponse(request, searchResponse);
            for (Multimedia m : multimediaList) {
                autocompleteList.add(new AutocompleteMultimedia(m, request));
            }
        }

        return autocompleteList;
    }

    private SearchQuery getAutocompleteQuery(String term, AksessSearchContext searchContext) {
        String queryString = "(title_no:" + term + "*) OR (altTitle_no:" + term + "*)";
        SearchQuery query = new SearchQuery(searchContext, queryString, "indexedContentType:" + MultimediaTransformer.HANDLED_DOCUMENT_TYPE);
        query.setResultsPerPage(multimediaAutocompleteSuggestions);
        query.setQueryType(QueryType.Lucene);
        return query;
    }

    private List<Multimedia> getListFromSearchResponse(HttpServletRequest request, SearchResponse searchResponse) {
        List<Multimedia> multimediaList = new ArrayList<>();

        List<GroupResultResponse> groupResultResponses = searchResponse.getGroupResultResponses();
        MultimediaService multimediaService = new MultimediaService(request);
        for (GroupResultResponse groupResultResponse : groupResultResponses) {
            List<SearchResult> searchResults = groupResultResponse.getSearchResults();
            for (SearchResult searchResult : searchResults) {
                int id = searchResult.getId();
                try {
                    multimediaList.add(multimediaService.getMultimedia(id));
                } catch (Exception e) {
                    log.error("Error getting multimedia for id " + id, e);
                }
            }
        }
        return multimediaList;
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

    public SearchQuery customizeQuery(SearchQuery searchQuery, AksessSearchContext searchContext, HttpServletRequest request){
        return searchQuery;
    }

    public String getDescription() {
        return "Performs search for Aksess content";
    }
}
