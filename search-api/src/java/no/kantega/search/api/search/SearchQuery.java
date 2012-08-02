package no.kantega.search.api.search;

import java.util.Collections;
import java.util.List;

/**
 * Specifies a query to perform a search.
 * @see SearchResponse
 */
public class SearchQuery {
    private final SearchContext searchContext;
    private final String originalQuery;
    private final String fullQuery;
    private boolean highlightSearchResultDescription;
    private List<String> facetFields;
    private List<String> facetQueries;
    private List<DateRange> dateRangeFacets;

    /**
     * @param searchContext - The context in which this query is executed
     * @param originalQuery - The original query, e.g "kino"
     * @param fullQuery - The full query built to further specify the query. e.g. "title:kino +kommune:trondheim".
     *                  This should follow the Solr query syntax,
     *                  http://wiki.apache.org/solr/SolrQuerySyntax and
     *                  http://lucene.apache.org/core/3_6_0/queryparsersyntax.html
     *
     */
    public SearchQuery(SearchContext searchContext, String originalQuery, String fullQuery) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
        this.fullQuery = fullQuery;
    }

    public String getFullQuery() {
        return fullQuery;
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
        return facetFields != null || facetQueries != null || dateRangeFacets != null;
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

    public List<DateRange> getDateRangeFacets() {
        if(dateRangeFacets == null){
            dateRangeFacets = Collections.emptyList();
        }
        return dateRangeFacets;
    }

    /**
     * @param dateRangeFacets which should be used for the SearchResponse
     */
    public void setDateRangeFacets(List<DateRange> dateRangeFacets) {
        this.dateRangeFacets = dateRangeFacets;
    }

    /**
     * @param facetQueries which should be used for the SearchResponse.
     *                     http://wiki.apache.org/solr/SimpleFacetParameters#Facet_Fields_and_Facet_Queries
     */
    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }
}
