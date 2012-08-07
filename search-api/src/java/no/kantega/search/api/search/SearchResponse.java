package no.kantega.search.api.search;

import com.google.gdata.util.common.base.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The response returned as result of a SearchQuery
 * @see SearchQuery
 */
public class SearchResponse {
    private final long numFound;
    private int queryTime;
    private SearchQuery query;
    private List<SearchResult> documentHits;
    private List<String> spellSuggestions;
    private Map<String, List<Pair<String, Long>>> facetFields;
    private Map<String, List<Pair<String, Integer>>> rangeFacet;
    private List<Pair<String, Integer>> facetQueries;

    public SearchResponse(SearchQuery query, long numFound, int queryTime, List<SearchResult> searchResults) {
        this.query = query;
        this.numFound = numFound;
        this.queryTime = queryTime;
        this.documentHits = searchResults;

    }

    public long getNumberOfHits() {
         return numFound;
    }

    /**
     * @return the time in ms the search server used processing the query
     */
    public int getQueryTime() {
        return queryTime;
    }

    /**
     * @return the query this SearchResponse is a response of.
     */
    public SearchQuery getQuery() {
        return query;
    }

    public void setDocumentHits(List<SearchResult> documentHits) {
        this.documentHits = documentHits;
    }

    /**
     * @return a list containing the actual hits of the query
     */
    public List<SearchResult> getDocumentHits() {
        return documentHits;
    }

    /**
     * @return Spellsuggestions suggested by the search server.
     */
    public List<String> getSpellSuggestions() {
        if(spellSuggestions == null) return Collections.emptyList();

        return spellSuggestions;
    }

    public void setSpellSuggestions(List<String> spellSuggestions) {
        this.spellSuggestions = spellSuggestions;
    }

    public void setFacetFields(Map<String,List<Pair<String, Long>>> facetFields) {
        this.facetFields = facetFields;
    }

    /**
     * @return a map where the key is the name of the indexed field in which the facetes are created. Each entry is
     * a list of facetes with a value and the document count for the facet.
     */
    public Map<String, List<Pair<String, Long>>> getFacetFields() {
        return facetFields;
    }

    public void setRangeFacet(Map<String,List<Pair<String, Integer>>> rangeFacet) {
        this.rangeFacet = rangeFacet;
    }

    /**
     * @return the result of adding dateRange for the SearchQuery.
     */
    public Map<String, List<Pair<String, Integer>>> getRangeFacet() {
        if(rangeFacet == null){
            rangeFacet = Collections.emptyMap();
        }
        return rangeFacet;
    }

    public void setFacetQueries(List<Pair<String, Integer>> facetQueries) {
        this.facetQueries = facetQueries;
    }

    /**
     * @return a list of Pair<String, Integer> containing the value of each facet query, and the resulting document count.
     */
    public List<Pair<String, Integer>> getFacetQueries() {
        if(facetQueries == null){
            return Collections.emptyList();
        }
        return facetQueries;
    }

    public int getCurrentPage() {
        return query.getPageNumber();
    }
}
