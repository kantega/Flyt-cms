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
package no.kantega.publishing.topicmaps.ao.rowmapper;

import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TopicMapRowMapper implements RowMapper<TopicMap> {
    public TopicMap mapRow(ResultSet rs, int i) throws SQLException {
        TopicMap topicMap = new TopicMap();

        topicMap.setId(rs.getInt("Id"));
        topicMap.setSecurityId(topicMap.getId());
        topicMap.setName(rs.getString("Name"));
        topicMap.setEditable(rs.getInt("IsEditable") == 1);
        topicMap.setWSOperation(rs.getString("WSOperation"));
        topicMap.setWSSoapAction(rs.getString("WSSoapAction"));
        topicMap.setWSEndPoint(rs.getString("WSEndPoint"));

        return topicMap;
    }
}
