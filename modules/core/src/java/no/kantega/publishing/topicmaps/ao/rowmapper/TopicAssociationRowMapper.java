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

package no.kantega.publishing.topicmaps.ao.rowmapper;

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TopicAssociationRowMapper implements RowMapper<TopicAssociation> {
    private Topic topic;

    public TopicAssociationRowMapper(Topic topic) {
        this.topic = topic;
    }

    public TopicAssociation mapRow(ResultSet rs, int i) throws SQLException {
        TopicAssociation topicAssociation = new TopicAssociation();

        // Type knytning
        Topic instanceOf = new Topic();
        instanceOf.setId(rs.getString("InstanceOf"));
        topicAssociation.setInstanceOf(instanceOf);
        topicAssociation.setTopicRef(topic);


        // Legg til tilknyttet topic
        Topic atopic = new Topic();
        atopic.setId(rs.getString("AssociatedTopicRef"));
        atopic.setTopicMapId(topic.getTopicMapId());
        topicAssociation.setAssociatedTopicRef(atopic);


        // Legg til rolle mellom knytninger
        Topic rolespec = new Topic();
        rolespec.setBaseName(rs.getString("Basename"));
        rolespec.setId(rs.getString("Scope"));
        topicAssociation.setRolespec(rolespec);

        return topicAssociation;
    }
}
