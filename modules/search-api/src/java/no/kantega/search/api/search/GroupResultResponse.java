package no.kantega.search.api.search;

import java.util.List;

public class GroupResultResponse {
    private final String groupValue;
    private final Number numFound;
    private final List<SearchResult> searchResults;

    /**
     * @param groupValue - the value of this group, either field or query.
     * @param numFound - number of results for this group.
     * @param searchResults - list of the actuall results.
     */
    public GroupResultResponse(String groupValue, Number numFound, List<SearchResult> searchResults) {
        this.groupValue = groupValue;
        this.numFound = numFound;
        this.searchResults = searchResults;
    }

    /**
     * @return the value of this group, either field or query.
     */
    public String getGroupValue() {
        return groupValue;
    }

    /**
     * @return number of results for this group.
     */
    public Number getNumFound() {
        return numFound;
    }

    /**
     * @return list of the actuall results.
     */
    public List<SearchResult> getSearchResults() {
        return searchResults;
    }
}
