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
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopicAssociationAO {
    private static final String SOURCE = "aksess.TopicAssociationAO";

    public static void deleteTopicAssociation(TopicAssociation association) throws SystemException {
        Connection c = null;

        Topic topicRef   = association.getTopicRef();
        Topic associatedTopicRef = association.getAssociatedTopicRef();

        try {
            c = dbConnectionFactory.getConnection();
            if (topicRef == null || associatedTopicRef == null) {
                return;
            }

            PreparedStatement st = c.prepareStatement("DELETE FROM tmassociation WHERE TopicMapId = ? AND TopicRef = ? AND AssociatedTopicRef = ?");
            st.setInt(1, topicRef.getTopicMapId());
            st.setString(2, topicRef.getId());
            st.setString(3, associatedTopicRef.getId());
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

    public static void addTopicAssociation(TopicAssociation association) throws SystemException {
        Topic topicRef = association.getTopicRef();
        Topic roleSpec   = association.getRolespec();
        Topic associatedTopicRef = association.getAssociatedTopicRef();

        String instanceOf = "";


        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            if (topicRef == null || associatedTopicRef == null || roleSpec == null) {
                return;
            }

            PreparedStatement st;

            if (association.getInstanceOf() == null) {
                // Finn id til knytningstype mellom emner
                st = c.prepareStatement("SELECT TopicId FROM tmtopic WHERE IsAssociation = 1 AND TopicMapId = ? AND TopicId IN (SELECT TopicId FROM tmbasename WHERE Scope = ? AND TopicMapId = ?) AND TopicId IN (SELECT TopicId FROM tmbasename WHERE Scope = ? AND TopicMapId = ?)");
                st.setInt(1, topicRef.getTopicMapId());
                st.setString(2, topicRef.getInstanceOf().getId());
                st.setInt(3, topicRef.getTopicMapId());
                st.setString(4, associatedTopicRef.getInstanceOf().getId());
                st.setInt(5, associatedTopicRef.getTopicMapId());
                ResultSet rs = st.executeQuery();
                if(rs.next()) {
                    // Må sjekke om samme id finnes for begge scopes, i såfall så er det rett  ...
                    instanceOf = rs.getString("TopicId");
                } else {
                    // Legger inn en default knytning
                    instanceOf = "emne-emne";
                }
            } else {
                instanceOf = association.getInstanceOf().getId();
            }

            st = c.prepareStatement("INSERT INTO tmassociation VALUES(?,?,?,?,?)");
            st.setInt(1, topicRef.getTopicMapId());
            st.setString(2, instanceOf);
            st.setString(3, roleSpec.getId());
            st.setString(4, topicRef.getId());
            st.setString(5, associatedTopicRef.getId());
            st.execute();

            // Topics som andre er knytninger merkes med et flagg
            st = c.prepareStatement("UPDATE tmtopic SET IsAssociation = 1 WHERE TopicId = ? AND TopicMapId = ?");
            st.setString(1, instanceOf);
            st.setInt(2, topicRef.getTopicMapId());
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

    public static List<TopicAssociation> getTopicAssociations(Topic topic) throws SystemException {
        List<TopicAssociation> associations = new ArrayList<TopicAssociation>();

        if (topic == null) {
            return associations;
        }

        String sql = "";

        sql += " SELECT distinct tmassociation.InstanceOf, tmassociation.AssociatedTopicRef, tmbasename.Basename, tmbasename.Scope FROM tmassociation";
        sql += "   INNER JOIN tmbasename ON (tmassociation.TopicMapId = tmbasename.TopicMapId) AND (tmassociation.InstanceOf = tmbasename.TopicId) AND (tmassociation.Rolespec = tmbasename.Scope)";
        sql += " WHERE (tmassociation.TopicRef = ? AND tmassociation.TopicMapId = ?) ORDER BY tmbasename.Basename";

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement st = c.prepareStatement(sql);
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                TopicAssociation association = new TopicAssociation();

                // Type knytning
                Topic instanceOf = new Topic();
                instanceOf.setId(rs.getString("InstanceOf"));
                association.setInstanceOf(instanceOf);
                association.setTopicRef(topic);


                // Legg til tilknyttet topic
                Topic atopic = new Topic();
                atopic.setId(rs.getString("AssociatedTopicRef"));
                atopic.setTopicMapId(topic.getTopicMapId());
                association.setAssociatedTopicRef(atopic);


                // Legg til rolle mellom knytninger
                Topic rolespec = new Topic();
                rolespec.setBaseName(rs.getString("Basename"));
                rolespec.setId(rs.getString("Scope"));
                association.setRolespec(rolespec);

                // Legg til i liste
                associations.add(association);
            }

            // Hent detaljer om topic
            sql = "";
            sql += " SELECT tmtopic.TopicId, tmtopic.TopicMapId, tmtopic.InstanceOf, tmtopic.SubjectIdentity, tmbasename.Basename, tmbasename.Scope, tmtopic.IsTopicType, tmtopic.IsAssociation";
            sql += " FROM tmtopic";
            sql += "   INNER JOIN tmbasename ON (tmtopic.TopicId = tmbasename.TopicId) AND (tmtopic.TopicMapId = tmbasename.TopicMapId)";
            sql += "   INNER JOIN tmassociation ON (tmtopic.TopicId = tmassociation.TopicRef) AND (tmtopic.TopicMapId = tmassociation.TopicMapId) ";
            sql += " WHERE tmassociation.AssociatedTopicRef = ? AND tmtopic.TopicMapId = ? ORDER BY tmbasename.Basename";
            st = c.prepareStatement(sql);
            st.setString(1, topic.getId());
            st.setInt(2, topic.getTopicMapId());
            rs = st.executeQuery();
            while (rs.next()) {
                String topicId = rs.getString("TopicId");
                for (int i = 0; i < associations.size(); i++) {
                    TopicAssociation a = (TopicAssociation)associations.get(i);
                    Topic atopic = a.getAssociatedTopicRef();
                    if (atopic.getId().equalsIgnoreCase(topicId)) {
                        Topic instanceOf = new Topic();
                        instanceOf.setId(rs.getString("InstanceOf"));
                        atopic.setInstanceOf(instanceOf);
                        atopic.setSubjectIdentity(rs.getString("SubjectIdentity"));
                        atopic.setBaseName(rs.getString("Basename"));
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


        // Update topics with usage count
        List<Topic> topics = new ArrayList<Topic>();
        for (TopicAssociation a : associations) {
            topics.add(a.getAssociatedTopicRef());
        }

        updateTopicUsages(topics);


        return associations;
    }


    public static void deleteTopicAssociations(Topic topic) throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("DELETE FROM tmassociation WHERE (TopicRef = ? OR AssociatedTopicRef = ?) AND TopicMapId = ?");
            st.setString(1, topic.getId());
            st.setString(2, topic.getId());
            st.setInt(3, topic.getTopicMapId());
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


    private static void updateTopicUsages(List<Topic> topics) throws SystemException {
        if (topics == null || topics.size() == 0) {
            return;
        }

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select count(distinct ContentId) as Cnt, TopicId from ct2topic where TopicMapId = ?");
            sql.append(" and TopicId in (");
            for (int i = 0; i < topics.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
            }
            sql.append(") group by TopicId");
            PreparedStatement st = c.prepareStatement(sql.toString());
            st.setInt(1, topics.get(0).getTopicMapId());
            for (int i = 0; i < topics.size(); i++) {
                st.setString(i + 2, topics.get(i).getId());
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int cnt = rs.getInt("Cnt");
                String topicId = rs.getString("TopicId");
                for (Topic topic : topics) {
                    if (topic.getId().equals(topicId)) {
                        topic.setNoUsages(cnt);
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
}
