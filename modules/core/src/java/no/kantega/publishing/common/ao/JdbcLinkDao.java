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

import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.modules.linkcheck.check.CheckStatus;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkQueryGenerator;
import no.kantega.publishing.modules.linkcheck.check.NotCheckedSinceTerm;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.*;
import java.util.Date;
import java.util.List;

public class JdbcLinkDao extends JdbcDaoSupport implements LinkDao {
    private static final Logger log = LoggerFactory.getLogger(JdbcLinkDao.class);

    @Autowired
    private ContentIdHelper contentIdHelper;

    private String brokenLinkBasisQuery = "SELECT linkoccurrence.Id, linkoccurrence.ContentId, contentversion.Title, linkoccurrence.AttributeName, linkoccurrence.linkId, link.url, link.lastchecked, link.status, link.httpstatus, link.timeschecked FROM link, linkoccurrence, content, contentversion  WHERE ((NOT (link.status=1) AND link.lastchecked is not null) AND content.ContentId=linkoccurrence.contentid AND content.ContentId=contentversion.ContentID AND contentversion.IsActive=1 AND content.ContentId in (select ContentId from associations where IsDeleted = 0) AND linkoccurrence.linkid=link.id)";

    /**
     * @see LinkDao#deleteAllLinks()
     */
    @Override
    public void deleteAllLinks() {
        getJdbcTemplate().update("delete from link");
        getJdbcTemplate().update("delete from linkoccurrence");
    }


