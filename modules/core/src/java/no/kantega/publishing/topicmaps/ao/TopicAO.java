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

package no.kantega.publishing.topicmaps.ao;

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.Role;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.log.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TopicAO {
    private static final String SOURCE = "aksess.TopicAO";


    public static Topic getTopic(int topicMapId, String topicId) throws SystemException {
        Connection c = null;

        Topic topic = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st;
            if (topicMapId != -1) {
                st = c.prepareStatement("SELECT * FROM tmtopic WHERE TopicId = ? AND TopicMapId = ?");
                st.setString(1, topicId);
                st.setInt(2, topicMapId);                
            } else {
                st = c.prepareStatement("SELECT * FROM tmtopic WHERE TopicId = ?");
                st.setString(1, topicId);
            }
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                topic = new Topic();
                topic.setId(rs.getString("TopicId"));
                topic.setTopicMapId(rs.getInt("TopicMapId"));

                String instanceOf = rs.getString("InstanceOf");
                if (instanceOf != null) {
                    topic.setInstanceOf(new Topic(instanceOf));
                }

                topic.setIsTopicType(rs.getInt("IsTopicType") == 1);
                topic.setIsAssociation(rs.getInt("IsAssociation") == 1);

                topic.setLastUpdated(rs.getDate("LastUpdated"));
                topic.setSubjectIdentity(rs.getString("SubjectIdentity"));
                topic.setIsSelectable(rs.getInt("IsSelectable") == 1);

                // Hent basenames
                List baseNames = new ArrayList();
                st = c.prepareStatement("SELECT * FROM tmbasename WHERE TopicId = ? AND TopicMapId = ?");
                st.setString(1, topicId);
                st.setInt(2, topicMapId);
                rs = st.executeQuery();
                while(rs.next()) {
                    TopicBaseName baseName = new TopicBaseName();
                    baseName.setScope(rs.getString("Scope"));
                    baseName.setBaseName(rs.getString("Basename"));
                    baseNames.add(baseName);
                }
                topic.setBaseNames(baseNames);

                // Hent occurences
                List occurences =  new ArrayList();
                String sql = "";
                sql += " SELECT tmbasename.Basename, tmoccurence.ResourceData, tmoccurence.InstanceOf FROM tmoccurence";
                sql += "   INNER JOIN tmbasename ON (tmoccurence.InstanceOf = tmbasename.TopicId) AND (tmoccurence.TopicMapId = tmbasename.TopicMapId)";
                sql += " WHERE tmoccurence.TopicId = ? AND tmoccurence.TopicMapId = ?";
                st = c.prepareStatement(sql);
                st.setString(1, topicId);
                st.setInt(2, topicMapId);
                rs = st.executeQuery();
                while(rs.next()) {
                    TopicOccurence occurence = new TopicOccurence();
                    Topic instance = new Topic();
                    instance.setBaseName(rs.getString("Basename"));
                    occurence.setInstanceOf(instance);

                    occurence.setResourceData(rs.getString("ResourceData"));
                    instance.setId(rs.getString("InstanceOf"));

                    occurences.add(occurence);
                }
                topic.setOccurences(occurences);
            }
        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

        return topic;
    }

    public static void deleteTopic(Topic topic) throws SystemException {
        deleteTopic(topic, true);
    }

    public static void deleteTopic(Topic topic, boolean deleteRelatedTables) throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            // Slett eksisterende topic
            PreparedStatement st = c.prepareStatement("DELETE FROM tmtopic WHERE TopicId = ? AND TopicMapId = ?");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            st.execute();

            st = c.prepareStatement("DELETE FROM tmbasename WHERE TopicId = ? AND TopicMapId = ?");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            st.execute();

            st = c.prepareStatement("DELETE FROM tmoccurence WHERE TopicId = ? AND TopicMapId = ?");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            st.execute();

            if (deleteRelatedTables) {
                st = c.prepareStatement("DELETE FROM role2topic WHERE TopicId = ? AND TopicMapId = ?");
                st.setString(1, topic.getId());
                st.setInt(2, topic.getTopicMapId());
                st.execute();

                st = c.prepareStatement("DELETE FROM ct2topic WHERE TopicId = ? AND TopicMapId = ?");
                st.setString(1, topic.getId());
                st.setInt(2, topic.getTopicMapId());
                st.execute();
            }

        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    public static void setTopic(Topic topic) throws SystemException {
        Connection c = null;

        // Slett topic i tilfelle det finnes fra før
        deleteTopic(topic, false);

        try {
            c = dbConnectionFactory.getConnection();

            // Legg til topic
            PreparedStatement st = c.prepareStatement("INSERT INTO tmtopic VALUES(?,?,?,?,?,?,?,?)");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            st.setString(3, topic.getInstanceOf() == null ? null:topic.getInstanceOf().getId());
            st.setInt(4, topic.isTopicType() ? 1:0);
            st.setInt(5, topic.isAssociation() ? 1:0);
            st.setTimestamp(6, new java.sql.Timestamp(new Date().getTime()));
            st.setString(7, topic.getSubjectIdentity());
            st.setInt(8, topic.isSelectable() ? 1:0);
            st.execute();

            // Topics som andre er instans av merkes med et flagg
            if (topic.getInstanceOf() != null) {
                st = c.prepareStatement("UPDATE tmtopic SET IsTopicType = 1 WHERE TopicId = ? AND TopicMapId = ?");
                st.setString(1, topic.getId());
                st.setInt(2, topic.getTopicMapId());
            }

            // Legg til basenames
            st = c.prepareStatement("INSERT INTO tmbasename VALUES(?,?,?,?)");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());

            List baseNames = topic.getBaseNames();
            if (baseNames != null) {
                for (int i = 0; i < baseNames.size(); i++) {
                    TopicBaseName tbn = (TopicBaseName)baseNames.get(i);
                    st.setString(3, tbn.getScope());
                    st.setString(4, tbn.getBaseName());
                    st.execute();
                }
            }

            // Legg til occurences
            st = c.prepareStatement("INSERT INTO tmoccurence VALUES(?,?,?,?)");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());

            List occurences = topic.getOccurences();
            if (occurences != null) {
                for (int i = 0; i < occurences.size(); i++) {
                    TopicOccurence occurence = (TopicOccurence)occurences.get(i);
                    if (occurence.getInstanceOf() != null) {
                        st.setString(3, occurence.getInstanceOf().getId());
                        st.setString(4, occurence.getResourceData());
                        st.execute();
                    }
                }
            }

        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    
    /**
     * Adds topic to specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public static void addTopicContentAssociation(Topic topic, int contentId) throws SystemException {

        // Remove old association if exists
        removeTopicContentAssociation(topic, contentId);

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            // Add association
            PreparedStatement st = c.prepareStatement("INSERT INTO ct2topic VALUES (?,?,?)");
            st.setInt(1, contentId);
            st.setInt(2, topic.getTopicMapId());
            st.setString(3, topic.getId());
            st.execute();

        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

    }


    /**
     * Remove topic from specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public static void removeTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            // Slett eksisterende topic
            PreparedStatement st = c.prepareStatement("DELETE FROM ct2topic WHERE TopicId = ? AND TopicMapId = ? AND ContentId = ?");
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            st.setInt(3, contentId);
            st.execute();

        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

    }

    public static void removeTopicSIDAssociation(Topic topic, SecurityIdentifier cid) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            if (topic == null || cid == null) {
                return;
            }

            PreparedStatement st = c.prepareStatement("DELETE FROM role2topic WHERE TopicMapId = ? AND TopicId = ? AND Role = ?");
            st.setInt(1, topic.getTopicMapId());
            st.setString(2, topic.getId());
            st.setString(3, cid.getId());
            st.execute();

        } catch (SQLException e) {
            throw new SystemException("SQL Feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    public static void addTopicSIDAssociation(Topic topic, SecurityIdentifier cid) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            if (topic == null || cid == null) {
                return;
            }

            PreparedStatement st = c.prepareStatement("SELECT * FROM role2topic WHERE TopicMapId = ? AND TopicId = ? AND Role = ?");
            st.setInt(1, topic.getTopicMapId());
            st.setString(2, topic.getId());
            st.setString(3, cid.getId());
            ResultSet rs = st.executeQuery();

            boolean exists = false;
            if (rs.next()) {
                exists = true;
            }

            rs.close();
            rs = null;

            if (!exists) {
                st = c.prepareStatement("INSERT INTO role2topic VALUES (?, ?, ?, ?)");
                st.setInt(1, topic.getTopicMapId());
                st.setString(2, topic.getId());
                st.setString(3, cid.getType());
                st.setString(4, cid.getId());
                st.execute();
            }

            st = null;

        } catch (SQLException e) {
            throw new SystemException("SQL Feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }


    public static List getRolesByTopic(Topic topic) throws SystemException {
        Connection c = null;

        List roles = new ArrayList();

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("select Role from role2topic where TopicMapId = ? and TopicId = ? and RoleType = ? order by Role");
            st.setInt(1, topic.getTopicMapId());
            st.setString(2, topic.getId());
            st.setString(3, new Role().getType());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getString("Role"));
                role.setName(role.getId());
                roles.add(role);
            }
        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return roles;
    }


    private static List<Topic> getTopicsBySQLStatement(String whereClause) throws SystemException {
        Connection c = null;

        List<Topic> topics = new ArrayList<Topic>();

        try {
            c = dbConnectionFactory.getConnection();
            String sql = "";
            sql += " SELECT tmtopic.TopicId, tmtopic.TopicMapId, tmtopic.InstanceOf, tmtopic.SubjectIdentity, tmbasename.Basename, tmbasename.Scope, tmtopic.IsTopicType, tmtopic.IsAssociation";
            sql += "   FROM tmtopic";
            sql += " INNER JOIN tmbasename ON (tmtopic.TopicId = tmbasename.TopicId) AND (tmtopic.TopicMapId = tmbasename.TopicMapId)";

            ResultSet rs = SQLHelper.getResultSet(c, sql + whereClause);
            while (rs.next()) {
                Topic topic = new Topic();

                topic.setId(rs.getString("TopicId"));
                topic.setTopicMapId(rs.getInt("TopicMapId"));

                String instanceOf = rs.getString("InstanceOf");
                if (instanceOf != null) {
                    topic.setInstanceOf(new Topic(instanceOf, topic.getTopicMapId()));
                }

                topic.setSubjectIdentity(rs.getString("SubjectIdentity"));

                List<TopicBaseName> baseNames = new ArrayList<TopicBaseName>();
                TopicBaseName tbn = new TopicBaseName();
                tbn.setBaseName(rs.getString("Basename"));
                tbn.setScope(rs.getString("Scope"));
                baseNames.add(tbn);
                topic.setBaseNames(baseNames);

                topic.setIsTopicType(rs.getInt("IsTopicType") == 1);
                topic.setIsAssociation(rs.getInt("IsAssociation") == 1);

                topics.add(topic);
            }
        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return topics;
    }

    public static List<Topic> getAllTopics() throws SystemException {
        String sql = "";
        sql += "   WHERE tmtopic.IsTopicType = 0 AND tmtopic.IsAssociation = 0";
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicsByTopicMapId(int topicMapId) throws SystemException {
        String sql = "";
        sql += "   WHERE tmtopic.IsTopicType = 0 AND tmtopic.IsAssociation = 0 AND tmtopic.TopicMapId = " + topicMapId;
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicTypes(int topicMapId) throws SystemException {
        String sql = "";
        sql += "   WHERE tmtopic.IsTopicType = 1 AND tmtopic.TopicMapId = " + topicMapId;
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicsByInstance(Topic instance) throws SystemException {
        String sql = "";
        sql += "   WHERE tmtopic.InstanceOf = '" + instance.getId() + "' ";
        sql += "   AND tmtopic.TopicMapId = " + instance.getTopicMapId();
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicsByContentId(int contentId) throws SystemException {
        String sql = "";
        sql += " INNER JOIN ct2topic ON (tmtopic.TopicId = ct2topic.TopicId) AND (tmtopic.TopicMapId = ct2topic.TopicMapId)";
        sql += "   WHERE ct2topic.ContentId = " + contentId + " AND tmbasename.Scope IS NULL";
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicsBySID(SecurityIdentifier sid) throws SystemException {
        String sql = "";
        sql += " INNER JOIN role2topic ON (tmtopic.TopicId = role2topic.TopicId) AND (tmtopic.TopicMapId = role2topic.TopicMapId)";
        sql += "   WHERE role2topic.Role = '" + sid.getId() + "' AND role2topic.RoleType = '" + sid.getType() + "' AND tmbasename.Scope IS NULL";
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicsByNameAndTopicMapId(String topicName, int topicMapId) throws SystemException {
        List results = new ArrayList();
        String sql = "";
        if (topicName == null) {
            return results;
        }

        topicName = topicName.trim();
        topicName = StringHelper.replace(topicName, "'", "");
        topicName = StringHelper.replace(topicName, "\\", "");

        sql += "   WHERE tmbasename.Basename LIKE '" + topicName + "%' ";
        sql += "   AND tmtopic.TopicMapId = " + topicMapId;

        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public static List<Topic> getTopicsByNameAndInstance(String topicName, Topic instance) throws SystemException {
        List results = new ArrayList();
        String sql = "";
        if (topicName == null) {
            return results;
        }

        topicName = topicName.trim();
        topicName = StringHelper.replace(topicName, "'", "");
        topicName = StringHelper.replace(topicName, "\\", "");

        sql += "   WHERE tmbasename.Basename LIKE '" + topicName + "%' ";

        if (instance != null) {
            sql += "   AND tmtopic.InstanceOf = '" + instance.getId() + "' ";
            sql += "   AND tmtopic.TopicMapId = " + instance.getTopicMapId();
        }

        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

}
