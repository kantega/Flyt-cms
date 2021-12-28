package no.kantega.openaksess.search.searchlog.dao;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchLogDao {
    void registerSearch(String queryString, List<String> exactQuery, int siteId, Number numberOfHits);
    int getSearchCountForPeriod(LocalDateTime after, LocalDateTime before, int siteId);
    List<QueryStatItem> getMostPopularQueries(int siteId, Integer numberOfRows);
    List<QueryStatItem> getQueriesWithLeastHits(int siteId, Integer numberOfRows);
    List<QueryStatItem> getMostPopularQueries(int siteId, LocalDateTime after, LocalDateTime before, Integer numberOfRows);
    List<QueryStatItem> getQueriesWithLeastHits(int siteId, LocalDateTime after, LocalDateTime before, Integer numberOfRows);
}
