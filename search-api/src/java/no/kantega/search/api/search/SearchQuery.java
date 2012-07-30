package no.kantega.search.api.search;

import java.util.Collections;
import java.util.List;

public class SearchQuery {
    private final SearchContext searchContext;
    private final String originalQuery;
    private final String fullQuery;
    private boolean highlightSearchResultDescription;
    private List<String> facetFields;
    private List<String> facetQueries;
    private List<DateRange> dateRangeFacets;

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

    public void setDateRangeFacets(List<DateRange> dateRangeFacets) {
        this.dateRangeFacets = dateRangeFacets;
    }


    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }
}
