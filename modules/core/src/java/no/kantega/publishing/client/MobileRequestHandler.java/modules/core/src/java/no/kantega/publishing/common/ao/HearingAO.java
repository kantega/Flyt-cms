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

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.spring.RootContext;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.sql.*;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;


public class HearingAO {
    private static final String SOURCE = "aksess.HearingAO";


    public static Hearing getHearing(int id) throws SystemException {

        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        return (Hearing) template.queryForObject("select * from hearing where HearingId = ?",
                new Integer[] {new Integer(id)},
                new RowMapper() {
                    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                        return getHearingFromRS(resultSet);
                    }
                });

    }

    public static Hearing getHearingByContentVersion(int contentVersionId) throws SystemException {

        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        List list = template.query("select * from hearing where ContentVersionId = ?",
                new Integer[] {new Integer(contentVersionId)},
                new RowMapper() {
                    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                        return getHearingFromRS(resultSet);
                    }
                });

        if (list == null || list.size() == 0) {
            return null;
        } else  {
            return (Hearing)list.get(0);
        }
    }

    public static List getCommentsForHearing(int hearingId) {
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        return template.query("select HearingCommentId, HearingId, UserRef, CommentDate, CommentContent from hearingcomment WHERE HearingId=? order by CommentDate",
        new Object[] {new Integer(hearingId)},
            new RowMapper() {
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    return getHearingCommentFromResultSet(resultSet);
                }


            });
    }

    public static int saveOrUpdate(final HearingComment comment) {
        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());


        if(comment.getId() > 0) {
            template.update("update hearingcomment set HearingId= ?, UserRef = ?, CommmentDate = ?, CommentContent = ? where HearingCommentId = ?",
                    new Object[] {
                            new Integer(comment.getId()),
                            comment.getUserRef(),
                            new Timestamp(comment.getDate().getTime()),
                            comment.getComment(),
                            new Integer(comment.getId())});
            return comment.getId();

        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

            template.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement p = connection.prepareStatement("insert into hearingcomment (HearingId, UserRef, CommentDate, CommentContent) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    p.setInt(1, comment.getHearingId());
                    p.setString(2, comment.getUserRef());
                    p.setTimestamp(3, new Timestamp(comment.getDate().getTime()));
                    p.setString(4, comment.getComment());
                    return p;
                }
            }, keyHolder);

            comment.setId(keyHolder.getKey().intValue());
            return comment.getId();
        }
    }

    private static Hearing getHearingFromRS(ResultSet rs) throws SQLException {
        Hearing hearing = new Hearing();
        hearing.setId(rs.getInt("HearingId"));
        hearing.setDeadLine(rs.getTimestamp("DeadLine"));
        hearing.setContentVersionId(rs.getInt("ContentVersionId"));
        return hearing;
    }

    private static HearingComment getHearingCommentFromResultSet(ResultSet resultSet) throws SQLException {
        HearingComment comment = new HearingComment();
        comment.setId(resultSet.getInt("HearingCommentId"));
        comment.setHearingId(resultSet.getInt("HearingId"));
        comment.setUserRef(resultSet.getString("userRef"));
        comment.setDate(resultSet.getTimestamp("CommentDate"));
        comment.setComment(resultSet.getString("CommentContent"));
        return comment;
    }

    public static int saveOrUpdate(final Hearing hearing) throws SystemException {

        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        if(hearing.getId() > 0) {
            template.update("update hearing set ContentVersionId = ?, Deadline = ? where HearingId = ?",
                    new Object[] {
                            new Integer(hearing.getContentVersionId()),
                            new Timestamp(hearing.getDeadLine().getTime()),
                            new Integer(hearing.getId())
                    });
            return hearing.getId();

        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

            template.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement p = connection.prepareStatement("insert into hearing (ContentVersionId, Deadline) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                    p.setInt(1, hearing.getContentVersionId());
                    p.setTimestamp(2, new Timestamp(hearing.getDeadLine().getTime()));
                    return p;
                }
            }, keyHolder);

            hearing.setId(keyHolder.getKey().intValue());

            return hearing.getId();
        }
    }

    public static int saveOrUpdate(final HearingInvitee invitee) throws SystemException {

        JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

        if(invitee.getId() > 0) {
            template.update("update hearinginvitee set HearingId = ?, InviteeType= ?, InviteeRef = ? where HearingInviteeId = ?",
                    new Object[] {
                            new Integer(invitee.getHearingId()),
                            new Integer(invitee.getType()),
                            invitee.getReference(),
                            new Integer(invitee.getId())
                    });
            return invitee.getId();

        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

            template.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement p = connection.prepareStatement("insert into hearinginvitee (HearingId, InviteeType, InviteeRef) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    p.setInt(1, invitee.getHearingId());
                    p.setInt(2, invitee.getType());
                    p.setString(3, invitee.getReference());
                    return p;
                }
            }, keyHolder);

            invitee.setId(keyHolder.getKey().intValue());

            return invitee.getId();
        }
    }

    public static List getPersonInviteesForHearing(int hearingId) {
        return getInviteesForHearingByType(hearingId, HearingInvitee.TYPE_PERSON);
    }

    public static List getOrgUnitInviteesForHearing(int hearingId) {
        return getInviteesForHearingByType(hearingId, HearingInvitee.TYPE_ORGUNIT);
    }

    private static List getInviteesForHearingByType(int hearingId, int type) {
            JdbcTemplate template = new JdbcTemplate(dbConnectionFactory.getDataSource());

            return template.query("select HearingInviteeId, HearingId, InviteeType, InviteeRef from hearinginvitee where hearingId=? AND InviteeType = ?",
                    new Integer[] {new Integer(hearingId), new Integer(type)},
                    new RowMapper() {
                        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                            return getHearingInviteeFromRS(resultSet);
                        }


                    });
        }


    private static Object getHearingInviteeFromRS(ResultSet resultSet) throws SQLException {
        HearingInvitee invitee = new HearingInvitee();
        invitee.setId(resultSet.getInt("HearingInviteeId"));
        invitee.setHearingId(resultSet.getInt("HearingId"));
        invitee.setType(resultSet.getInt("InviteeType"));
        invitee.setReference(resultSet.getString("InviteeRef"));
        return invitee;
    }

    public static int getHearingContentVersion(int contentId) {

        Connection c = null;

        try {
             c = dbConnectionFactory.getConnection();
            int activeversion = SQLHelper.getInt(c, "select Version from contentversion where ContentId = " + contentId +" and contentversion.IsActive = 1 order by Version desc" , "Version");
            if (activeversion == -1) {
                return -1;
            }
            return SQLHelper.getInt(c, "select ContentVersionId from contentversion where ContentId = " + contentId +  " AND Status = " + ContentStatus.HEARING +" AND Version > " +activeversion +" order by Version desc" , "ContentVersionId");
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            return -1;
        } finally {
            if(c!= null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }

    public static boolean isHearingInstance(int contentVersionId, String user) {
        JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
        String query = "select count(HearingInvitee.HearingInviteeId) FROM HearingInvitee, Hearing WHERE InviteeType=" + HearingInvitee.TYPE_PERSON + " AND InviteeRef='" + user + "' AND Hearing.HearingId=HearingInvitee.HearingId AND Hearing.ContentVersionId=" + contentVersionId;
        int count = template.queryForInt(query);
        if(count > 0) {
            return true;
        } else {
            OrganizationManager manager = (OrganizationManager) RootContext.getInstance().getBeansOfType(OrganizationManager.class).values().iterator().next();
            List above = manager.getOrgUnitsAboveUser(user);
            if(above.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("select count(HearingInvitee.HearingInviteeId) FROM HearingInvitee, Hearing WHERE InviteeType=" + HearingInvitee.TYPE_ORGUNIT + " AND Hearing.HearingId=HearingInvitee.HearingId AND Hearing.ContentVersionId=" + contentVersionId+" AND InviteeRef IN (");

                for (int i = 0; i < above.size(); i++) {
                    OrgUnit unit = (OrgUnit) above.get(i);
                    buffer.append("'");
                    buffer.append(unit.getExternalId());
                    buffer.append("'");
                    if(i < above.size() -1) {
                        buffer.append(",");
                    }

                }
                buffer.append(")");

                count = template.queryForInt(buffer.toString());
                if(count > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
