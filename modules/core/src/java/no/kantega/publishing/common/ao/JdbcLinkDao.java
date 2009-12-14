/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.ao;

import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import no.kantega.publishing.modules.linkcheck.check.LinkHandler;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;
import no.kantega.commons.sqlsearch.SearchTerm;
import no.kantega.commons.sqlsearch.dialect.SQLDialect;
import no.kantega.commons.log.Log;

import java.util.*;
import java.util.Date;
import java.sql.*;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.DataAccessException;

public class JdbcLinkDao extends JdbcDaoSupport implements LinkDao {

    private SQLDialect sqlDialect;

    /**
     * @see no.kantega.publishing.common.ao.LinkDao#deleteAllLinks()
     */
    public void deleteAllLinks() {
        getJdbcTemplate().update("delete from link");
        getJdbcTemplate().update("delete from linkoccurrence");
    }


    /**
     * @see LinkDao#saveAllLinks(no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter)
     */
    public void saveAllLinks(final LinkEmitter emitter) {
        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {

                final PreparedStatement checkLinkStatement = connection.prepareStatement("SELECT Id from link where url=?");

                final PreparedStatement checkOccurrenceStatementAttribute = connection.prepareStatement("SELECT Id from linkoccurrence where linkId=? AND ContentId=? AND AttributeName=?");

                final PreparedStatement checkOccurrenceStatement = connection.prepareStatement("SELECT Id from linkoccurrence where linkId=? AND ContentId=? AND AttributeName IS NULL");

                final PreparedStatement insLinkStatement = connection.prepareStatement("INSERT INTO link (url, firstfound, timeschecked) VALUES (?,?,0)", new String[] {"Id"});

                final PreparedStatement insOccurrenceStatement = connection.prepareStatement("INSERT into linkoccurrence (LinkId, ContentId, AttributeName) VALUES (?, ?, ?) ");


                emitter.emittLinks(new no.kantega.publishing.modules.linkcheck.crawl.LinkHandler() {
                    public void contentLinkFound(Content content, String link) {
                        try {
                            int linkId = checkLinkInserted(link, checkLinkStatement, insLinkStatement);

                            checkLinkOccurrenceInserted(linkId, content, checkOccurrenceStatement, insOccurrenceStatement, null);


                        } catch (SQLException e) {
                            Log.error(this.getClass().getName(), e, null, null);
                        }
                    }

                    public void attributeLinkFound(Content content, String link, String attributeName) {
                        try {
                            int linkId = checkLinkInserted(link, checkLinkStatement, insLinkStatement);
                            checkLinkOccurrenceInserted(linkId, content, checkOccurrenceStatementAttribute, insOccurrenceStatement, attributeName);

                        } catch (SQLException e) {
                            Log.error(this.getClass().getName(), e, null, null);
                        }
                    }
                });

                return null;
            }
        });
    }


    /**
     * @see no.kantega.publishing.common.ao.LinkDao#doForEachLink(no.kantega.commons.sqlsearch.SearchTerm, no.kantega.publishing.modules.linkcheck.check.LinkHandler)
     */
    public void doForEachLink(SearchTerm term, final no.kantega.publishing.modules.linkcheck.check.LinkHandler handler) {


        final String query = term.getQuery("Id, url", sqlDialect.getResultLimitorStrategy());

        Log.debug(this.getClass().getName(), "query=" + query, null, null);

        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                PreparedStatement p = connection.prepareStatement(query);

                PreparedStatement updateStatement = connection.prepareStatement("UPDATE link set lastchecked=?, status=?, httpstatus=?, timeschecked = timeschecked + 1 where id=?");

                ResultSet rs = p.executeQuery();

                while(rs.next()) {
                    int id = rs.getInt("Id");
                    String url = rs.getString("url");

                    LinkOccurrence occurrence = new LinkOccurrence();
                    handler.handleLink(id, url, occurrence);

                    if(occurrence.getStatus() != -1) {
                        updateStatement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                        updateStatement.setInt(2, occurrence.getStatus());
                        updateStatement.setInt(3, occurrence.getHttpStatus());
                        updateStatement.setInt(4, id);
                        updateStatement.executeUpdate();
                    }

                }
                return null;
            }
        });
    }


    /**
     * @see no.kantega.publishing.common.ao.LinkDao#doForEachLinkOccurrence(int, String, no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler)
     */
    public void doForEachLinkOccurrence(int siteId, String sort, final LinkOccurrenceHandler handler) {
        String orderBy;
        if("url".equals(sort)) {
            orderBy = "link.url";
        } else if("status".equals(sort)) {
            orderBy = "link.status, link.httpstatus, contentversion.title";
        } else if("lastchecked".equals(sort)) {
            orderBy = "link.lastchecked";
        } else if("timeschecked".equals(sort)) {
            orderBy = "link.timeschecked";
        } else {
            orderBy = "contentversion.title";
        }

        String siteClause = "";
        if (siteId > 0) {
            siteClause = " AND linkoccurrence.ContentId IN (SELECT ContentId FROM associations WHERE SiteId=" + siteId + ")";
        }
        final String query = "SELECT linkoccurrence.Id, linkoccurrence.ContentId, contentversion.Title, linkoccurrence.AttributeName, linkoccurrence.linkId, link.url, link.lastchecked, link.status, link.httpstatus, link.timeschecked FROM link, linkoccurrence, content, contentversion WHERE ((NOT (link.status=1) AND link.lastchecked is not null) AND content.ContentId=linkoccurrence.contentid and content.ContentId=contentversion.ContentID and contentversion.IsActive=1 and content.ContentId in (select ContentId from associations where IsDeleted = 0) AND linkoccurrence.linkid=link.id) " + siteClause + " ORDER BY " + orderBy;

        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                PreparedStatement p = connection.prepareStatement(query);
                ResultSet rs = p.executeQuery();

                while(rs.next()) {
                    LinkOccurrence o = getOccurrenceFromResultSet(rs);
                    handler.handleLinkOccurrence(o);
                }
                return null;
            }

        });

    }


    /**
     * @see LinkDao#getLinksforContentId(int)
     */
    @SuppressWarnings("unchecked")
    public List<LinkOccurrence> getLinksforContentId(int contentId) {
        return getJdbcTemplate().query("select * from linkoccurrence where ContentId=?", new Object[]{contentId},new RowMapper(){
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getOccurrenceFromResultSet(rs);
            }
        });
    }


    /**
     * @see LinkDao#deleteLinksForContentId(int)
     */
    public void deleteLinksForContentId(int contentId) {
        getJdbcTemplate().execute("delete from linkoccurrence where ContentId = " + contentId);
    }


    /**
     * @see no.kantega.publishing.common.ao.LinkDao#getNumberOfLinks()
     */
    public int getNumberOfLinks() {
        return getJdbcTemplate().queryForInt("select count(*) from link");
    }




    private static int checkLinkInserted(String link, PreparedStatement checkLinkStatement, PreparedStatement insLinkStatement) throws SQLException {
        // Sjekk om linken er registrert fra før, legg til hvis ikke
        checkLinkStatement.setString(1, link);
        ResultSet rs = checkLinkStatement.executeQuery();
        int linkId;
        if(!rs.next()) {
            insLinkStatement.setString(1, link);
            insLinkStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            insLinkStatement.executeUpdate();
            ResultSet keys = insLinkStatement.getGeneratedKeys();
            keys.next();
            linkId = keys.getInt(1);
        } else {
            linkId  = rs.getInt(1);
        }
        return linkId;
    }


    private static void checkLinkOccurrenceInserted(int linkId, Content content, PreparedStatement checkOccurrenceStatement, PreparedStatement insOccurrenceStatement, String attributeName) throws SQLException {
        // Legg til occurrence hvis ikke registrert hva før
        checkOccurrenceStatement.setInt(1, linkId);
        checkOccurrenceStatement.setInt(2, content.getId());
        if (attributeName != null) {
            checkOccurrenceStatement.setString(3, attributeName);
        }

        ResultSet rs = checkOccurrenceStatement.executeQuery();

        if(!rs.next()) {
            insOccurrenceStatement.setInt(1, linkId);
            insOccurrenceStatement.setInt(2, content.getId());
            insOccurrenceStatement.setString(3, attributeName);
            insOccurrenceStatement.executeUpdate();
        }
    }

    private static LinkOccurrence getOccurrenceFromResultSet(ResultSet rs) throws SQLException {
        LinkOccurrence o = new LinkOccurrence();
        o.setId(rs.getInt("id"));
        o.setContentId(rs.getInt("ContentId"));
        o.setContentTitle(rs.getString("Title"));
        o.setAttributeName(rs.getString("AttributeName"));
        o.setLinkId(rs.getInt("linkId"));
        o.setUrl(rs.getString("url"));
        Timestamp lastChecked = rs.getTimestamp("lastchecked");
        o.setLastChecked(lastChecked == null ? null : new Date(lastChecked.getTime()));
        o.setStatus(rs.getInt("status"));
        o.setHttpStatus(rs.getInt("httpstatus"));
        o.setTimesChecked(rs.getInt("timeschecked"));

        return o;
    }

    public void setSqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
}
