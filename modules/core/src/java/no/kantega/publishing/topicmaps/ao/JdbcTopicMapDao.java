
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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.topicmaps.ao.rowmapper.TopicMapRowMapper;
import no.kantega.publishing.topicmaps.data.TopicMap;
import no.kantega.publishing.topicmaps.impl.XTMImportWorker;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class JdbcTopicMapDao extends SimpleJdbcDaoSupport implements TopicMapDao {
    private TopicMapRowMapper rowMapper = new TopicMapRowMapper();

    public List<TopicMap> getTopicMaps() {
        return getSimpleJdbcTemplate().query("SELECT * FROM tmmaps ORDER BY Name", rowMapper);
    }

    public TopicMap getTopicMapById(int topicMapId) {
        return getSimpleJdbcTemplate().queryForObject("SELECT * FROM tmmaps WHERE Id = ?", rowMapper, topicMapId);
    }

    public TopicMap saveOrUpdateTopicMap(final TopicMap topicMap) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
                PreparedStatement st;
                if (topicMap.isNew()) {
                    st = c.prepareStatement("insert into tmmaps (Name, Url, IsEditable, WSOperation, WSSoapAction, WSEndPoint) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                } else {
                    st = c.prepareStatement("update tmmaps set Name = ?, Url = ?, IsEditable = ?, WSOperation = ?, WSSoapAction = ?, WSEndPoint = ? where Id = ?");
                }

                int i = 1;
                st.setString(i++, topicMap.getName());
                st.setString(i++, topicMap.getUrl());
                st.setInt(i++, topicMap.isEditable() ? 1 : 0);
                st.setString(i++, topicMap.getWSOperation());
                st.setString(i++, topicMap.getWSSoapAction());
                st.setString(i++, topicMap.getWSEndPoint());

                if (topicMap.getId() != -1){
                    st.setInt(i, topicMap.getId());
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
        int cnt = getSimpleJdbcTemplate().queryForInt("select count(*) from tmtopic where TopicMapId = ?", topicMapId);
        if (cnt > 0) {
            throw new ObjectInUseException(this.getClass().getSimpleName(), "");
        }

        getSimpleJdbcTemplate().update("delete from tmmaps where Id = ?", topicMapId);

        getSimpleJdbcTemplate().update("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?", topicMapId, ObjectType.TOPICMAP);
    }

    public void importTopicMap(int topicMapId) throws SystemException {
        TopicMap map = getTopicMapById(topicMapId);

        try {
            URL file = new URL(map.getUrl());
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(file.openStream());

            long start = new java.util.Date().getTime();
            XTMImportWorker xtmworker = new XTMImportWorker(topicMapId);
            xtmworker.importXTM(doc);
            long end = new java.util.Date().getTime();
            Log.debug(this.getClass().getName(), "Time to import in ms: " + (end - start));
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), "importTopicMap", e);
        }
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
