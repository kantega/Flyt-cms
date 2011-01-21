
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

package no.kantega.publishing.topicmaps.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.topicmaps.ao.rowmapper.TopicMapRowMapper;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

public class JdbcTopicMapDao extends SimpleJdbcDaoSupport implements TopicMapDao {
    private TopicMapRowMapper rowMapper = new TopicMapRowMapper();

    public List<TopicMap> getTopicMaps() {
        return getSimpleJdbcTemplate().query("SELECT * FROM tmmaps ORDER BY Name", rowMapper);
    }

    public TopicMap getTopicMapById(int topicMapId) {
        return getSimpleJdbcTemplate().queryForObject("SELECT * FROM tmmaps WHERE Id = ?", rowMapper, topicMapId);
    }

    public TopicMap setTopicMap(final TopicMap topicMap) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
                PreparedStatement st;
                if (topicMap.isNew()) {
                    st = c.prepareStatement("insert into tmmaps (Name, IsEditable, WSOperation, WSSoapAction, WSEndPoint) values(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                } else {
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

                if (topicMap.isNew()) {
                    // Finn id til nytt objekt
                    ResultSet rs = st.getGeneratedKeys();
                    if (rs.next()) {
                        topicMap.setId(rs.getInt(1));
                    }
                    rs.close();
                }

                return st;
            }
        }, keyHolder);

        if (topicMap.isNew()) {
            topicMap.setId(keyHolder.getKey().intValue());
        }

        return topicMap;
    }

    public void deleteTopicMap(int topicMapId) throws ObjectInUseException {
        int cnt = getSimpleJdbcTemplate().queryForInt("select * from tmtopic where TopicMapId = ?", topicMapId);
        if (cnt > 0) {
            throw new ObjectInUseException(this.getClass().getSimpleName(), "");
        }

        getSimpleJdbcTemplate().update("delete from tmmaps where Id = ?", topicMapId);

        getSimpleJdbcTemplate().update("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?", topicMapId, ObjectType.TOPICMAP);
    }

    public TopicMap getTopicMapByName(String name) throws SystemException {
        List<TopicMap> topicMaps = getSimpleJdbcTemplate().query("SELECT * FROM tmmaps WHERE name like ?", rowMapper, name + "%");
        if (topicMaps.size() > 0) {
            return topicMaps.get(0);
        } else {
            return null;
        }
    }
}