    /**
     * @see LinkDao#saveAllLinks(no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter)
     */
    @Override
    public void saveAllLinks(final LinkEmitter emitter) {
        log.debug("Saving all links");
        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {

                try (final PreparedStatement checkLinkStatement = connection.prepareStatement("SELECT Id from link where url=?");

                     final PreparedStatement checkOccurrenceStatementAttribute = connection.prepareStatement("SELECT Id from linkoccurrence where linkId=? AND ContentId=? AND AttributeName=?");

                     final PreparedStatement checkOccurrenceStatement = connection.prepareStatement("SELECT Id from linkoccurrence where linkId=? AND ContentId=? AND AttributeName IS NULL");

                     final PreparedStatement insLinkStatement = connection.prepareStatement("INSERT INTO link (url, firstfound, timeschecked) VALUES (?,?,0)", new String[]{"Id"});

                     final PreparedStatement insOccurrenceStatement = connection.prepareStatement("INSERT into linkoccurrence (LinkId, ContentId, AttributeName) VALUES (?, ?, ?) ")) {


                    emitter.emittLinks(new no.kantega.publishing.modules.linkcheck.crawl.LinkHandler() {
                        public void contentLinkFound(Content content, String link) {
                            log.debug("Contentlink found {}", link);
                            try {
                                int linkId = checkLinkInserted(link, checkLinkStatement, insLinkStatement);

                                checkLinkOccurrenceInserted(linkId, content, checkOccurrenceStatement, insOccurrenceStatement, null);

                            } catch (SQLException e) {
                                log.error("Error inserting link occurrence", e);
                            }
                        }

                        public void attributeLinkFound(Content content, String link, String attributeName) {
                            log.debug("Attributelink for {} found {}", attributeName, link);
                            try {
                                int linkId = checkLinkInserted(link, checkLinkStatement, insLinkStatement);
                                checkLinkOccurrenceInserted(linkId, content, checkOccurrenceStatementAttribute, insOccurrenceStatement, attributeName);

                            } catch (SQLException e) {
                                log.error("Error inserting link occurrence", e);
                            }
                        }
                    });
                    log.info("Done emitting links");
                    return null;
                }
            }
        });
    }
    @Override
    public void saveLinksForContent(final LinkEmitter emitter, final Content content) {
        log.debug("Saving link for content" + content.getId());
        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {

                try (final PreparedStatement checkLinkStatement = connection.prepareStatement("SELECT Id from link where url=?");

                     final PreparedStatement checkOccurrenceStatementAttribute = connection.prepareStatement("SELECT Id from linkoccurrence where linkId=? AND ContentId=? AND AttributeName=?");

                     final PreparedStatement checkOccurrenceStatement = connection.prepareStatement("SELECT Id from linkoccurrence where linkId=? AND ContentId=? AND AttributeName IS NULL");

                     final PreparedStatement insLinkStatement = connection.prepareStatement("INSERT INTO link (url, firstfound, timeschecked) VALUES (?,?,0)", new String[]{"Id"});

                     final PreparedStatement insOccurrenceStatement = connection.prepareStatement("INSERT into linkoccurrence (LinkId, ContentId, AttributeName) VALUES (?, ?, ?) ")) {


                    emitter.emittLinksForContent(new no.kantega.publishing.modules.linkcheck.crawl.LinkHandler() {
                        public void contentLinkFound(Content content, String link) {
                            log.debug("Contentlink found {}", link);
                            try {
                                int linkId = checkLinkInserted(link, checkLinkStatement, insLinkStatement);

                                checkLinkOccurrenceInserted(linkId, content, checkOccurrenceStatement, insOccurrenceStatement, null);

                            } catch (SQLException e) {
                                log.error("Error inserting link occurrence", e);
                            }
                        }

                        public void attributeLinkFound(Content content, String link, String attributeName) {
                            log.debug("Attributelink for {} found {}", attributeName, link);
                            try {
                                int linkId = checkLinkInserted(link, checkLinkStatement, insLinkStatement);
                                checkLinkOccurrenceInserted(linkId, content, checkOccurrenceStatementAttribute, insOccurrenceStatement, attributeName);

                            } catch (SQLException e) {
                                log.error("Error inserting link occurrence", e);
                            }
                        }
                    }, content);
                    log.info("Done emitting links");
                    return null;
                }
            }
        });
    }
    /**
     */
    @Override
    public void doForEachLink(final LinkQueryGenerator linkQueryGenerator, final no.kantega.publishing.modules.linkcheck.check.LinkHandler handler) {
        final String query = linkQueryGenerator.getQuery();
        log.debug( "query={}", query);
        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                try(PreparedStatement p = connection.prepareStatement(query);
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE link set lastchecked=?, status=?, httpstatus=?, timeschecked = timeschecked + 1 where id=?")) {
                    if(linkQueryGenerator instanceof NotCheckedSinceTerm){
                        p.setDate(1, new java.sql.Date(((NotCheckedSinceTerm)linkQueryGenerator).getNotCheckedSince().getTime()));
                    }
                    try(ResultSet rs = p.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("Id");
                            String url = rs.getString("url");
                            log.debug("Checking url {}", url);
                            LinkOccurrence occurrence = new LinkOccurrence();
                            handler.handleLink(id, url, occurrence);
                            if (occurrence.getStatus() != null) {
                                updateStatement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                                updateStatement.setInt(2, occurrence.getStatus().intValue);
                                updateStatement.setInt(3, occurrence.getHttpStatus());
                                updateStatement.setInt(4, id);
                                updateStatement.executeUpdate();
                            }

                        }
                    }
                }
                return null;
            }
        });
    }

    /**
     * @see LinkDao#getBrokenLinksUnderParent(no.kantega.publishing.api.content.ContentIdentifier, String)
     */
    @Override
    public List<LinkOccurrence> getBrokenLinksUnderParent(ContentIdentifier parent, String sort) {
        contentIdHelper.assureContentIdAndAssociationIdSet(parent);
        String query = brokenLinkBasisQuery
            + " AND linkoccurrence.ContentId IN (SELECT ContentId FROM associations WHERE Path LIKE ? OR UniqueId = ?)"
            + getDefaultOrderByClause();
        int associationId = parent.getAssociationId();
        Object[] args = {"%/" + associationId + "/%", associationId};
        return findMatchingLinkOccurrences(query, args);
    }

    /**
     * @see LinkDao#getAllBrokenLinks(String)
     */
    @Override
    public List<LinkOccurrence> getAllBrokenLinks(String sortBy) {
        String query = brokenLinkBasisQuery
            + getOrderByClause("");
        Object[] args = {};
        return findMatchingLinkOccurrences(query, args);
    }


    /**
     * @see LinkDao#getBrokenLinksforContentId(int)
     */
    @Override
    public List<LinkOccurrence> getBrokenLinksforContentId(int contentId) {
        return findMatchingLinkOccurrences("SELECT linkoccurrence.Id, linkoccurrence.ContentId, contentversion.Title, linkoccurrence.AttributeName, linkoccurrence.linkId, link.url, link.lastchecked, link.status, link.httpstatus, link.timeschecked FROM link, linkoccurrence, content, contentversion WHERE ((NOT (link.status=1) AND link.lastchecked is not null) AND content.ContentId=linkoccurrence.contentid AND content.ContentId=contentversion.ContentID AND contentversion.IsActive=1 AND linkoccurrence.linkid=link.id AND content.ContentId=?) ORDER BY link.lastchecked", new Object[]{contentId});
    }

    /**
     * @see LinkDao#deleteLinksForContentId(int)
     */
    @Override
    public void deleteLinksForContentId(int contentId) {
        getJdbcTemplate().execute("delete from linkoccurrence where ContentId = " + contentId);
    }


    /**
     * @see LinkDao#getNumberOfLinks()
     */
    @Override
    public int getNumberOfLinks() {
        return getJdbcTemplate().queryForObject("select count(*) from link", Integer.class);
    }

    private static int checkLinkInserted(String link, PreparedStatement checkLinkStatement, PreparedStatement insLinkStatement) throws SQLException {
        log.debug("Inserting link {}", link);
        // Check if link is registred, if not add the link.
        checkLinkStatement.setString(1, link);
        int linkId;
        try(ResultSet rs = checkLinkStatement.executeQuery()){
            if(!rs.next()) {
                insLinkStatement.setString(1, link);
                insLinkStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
                insLinkStatement.executeUpdate();
                ResultSet keys = insLinkStatement.getGeneratedKeys();
                keys.next();
                linkId = keys.getInt(1);
            } else {
                linkId  = rs.getInt(1);
            }}
        return linkId;
    }


    private static void checkLinkOccurrenceInserted(int linkId, Content content, PreparedStatement checkOccurrenceStatement, PreparedStatement insOccurrenceStatement, String attributeName) throws SQLException {
        // Add to occurrence if not registred before
        checkOccurrenceStatement.setInt(1, linkId);
        checkOccurrenceStatement.setInt(2, content.getId());
        if (attributeName != null) {
            checkOccurrenceStatement.setString(3, attributeName);
        }

        try(ResultSet rs = checkOccurrenceStatement.executeQuery()) {

            if (!rs.next()) {
                insOccurrenceStatement.setInt(1, linkId);
                insOccurrenceStatement.setInt(2, content.getId());
                insOccurrenceStatement.setString(3, attributeName);
                insOccurrenceStatement.executeUpdate();
            }
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
        o.setStatus(CheckStatus.getFromInt(rs.getInt("status")));
        o.setHttpStatus(rs.getInt("httpstatus"));
        o.setTimesChecked(rs.getInt("timeschecked"));

        return o;
    }

    private List<LinkOccurrence> findMatchingLinkOccurrences(String query, Object[] args) {
        return getJdbcTemplate().query(query, args, (rs, rowNum) -> {
            return getOccurrenceFromResultSet(rs);
        });
    }

    private String getDefaultOrderByClause(){
        return getOrderByClause("");
    }

    private String getOrderByClause(String sort) {
        String orderBy;
        switch (sort) {
            case "url":
                orderBy = "link.url";
                break;
            case "status":
                orderBy = "link.status, link.httpstatus, contentversion.title";
                break;
            case "lastchecked":
                orderBy = "link.lastchecked";
                break;
            case "timeschecked":
                orderBy = "link.timeschecked";
                break;
            default:
                orderBy = "contentversion.title";
                break;
        }
        return " ORDER BY " + orderBy;
    }
}
