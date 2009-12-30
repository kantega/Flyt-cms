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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.ContentViewStatistics;
import no.kantega.publishing.common.data.PeriodViewStatistics;
import no.kantega.publishing.common.data.TrafficLogQuery;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.TrafficOrigin;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class TrafficLogAO {
    private static final String SOURCE = "aksess.TrafficLogAO";

    public static int getNumberOfHitsOrSessionsInPeriod(TrafficLogQuery query, boolean sessions) throws SystemException {
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

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            String originClause = createOriginClause(query.getTrafficOrigin());

            String sessionsClause = "count(ContentId)";
            if (sessions) {
                sessionsClause = "count(distinct SessionId)";
            }
            PreparedStatement st = c.prepareStatement("select " + sessionsClause + "  as Hits from trafficlog where ContentId = ? and Time >= ? and Time <= ?" + originClause);
            st.setInt(1, query.getCid().getContentId());
            st.setTimestamp(2, new java.sql.Timestamp(start.getTime()));
            st.setTimestamp(3, new java.sql.Timestamp(end.getTime()));
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                visits = rs.getInt("Hits");
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return visits;
    }

    private static String createOriginClause(int trafficOrigin) {
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

    public static int getTotalNumberOfHitsOrSessionsInPeriod(TrafficLogQuery query, boolean sessions) throws SystemException {
        int visits = 0;

        Calendar calendar = new GregorianCalendar();

        Date end = query.getEnd();
        if (end == null) {
            end = calendar.getTime();
        }

        Date start = query.getEnd();
        if (start == null) {
            calendar.add(Calendar.MONTH, -1);
            start = calendar.getTime();
        }

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            String originClause = createOriginClause(query.getTrafficOrigin());

            String sessionsClause = "count(ContentId)";
            if (sessions) {
                sessionsClause = "count(distinct SessionId)";
            }
            PreparedStatement st = c.prepareStatement("select " + sessionsClause + "  as Hits from trafficlog where Time >= ? and Time <= ? and SiteId = ?" + originClause);
            st.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            st.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            st.setInt(3, query.getSiteId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                visits = rs.getInt("Hits");
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return visits;
    }


    public static List getMostVisitedContentStatistics(TrafficLogQuery trafficQuery, int limit) throws SystemException {
        List stats = new ArrayList();
        Connection c = null;

        Calendar calendar = new GregorianCalendar();
        Date end = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date start = calendar.getTime();

        try {
            c = dbConnectionFactory.getConnection();
            String query = "select associations.associationid, contentversion.title, count(trafficlog.contentid) as count FROM associations, content, contentversion, trafficlog WHERE content.contentid = trafficlog.contentid AND content.contentid = associations.contentid and content.contentid = contentversion.contentid and contentversion.isactive = 1 AND trafficlog.SiteId = ? AND associations.SiteId = trafficlog.SiteId AND associations.type = " + AssociationType.DEFAULT_POSTING_FOR_SITE + " and Time >= ? and Time <= ?";
            query += createOriginClause(trafficQuery.getTrafficOrigin());
            PreparedStatement p = c.prepareStatement(query + "  group by associations.associationid, contentversion.title order by count desc");
            p.setInt(1, trafficQuery.getSiteId());
            p.setTimestamp(2, new java.sql.Timestamp(start.getTime()));
            p.setTimestamp(3, new java.sql.Timestamp(end.getTime()));
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
            throw new SystemException("SQL error",SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }

    public static List getPeriodViewStatistics(TrafficLogQuery trafficQuery, int period) throws SystemException {
        List stats = new ArrayList();
        Connection c = null;

        Calendar calendar = new GregorianCalendar();
        Date end = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date start = calendar.getTime();

        int siteId = trafficQuery.getSiteId();

        String originClause = createOriginClause(trafficQuery.getTrafficOrigin());

        try {
            c = dbConnectionFactory.getConnection();
            String query = "";
            String driver = dbConnectionFactory.getDriverName();
            if ((driver.indexOf("oracle") != -1) || (driver.indexOf("postgresql") != -1)) {
                String dp = "DD";
                switch (period) {
                    case Calendar.HOUR:
                        dp = "HH24";
                }
                query = "select TO_CHAR(time,'" + dp + "') as period, count(*) as count from trafficlog where SiteId = " + siteId + " and Time >= ? and Time <= ? " + originClause + " group by TO_CHAR(time,'" + dp + "') order by period";
            } else if (driver.indexOf("mysql") != -1) {
                String dp = "day";
                switch (period) {
                    case Calendar.HOUR:
                        dp = "hour";
                }
                query = "select " + dp + "(time) as period, count(*) as count from trafficlog  where SiteId = " + siteId + " and Time >= ? and Time <= ? " + originClause + " group by " + dp + "(time) order by period";
            } else {
                String dp = "day";
                switch (period) {
                    case Calendar.HOUR:
                        dp = "hour";
                }
                query = "select datepart(" + dp + ", time) as period, count(*) as count from trafficlog where SiteId = " + siteId + " and Time >= ? and Time <= ? " + originClause + " group by datepart(" + dp + ", time) order by period";
            }

            PreparedStatement p = c.prepareStatement(query);
            p.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            p.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            ResultSet rs = p.executeQuery();
            int i = 0;
            while(rs.next()) {
                PeriodViewStatistics stat = new PeriodViewStatistics();
                stat.setPeriod(rs.getString(1));
                stat.setNoPageViews(rs.getInt(2));
                stats.add(stat);
            }
            return stats;
        } catch (SQLException e) {
            throw new SystemException("SQL error",SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }

    public static List getReferersInPeriod(TrafficLogQuery query) {
        return internalGetReferer("referer, count(referer) as refcount", "referer", query.getCid(), query.getStart(), query.getEnd(), query.getTrafficOrigin());
    }

    private static List internalGetReferer(String select, String groupby, final ContentIdentifier cid, final Date start, final Date end, int origin) {
        final List arguments = new ArrayList();
        arguments.add(new Integer(cid.getContentId()));

        final StringBuffer query = new StringBuffer();
        query.append("select ").append(select).append(" from trafficlog where Contentid=? and ").append(groupby).append(" is not null ");
        if(start != null) {
            query.append(" and Time >=? ");
            arguments.add(start);
        }
        if(end != null) {
            query.append(" and Time <= ? ");
            arguments.add(end);
        }
        query.append(createOriginClause(origin));
        query.append(" group by ").append(groupby).append(" order by refcount desc ");
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        return template.query(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
                int p = 1;
                preparedStatement.setInt(p++, cid.getContentId());
                if(start != null) {
                    preparedStatement.setTimestamp(p++, new Timestamp(start.getTime()));
                }
                if(end != null) {
                    preparedStatement.setTimestamp(p++, new Timestamp(end.getTime()));
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

    public static List getReferingHostsInPeriod(TrafficLogQuery query) {
        return internalGetReferer("RefererHost, count(RefererHost) as refcount", "RefererHost", query.getCid(), query.getStart(), query.getEnd(), query.getTrafficOrigin());
    }

    public static List getReferingQueriesInPeriod(TrafficLogQuery query) {
        return internalGetReferer("RefererQuery, count(RefererQuery) as refcount", "RefererQuery", query.getCid(), query.getStart(), query.getEnd(), TrafficOrigin.ALL_USERS);
    }

    public static class RefererOccurrence {
        private String referer;
        private int occurrences;


        public RefererOccurrence(String referer, int occurrences) {
            this.referer = referer;
            this.occurrences = occurrences;
        }


        public String getReferer() {
            return referer;
        }
        public String getRefererShort() {
            int i = 60;
            return referer == null ? null : referer.length() > i ? referer.substring(0, i) +".." : referer;
        }

        public int getOccurrences() {
            return occurrences;
        }
    }
}
