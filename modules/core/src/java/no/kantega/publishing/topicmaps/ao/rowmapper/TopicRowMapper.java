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

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopicRowMapper implements RowMapper<Topic> {
    public Topic mapRow(ResultSet rs, int i) throws SQLException {
        Topic topic = new Topic();

        topic.setId(rs.getString("TopicId"));
        topic.setTopicMapId(rs.getInt("TopicMapId"));
        topic.setImported(rs.getInt("imported") == 1);

        String instanceOf = rs.getString("InstanceOf");
        if (instanceOf != null) {
            topic.setInstanceOf(new Topic(instanceOf, topic.getTopicMapId()));
        }

        topic.setSubjectIdentity(rs.getString("SubjectIdentity"));

        List<TopicBaseName> baseNames = new ArrayList<TopicBaseName>();

        try {
            TopicBaseName tbn = new TopicBaseName();
            tbn.setBaseName(rs.getString("Basename"));
            tbn.setScope(rs.getString("Scope"));
            baseNames.add(tbn);
            topic.setBaseNames(baseNames);
        } catch (SQLException e) {
            // Fields are not specified in query
        }

        topic.setIsTopicType(rs.getInt("IsTopicType") == 1);
        topic.setIsAssociation(rs.getInt("IsAssociation") == 1);
        topic.setIsSelectable(rs.getInt("IsSelectable") == 1);

        return topic;
    }
}
