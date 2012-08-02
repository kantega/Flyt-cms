package no.kantega.openaksess.search.searchlog.dao;

import java.util.Date;
import java.util.List;

public interface SearchLogDao {
    public void registerSearch(String queryString, String exactQuery, int siteId, int numberOfHits);
    public int getSearchCountForPeriod(Date after, Date before, int siteId);
    public List getMostPopularQueries(int siteId);
    public List getQueriesWithLeastHits(int siteId);
}
