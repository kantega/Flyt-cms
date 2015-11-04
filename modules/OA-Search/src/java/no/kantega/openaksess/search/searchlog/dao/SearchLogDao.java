package no.kantega.openaksess.search.searchlog.dao;

import java.util.Date;
import java.util.List;

public interface SearchLogDao {
    void registerSearch(String queryString, List<String> exactQuery, int siteId, Number numberOfHits);
    int getSearchCountForPeriod(Date after, Date before, int siteId);
    List<QueryStatItem> getMostPopularQueries(int siteId);
    List<QueryStatItem> getQueriesWithLeastHits(int siteId);
}
