package no.kantega.openaksess.search.searchlog.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SearchLogDaoJdbcImpl implements SearchLogDao {

    private NamedParameterJdbcTemplate namedjdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public void registerSearch(String queryString, List<String> exactQuery, int siteId, Number numberOfHits) {
        Map<String, Object> params = new HashMap<>();
        params.put("time", new Timestamp(new Date().getTime()));
        params.put("query", queryString);
        params.put("exact_query", StringUtils.join(exactQuery, ","));
        params.put("site_id", siteId);
        params.put("num_hits", numberOfHits);
        namedjdbcTemplate.update("insert into searchlog (Time, Query, ExactQuery, SiteId, NumberOfHits) VALUES (:time, :query, :exact_query, :site_id, :num_hits)", params);
    }

    public int getSearchCountForPeriod(LocalDateTime after, LocalDateTime before, int siteId)  {
        Map<String, Object> params = new HashMap<>();
        StringBuilder queryString = new StringBuilder("select count(*) from searchlog where siteId=:site_id");
        params.put("site_id", siteId);

        if(after!= null) {
            queryString.append(" and Time >= :aftertime ");
            params.put("aftertime", getTimestamp(after));
        }
        if(before != null) {
            queryString.append(" and Time <= :beforetime ");
            params.put("beforetime", getTimestamp(before));

        }
        return namedjdbcTemplate.queryForObject(queryString.toString(), params, Integer.class);
    }

    public List<QueryStatItem> getMostPopularQueries(int siteId, LocalDateTime after, LocalDateTime before) {
        return getQueryStats("numberofsearches desc", siteId, after, before);
    }

    public List<QueryStatItem> getQueriesWithLeastHits(int siteId, LocalDateTime after, LocalDateTime before) {
        return getQueryStats("numberofhits asc", siteId, after, before);
    }

    public List<QueryStatItem> getMostPopularQueries(int siteId) {
        return getQueryStats("numberofsearches desc", siteId, null, null);
    }

    public List<QueryStatItem> getQueriesWithLeastHits(int siteId) {
        return getQueryStats("numberofhits asc", siteId, null, null);
    }

    private List<QueryStatItem> getQueryStats(final String orderBy, final int siteId, LocalDateTime after, LocalDateTime before) {
        StringBuilder sql = new StringBuilder("select query, count(*) as numberofsearches, avg(numberofhits) as numberofhits from searchlog where siteId=? ");
        if(after != null){
            sql.append(" and Time >= ? ");
        }
        if(before != null){
            sql.append(" and Time <= ? ");
        }
        sql.append("group by query order by ").append(orderBy);

        return jdbcTemplate.query(connection -> {
            PreparedStatement p = connection.prepareStatement(sql.toString());
            p.setMaxRows(100);
            p.setInt(1, siteId);
            if(after != null){
                p.setTimestamp(2, getTimestamp(after));
            }
            if(before != null){
                p.setTimestamp(3, getTimestamp(before));
            }
            return p;
        }, (rs, i) -> { return new QueryStatItem(rs.getString("query"), rs.getInt("numberofsearches"), rs.getLong("numberofhits"));  });
    }

    private Timestamp getTimestamp(LocalDateTime after) {
        return Timestamp.valueOf(after);
    }

    @Autowired
    @Qualifier("aksessDataSource")
    public void setDataSource(DataSource dataSource){
        namedjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
