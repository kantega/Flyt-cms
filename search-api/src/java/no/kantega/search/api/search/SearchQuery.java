package no.kantega.search.api.search;

public class SearchQuery {
    private final SearchContext searchContext;
    private final String originalQuery;
    private final String fullQuery;
    private boolean highlightSearchResultDescription;

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
}
