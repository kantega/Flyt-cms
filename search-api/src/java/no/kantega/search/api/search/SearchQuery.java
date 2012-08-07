package no.kantega.search.api.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Specifies a query to perform a search.
 * @see SearchResponse
 */
public class SearchQuery {
    private final SearchContext searchContext;
    private final String originalQuery;
    private List<String> filterQueries;
    private boolean highlightSearchResultDescription;
    private List<String> facetFields;
    private List<String> facetQueries;
    private Integer resultsPerPage = 50;
    private Integer pageNumber = 0;

    /**
     * Construct an query with a query string which typically comes from the user, and an
     * exact query to restrict/filter the results by.
     * @param searchContext - The context in which this query is executed
     * @param originalQuery - The original query, e.g "kino"
     * @param filterQueries - An exact query built to restrict the super set of documents that
     *                    can be returned, without influencing score.
     *                    e.g. "kommune:trondheim", where kommune is a field, results where only results with
     *                    value "trondheim" in kommune-field is returned.
     *                    This should follow the Solr query syntax,
     *                    http://wiki.apache.org/solr/SolrQuerySyntax and
     *                    http://lucene.apache.org/core/3_6_0/queryparsersyntax.html
     *
     */
    public SearchQuery(SearchContext searchContext, String originalQuery, List<String> filterQueries) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
        this.filterQueries = filterQueries;
    }

    public SearchQuery(SearchContext searchContext, String originalQuery, String... filterQueries) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
        this.filterQueries = Arrays.asList(filterQueries);
    }

    /**
     * Construct an query with a query string which typically comes from the user.
     * @param searchContext - The context in which this query is executed
     * @param originalQuery - The original query, e.g "kino"
     *
     */
    public SearchQuery(SearchContext searchContext, String originalQuery) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
    }

    public List<String> getFilterQueries() {
        return filterQueries;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public SearchContext getSearchContext() {
        return searchContext;
    }

    /**
     * @param highlightSearchResultDescription - set to true if highlighting should be performed.
     */
    public void setHighlightSearchResultDescription(boolean highlightSearchResultDescription) {
        this.highlightSearchResultDescription = highlightSearchResultDescription;
    }

    public boolean isHighlightSearchResultDescription() {
        return highlightSearchResultDescription;
    }

    public boolean useFacet() {
        return facetFields != null || facetQueries != null;
    }

    public List<String> getFacetFields() {
        if(facetFields == null){
            return Collections.emptyList();
        }
        return facetFields;
    }

    /**
     * @param facetFields - The indexed fields facet should be used on.
     */
    public void setFacetFields(List<String> facetFields) {
        this.facetFields = facetFields;
    }

    public List<String> getFacetQueries() {
        if(facetQueries == null){
            return Collections.emptyList();
        }
        return facetQueries;
    }

    /**
     * @param facetQueries which should be used for the SearchResponse.
     *                     http://wiki.apache.org/solr/SimpleFacetParameters#Facet_Fields_and_Facet_Queries
     */
    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }

    public void setResultsPerPage(Integer resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public Integer getResultsPerPage() {
        return resultsPerPage;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }
}
