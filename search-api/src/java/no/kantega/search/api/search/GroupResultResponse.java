package no.kantega.search.api.search;

import java.util.List;

public class GroupResultResponse {
    private final String groupValue;
    private final Number numFound;
    private final List<SearchResult> searchResults;

    public GroupResultResponse(String groupValue, Number numFound, List<SearchResult> searchResults) {
        this.groupValue = groupValue;
        this.numFound = numFound;
        this.searchResults = searchResults;
    }

    public String getGroupValue() {
        return groupValue;
    }

    public Number getNumFound() {
        return numFound;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }
}
