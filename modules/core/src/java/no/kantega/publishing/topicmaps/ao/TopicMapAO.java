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

import no.kantega.publishing.topicmaps.data.TopicMap;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class TopicMapAO {
    private static final String SOURCE = "aksess.TopicAO";

    public static TopicMap getTopicMapFromRS(ResultSet rs) throws SQLException {
        TopicMap topicmap = new TopicMap();

        topicmap.setId(rs.getInt("Id"));
        topicmap.setSecurityId(topicmap.getId());
        topicmap.setName(rs.getString("Name"));
        topicmap.setEditable(rs.getInt("IsEditable") == 1 ? true : false);
        topicmap.setWSOperation(rs.getString("WSOperation"));
        topicmap.setWSSoapAction(rs.getString("WSSoapAction"));
        topicmap.setWSEndPoint(rs.getString("WSEndPoint"));

        return topicmap;
    }


    public static List getTopicMaps() throws SystemException {
        Connection c = null;

        List topicmaps = new ArrayList();

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("SELECT * FROM tmmaps ORDER BY Name");
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                TopicMap topicmap = getTopicMapFromRS(rs);
                topicmaps.add(topicmap);
            }
            return topicmaps;
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

    public static TopicMap getTopicMap(int id) throws SystemException {
        Connection c = null;

        TopicMap topicmap = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("SELECT * FROM tmmaps WHERE Id = ?");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()) {
                topicmap = getTopicMapFromRS(rs);
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
        
        return topicmap;
    }

    public static TopicMap setTopicMap(TopicMap topicMap) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = null;
            if (topicMap.getId() == -1) {
                // Ny
                st = c.prepareStatement("insert into tmmaps (Name, IsEditable, WSOperation, WSSoapAction, WSEndPoint) values(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                // Oppdater
                st = c.prepareStatement("update tmmaps set Name = ?, IsEditable = ?, WSOperation = ?, WSSoapAction = ?, WSEndPoint = ? where Id = ?");
            }

            st.setString(1, topicMap.getName());
            st.setInt(2, topicMap.isEditable() ? 1 : 0);
            st.setString(3, topicMap.getWSOperation());
            st.setString(4, topicMap.getWSSoapAction());
            st.setString(5, topicMap.getWSEndPoint());

            if (topicMap.getId() != -1){
                st.setInt(6, topicMap.getId());
            }

            st.execute();

            if (topicMap.getId() == -1) {
                // Finn id til nytt objekt
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    topicMap.setId(rs.getInt(1));
                }
                rs.close();
            }


        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
            throw new SystemException(SOURCE, "Feil ved oppretting av emnekart", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }

        return topicMap;
    }

    public static void deleteTopicMap(int id) throws SystemException, ObjectInUseException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            // Først sjekk om det finnes underlementer
            ResultSet rs = SQLHelper.getResultSet(c, "select * from tmtopic where TopicMapId = " + id);
            if (rs.next()) {
                rs.close();
                rs = null;
                throw new ObjectInUseException(SOURCE, "");
            }

            PreparedStatement st = c.prepareStatement("delete from tmmaps where Id = ?");
            st.setInt(1, id);
            st.execute();
            st.close();

            // Slett eventuelle tilgangsrettigheter
            st = c.prepareStatement("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?");
            st.setInt(1, id);
            st.setInt(2, ObjectType.TOPICMAP);
            st.execute();
            st.close();

        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
            throw new SystemException(SOURCE, "SQL feil ved sletting av emnekart", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }

    }

    public List getAllTopicMaps() {
        return null;
    }   
}
