package no.kantega.openaksess.search.searchlog.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
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

    public int getSearchCountForPeriod(Date after, Date before, int siteId)  {
        Map<String, Object> params = new HashMap<>();
        StringBuilder queryString = new StringBuilder("select count(*) from searchlog where siteId=:site_id");
        params.put("site_id", siteId);

        if(after!= null) {
            queryString.append(" and Time >= :aftertime ");
            params.put("aftertime", new Timestamp(after.getTime()));
        }
        if(before != null) {
            queryString.append(" and Time <= :beforetime ");
            params.put("beforetime", new Timestamp(before.getTime()));

        }
        return namedjdbcTemplate.queryForInt(queryString.toString(), params);
    }

    public List getMostPopularQueries(int siteId) {
        return getQueryStats("numberofsearches desc", siteId);
    }

    public List<QueryStatItem> getQueriesWithLeastHits(int siteId) {
        return getQueryStats("numberofhits asc", siteId);
    }

    private List<QueryStatItem> getQueryStats(final String orderBy, final int siteId) {
        return jdbcTemplate.query(new PreparedStatementCreator() {
                                      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                          PreparedStatement p = connection.prepareStatement("select query, count(*) as numberofsearches, avg(numberofhits) as numberofhits from searchlog where siteId=? group by query order by " + orderBy);
                                          p.setMaxRows(100);
                                          p.setInt(1, siteId);
                                          return p;
                                      }
                                  }, new RowMapper<QueryStatItem>() {
                                      public QueryStatItem mapRow(ResultSet rs, int i) throws SQLException {
                                          return new QueryStatItem(rs.getString("query"), rs.getInt("numberofsearches"), rs.getLong("numberofhits"));
                                      }
                                  });
    }


    @Autowired
    @Qualifier("aksessDataSource")
    public void setDataSource(DataSource dataSource){
        namedjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}