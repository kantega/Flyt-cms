/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.ContentViewStatistics;
import no.kantega.publishing.common.data.PeriodViewStatistics;
import no.kantega.publishing.common.data.RefererOccurrence;
import no.kantega.publishing.common.data.TrafficLogQuery;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.TrafficOrigin;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class JdbcTrafficLogDao extends JdbcDaoSupport implements TrafficLogDao {
    private static final Logger log = LoggerFactory.getLogger(JdbcTrafficLogDao.class);
    @Autowired
    private ContentIdHelper contentIdHelper;

    public int getNumberOfHitsOrSessionsInPeriod(TrafficLogQuery query, boolean sessions) throws SystemException {
        int visits = 0;

        Calendar calendar = new GregorianCalendar();

        Date end = query.getEnd();
        if (end == null) {
            end = calendar.getTime();
        }

        Date start = query.getStart();
        if (start == null) {
            calendar.add(Calendar.MONTH, -1);
            start = calendar.getTime();
        }

        try (Connection c = getConnection()) {
            String originClause = createOriginClause(query.getTrafficOrigin());
            String contentIdClause = createContentIdClause(query);

            String sessionsClause = "count(ContentId)";
            if (sessions) {
                sessionsClause = "count(distinct SessionId)";
            }
            PreparedStatement st = c.prepareStatement("select " + sessionsClause + "  as Hits from trafficlog where Time >= ? and Time <= ?" + originClause + contentIdClause);
            st.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            st.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                visits = rs.getInt("Hits");
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil", e);
        }

        return visits;
    }

    private String createContentIdClause(TrafficLogQuery query) {
        String clause = "";
        ContentIdentifier cid = query.getCid();
        if (cid != null) {
            contentIdHelper.assureContentIdAndAssociationIdSet(cid);
            int contentId = cid.getContentId();
            int siteId = cid.getSiteId();
            if (siteId != -1) {
                clause = " and trafficlog.SiteId = " + siteId;
            }
            if (query.isIncludeSubPages()) {
                int associationId = cid.getAssociationId();
                clause += " and (trafficlog.ContentId in (select ContentId from associations where path like '%/" + associationId + "/%') OR trafficlog.ContentId = " + contentId + ") ";
            } else {
                clause += " and trafficlog.ContentId = " + contentId + " ";
            }
        }
        return clause;
    }

    private String createOriginClause(int trafficOrigin) {
        String originClause = "";

        if (trafficOrigin == TrafficOrigin.INTERNAL && Aksess.getInternalIpSegment() != null) {
            String[] segments = Aksess.getInternalIpSegment();
            originClause = " and (";
            for (int i = 0; i < segments.length; i++) {
                if (i > 0) {
                    originClause += " or ";
                }
                originClause += "RemoteAddress like '" + segments[i] + "%'";
            }
            originClause += ") ";
        }

        if (trafficOrigin == TrafficOrigin.EXTERNAL && Aksess.getInternalIpSegment() != null)  {
            String[] segments = Aksess.getInternalIpSegment();
            originClause = " and (";
            for (int i = 0; i < segments.length; i++) {
                if (i > 0) {
                    originClause += " and ";
                }
                originClause += "RemoteAddress not like '" + segments[i] + "%'";
            }
            originClause += ") ";
        }

        if (trafficOrigin != TrafficOrigin.ALL && trafficOrigin != TrafficOrigin.SEARCH_ENGINES) {
            originClause += " and IsSpider = 0 ";
        }

        if (trafficOrigin == TrafficOrigin.SEARCH_ENGINES) {
            originClause += " and IsSpider = 1 ";
        }

        return originClause;
    }


    public List<ContentViewStatistics> getMostVisitedContentStatistics(TrafficLogQuery trafficQuery, int limit) throws SystemException {
        List<ContentViewStatistics> stats = new ArrayList<ContentViewStatistics>();

        Calendar calendar = new GregorianCalendar();
        Date end = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date start = calendar.getTime();

        try (Connection c = dbConnectionFactory.getConnection()) {
            String query = "select associations.associationid, contentversion.title, count(trafficlog.contentid) as count FROM associations, content, contentversion, trafficlog WHERE content.contentid = trafficlog.contentid AND content.contentid = associations.contentid and content.contentid = contentversion.contentid and contentversion.isactive = 1 AND associations.SiteId = trafficlog.SiteId AND associations.type = " + AssociationType.DEFAULT_POSTING_FOR_SITE + " and Time >= ? and Time <= ?";
            query += createOriginClause(trafficQuery.getTrafficOrigin());
            query += createContentIdClause(trafficQuery);
            PreparedStatement p = c.prepareStatement(query + "  group by associations.associationid, contentversion.title order by count desc");
            p.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            p.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            p.setMaxRows(limit);
            ResultSet rs = p.executeQuery();
            int i = 0;
            while(rs.next() && ++i < limit) {
                ContentViewStatistics stat = new ContentViewStatistics(rs.getInt(1), rs.getString(2));
                stat.setNoPageViews(rs.getInt(3));
                stats.add(stat);
            }
            return stats;
        } catch (SQLException e) {
            throw new SystemException("SQL error", e);
        }
    }

    public List<PeriodViewStatistics> getPeriodViewStatistics(TrafficLogQuery trafficQuery, int period) throws SystemException {
        List<PeriodViewStatistics> stats = new ArrayList<PeriodViewStatistics>();

        Calendar calendar = new GregorianCalendar();
        Date end = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date start = calendar.getTime();

        int siteId = trafficQuery.getCid().getSiteId();

        String originClause = createOriginClause(trafficQuery.getTrafficOrigin());
        String contentIdClause = createContentIdClause(trafficQuery);

        try (Connection c = dbConnectionFactory.getConnection()) {
            String query = "";
            String driver = dbConnectionFactory.getDriverName();
            if ((driver.contains("oracle")) || (driver.contains("postgresql"))) {
                String dp = "DD";
                switch (period) {
                    case Calendar.HOUR:
                        dp = "HH24";
                }
                query = "select TO_CHAR(time,'" + dp + "') as period, count(*) as count from trafficlog where SiteId = " + siteId + " and Time >= ? and Time <= ? " + originClause + contentIdClause + " group by TO_CHAR(time,'" + dp + "') order by period";
            } else if (driver.contains("mysql")) {
                String dp = "day";
                switch (period) {
                    case Calendar.HOUR:
                        dp = "hour";
                }
                query = "select " + dp + "(time) as period, count(*) as count from trafficlog  where SiteId = " + siteId + " and Time >= ? and Time <= ? " + originClause + contentIdClause +  " group by " + dp + "(time) order by period";
            } else {
                String dp = "day";
                switch (period) {
                    case Calendar.HOUR:
                        dp = "hour";
                }
                query = "select datepart(" + dp + ", time) as period, count(*) as count from trafficlog where SiteId = " + siteId + " and Time >= ? and Time <= ? " + originClause  + contentIdClause +  " group by datepart(" + dp + ", time) order by period";
            }

            PreparedStatement p = c.prepareStatement(query);
            p.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            p.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                PeriodViewStatistics stat = new PeriodViewStatistics();
                stat.setPeriod(rs.getString(1));
                stat.setNoPageViews(rs.getInt(2));
                stats.add(stat);
            }
            return stats;
        } catch (SQLException e) {
            throw new SystemException("SQL error", e);
        }
    }

    public List<RefererOccurrence> getReferersInPeriod(TrafficLogQuery query) {
        return internalGetReferer("referer, count(referer) as refcount", "referer", query.getCid(), query.getStart(), query.getEnd(), query.getTrafficOrigin());
    }

    @SuppressWarnings("unchecked")
    private List<RefererOccurrence> internalGetReferer(String select, String groupby, final ContentIdentifier cid, final Date start, final Date end, int origin) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        final StringBuffer query = new StringBuffer();
        query.append("select ").append(select).append(" from trafficlog where Contentid=? and ").append(groupby).append(" is not null ");
        if(start != null) {
            query.append(" and Time >=? ");
        }
        if(end != null) {
            query.append(" and Time <= ? ");
        }
        query.append(createOriginClause(origin));
        query.append(" group by ").append(groupby).append(" order by refcount desc ");

        return getJdbcTemplate().query(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
                int p = 1;
                preparedStatement.setInt(p++, cid.getContentId());
                if(start != null) {
                    preparedStatement.setTimestamp(p++, new Timestamp(start.getTime()));
                }
                if(end != null) {
                    preparedStatement.setTimestamp(p, new Timestamp(end.getTime()));
                }
                preparedStatement.setMaxRows(25);
                return preparedStatement;
            }
        }, new RowMapper() {

            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                return new RefererOccurrence(resultSet.getString(1), resultSet.getInt(2));
            }
        });
    }

    public List<RefererOccurrence> getReferingHostsInPeriod(TrafficLogQuery query) {
        return internalGetReferer("RefererHost, count(RefererHost) as refcount", "RefererHost", query.getCid(), query.getStart(), query.getEnd(), query.getTrafficOrigin());
    }

    public List<RefererOccurrence> getReferingQueriesInPeriod(TrafficLogQuery query) {
        return internalGetReferer("RefererQuery, count(RefererQuery) as refcount", "RefererQuery", query.getCid(), query.getStart(), query.getEnd(), TrafficOrigin.ALL_USERS);
    }
}
