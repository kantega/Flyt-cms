
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTopicMapDao extends NamedParameterJdbcDaoSupport implements TopicMapDao {
    private TopicMapRowMapper rowMapper = new TopicMapRowMapper();

    public List<TopicMap> getTopicMaps() {
        return getJdbcTemplate().query("SELECT * FROM tmmaps ORDER BY Name", rowMapper);
    }

    public TopicMap getTopicMapById(int topicMapId) {
        return getJdbcTemplate().queryForObject("SELECT * FROM tmmaps WHERE Id = ?", rowMapper, topicMapId);
    }

    public TopicMap saveOrUpdateTopicMap(final TopicMap topicMap) {

        Map<String, Object> params = new HashMap<>();
        params.put("Name", topicMap.getName());
        params.put("Url", topicMap.getUrl());
        params.put("IsEditable", topicMap.isEditable() ? 1 : 0);
        params.put("WSOperation", topicMap.getWSOperation());
        params.put("WSSoapAction", topicMap.getWSSoapAction());
        params.put("WSEndPoint", topicMap.getWSEndPoint());
        if(topicMap.isNew()){
            SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate());
            insert.setTableName("tmmaps");
            insert.setGeneratedKeyName("ID");
            KeyHolder keyHolder = insert.executeAndReturnKeyHolder(params);
            Number number = keyHolder.getKey();
            topicMap.setId(number.intValue());
        }else{
             params.put("id", topicMap.getId());
            String sql = "UPDATE tmmaps SET Name=:Name, Url=:Url, IsEditable=:IsEditable, WSOperation=:WSOperation, WSSoapAction=:WSSoapAction, WSEndPoint=:WSEndPoint WHERE id=:id";
            getNamedParameterJdbcTemplate().update(sql, params);
        }
        return topicMap;
    }

    public void deleteTopicMap(int topicMapId) throws ObjectInUseException {
        int cnt = getJdbcTemplate().queryForObject("select count(*) from tmtopic where TopicMapId = ?", Integer.class, topicMapId);
        if (cnt > 0) {
            throw new ObjectInUseException("Topic map with id " + topicMapId + " is in use");
        }

        getJdbcTemplate().update("delete from tmmaps where Id = ?", topicMapId);

        getJdbcTemplate().update("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?", topicMapId, ObjectType.TOPICMAP);
    }

    public TopicMap getTopicMapByName(String name) throws SystemException {
        List<TopicMap> topicMaps = getJdbcTemplate().query("SELECT * FROM tmmaps WHERE name like ?", rowMapper, name + "%");
        if (topicMaps.size() > 0) {
            return topicMaps.get(0);
        } else {
            return null;
        }
    }
}
