package no.kantega.search.api.search;

import com.google.gdata.util.common.base.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchResponse {
    private int queryTime;
    private SearchQuery query;
    private List<SearchResult> documentHits;
    private List<String> spellSuggestions;
    private Map<String, List<Pair<String, Long>>> facetFields;
    private Map<String, List<Pair<String, Integer>>> rangeFacet;
    private List<Pair<String, Integer>> facetQuery;

    public int getNumberOfHits() {
        if (documentHits != null) {
            return documentHits.size();
        } else {
            return 0;
        }
    }

    public void setQueryTime(int queryTime) {
        this.queryTime = queryTime;
    }

    public int getQueryTime() {
        return queryTime;
    }

    public SearchQuery getQuery() {
        return query;
    }

    public void setQuery(SearchQuery query) {
        this.query = query;
    }

    public void setDocumentHits(List<SearchResult> documentHits) {
        this.documentHits = documentHits;
    }

    public List<SearchResult> getDocumentHits() {
        return documentHits;
    }

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

    public Map<String, List<Pair<String, Long>>> getFacetFields() {
        return facetFields;
    }

    public void setRangeFacet(Map<String,List<Pair<String, Integer>>> rangeFacet) {
        this.rangeFacet = rangeFacet;
    }

    public Map<String, List<Pair<String, Integer>>> getRangeFacet() {
        if(rangeFacet == null){
            rangeFacet = Collections.emptyMap();
        }
        return rangeFacet;
    }

    public void setFacetQuery(List<Pair<String, Integer>> facetQuery) {
        this.facetQuery = facetQuery;
    }

    public List<Pair<String, Integer>> getFacetQuery() {
        if(facetQuery == null){
            facetQuery = Collections.emptyList();
        }
        return facetQuery;
    }
}
