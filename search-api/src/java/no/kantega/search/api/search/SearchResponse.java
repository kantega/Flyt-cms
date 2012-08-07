package no.kantega.search.api.search;

import com.google.gdata.util.common.base.Pair;

import java.util.Collection;
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
    private Map<String, Collection<Pair<String, Number>>> facets;

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

    public int getCurrentPage() {
        return query.getPageNumber();
    }

    public int getNumberOfPages() {
        return (int) Math.ceil(numFound / query.getResultsPerPage());
    }

    public void setFacets(Map<String,Collection<Pair<String, Number>>> facets) {
        this.facets = facets;
    }

    /**
     * The result of using a facetQuery, facetField or dateRangeFacet will result in entries in the returned map.
     * facetQueries will be split based on the field queries, such that it will have a single entry in the returned map.
     * @return a map where the key is the name of the indexed field in which the facetes are created. Each entry is
     * a list of facets with a value and the document count for the facet.
     */
    public Map<String, Collection<Pair<String, Number>>> getFacets() {
        if(facets == null) return Collections.emptyMap();

        return facets;
    }
}
