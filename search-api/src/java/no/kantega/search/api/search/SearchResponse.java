package no.kantega.search.api.search;

import java.util.List;

public class SearchResponse {
    private int numberOfHits;
    private int queryTime;
    private String query;
    private List<SearchResult> documentHits;

    public int getNumberOfHits() {
        return numberOfHits;
    }

    public void setNumberOfHits(int numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public void setQueryTime(int queryTime) {
        this.queryTime = queryTime;
    }

    public int getQueryTime() {
        return queryTime;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setDocumentHits(List<SearchResult> documentHits) {
        this.documentHits = documentHits;
    }

    public List<SearchResult> getDocumentHits() {
        return documentHits;
    }
}
