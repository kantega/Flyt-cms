package no.kantega.search.api.search;

import java.util.*;

/**
 * The response returned as result of a SearchQuery
 * @see SearchQuery
 */
public class SearchResponse {
    private final Number numberOfHits;
    private int queryTime;
    private List<GroupResultResponse> groupResultResponses;
    private SearchQuery query;
    private List<String> spellSuggestions;
    private Map<String, Collection<FacetResult>> facets;
    private final String ALL_DOCUMENTS_GROUPNAME = "all";
    private String debugInformation;
    private List<AbstractMap.SimpleEntry<String, Integer>> collatedSuggestions;

    public SearchResponse(SearchQuery query, long numberOfHits, int queryTime, List<SearchResult> searchResults) {
        this.query = query;
        this.numberOfHits = numberOfHits;
        this.queryTime = queryTime;
        groupResultResponses = Arrays.asList(new GroupResultResponse(ALL_DOCUMENTS_GROUPNAME, numberOfHits, searchResults));

    }

    public SearchResponse(SearchQuery query, int matches, int queryTime, List<GroupResultResponse> groupResultResponses) {
        this.query = query;
        this.numberOfHits = matches;
        this.queryTime = queryTime;
        this.groupResultResponses = groupResultResponses;
    }

    public List<AbstractMap.SimpleEntry<String, Integer>> getCollatedSuggestions() {
        return collatedSuggestions;
    }

    public void setCollatedSuggestions(List<AbstractMap.SimpleEntry<String, Integer>> collatedSuggestions) {
        this.collatedSuggestions = collatedSuggestions;
    }

    public Number getNumberOfHits() {
         return numberOfHits;
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

    /**
     * @return a list containing the actual hits of the query if this
     * result is not a result of a grouped query, else an empty list.
     */
    public List<SearchResult> getSearchHits() {
        List<SearchResult> documentHits = Collections.emptyList();
        for(GroupResultResponse groupResultResponse : groupResultResponses){
            if(groupResultResponse.getGroupValue() != null && groupResultResponse.getGroupValue().equals(ALL_DOCUMENTS_GROUPNAME)){
                documentHits = groupResultResponse.getSearchResults();
            }
        }

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
        return (int) Math.ceil(numberOfHits.doubleValue() / query.getResultsPerPage());
    }

    public void setFacets(Map<String,Collection<FacetResult>> facets) {
        this.facets = facets;
    }

    /**
     * The result of using a facetQuery, facetField or dateRangeFacet will result in entries in the returned map.
     * facetQueries will be split based on the field queries, such that it will have a single entry in the returned map.
     * @return a map where the key is the name of the indexed field in which the facetes are created. Each entry is
     * a list of facets with a value and the document count for the facet.
     */
    public Map<String, Collection<FacetResult>> getFacets() {
        if(facets == null) return Collections.emptyMap();

        return facets;
    }

    public List<GroupResultResponse> getGroupResultResponses() {
        return groupResultResponses;
    }

    public void setGroupResultResponses(List<GroupResultResponse> groupResultResponses) {
        this.groupResultResponses = groupResultResponses;
    }

    public void setDebugInformation(String debugInformation) {
        this.debugInformation = debugInformation;
    }

    public String getDebugInformation() {
        return debugInformation;
    }
}
