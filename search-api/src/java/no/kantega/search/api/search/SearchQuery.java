package no.kantega.search.api.search;

public class SearchQuery {
    private final SearchContext searchContext;
    private final String originalQuery;
    private String fullQuery;

    public SearchQuery(SearchContext searchContext, String originalQuery) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
    }

    public String getFullQuery() {
        return fullQuery;
    }

    public void setFullQuery(String fullQuery) {
        this.fullQuery = fullQuery;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public SearchContext getSearchContext() {
        return searchContext;
    }
}
