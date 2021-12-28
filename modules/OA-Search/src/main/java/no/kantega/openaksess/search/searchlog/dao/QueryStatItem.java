package no.kantega.openaksess.search.searchlog.dao;

public class QueryStatItem {
    private final String query;
    private final int numberOfSearches;
    private final long numberOfHits;

    public QueryStatItem(String query, int numberOfSearches, long numberOfHits) {
        this.query = query;
        this.numberOfSearches = numberOfSearches;
        this.numberOfHits = numberOfHits;
    }

    public String getQuery() {
        return query;
    }

    public int getNumberOfSearches() {
        return numberOfSearches;
    }

    public double getNumberOfHits() {
        return numberOfHits;
    }
}
