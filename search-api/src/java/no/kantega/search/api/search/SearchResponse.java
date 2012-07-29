package no.kantega.search.api.search;

import java.util.List;

public class SearchResponse {
    private int queryTime;
    private SearchQuery query;
    private List<SearchResult> documentHits;

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
}
