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

import no.kantega.publishing.topicmaps.ao.rowmapper.TopicAssociationRowMapper;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcTopicAssociationDao extends JdbcDaoSupport implements TopicAssociationDao {
    private TopicUsageCounter topicUsageCounter;


    public void deleteTopicAssociation(TopicAssociation association) {
        Topic topicRef   = association.getTopicRef();
        Topic associatedTopicRef = association.getAssociatedTopicRef();

        getJdbcTemplate().update("DELETE FROM tmassociation WHERE TopicMapId = ? AND TopicRef = ? AND AssociatedTopicRef = ?",
                topicRef.getTopicMapId(), topicRef.getId(), associatedTopicRef.getId());
    }

    public void addTopicAssociation(TopicAssociation association) {
        Topic topicRef = association.getTopicRef();
        Topic roleSpec   = association.getRolespec();
        Topic associatedTopicRef = association.getAssociatedTopicRef();

        String instanceOf = "";

        if (topicRef == null || associatedTopicRef == null || roleSpec == null) {
            return;
        }

        if (association.getInstanceOf() == null) {
            instanceOf = getAssociationInstanceOf(topicRef, associatedTopicRef);
        } else {
            instanceOf = association.getInstanceOf().getId();
        }

        getJdbcTemplate().update("INSERT INTO tmassociation VALUES(?,?,?,?,?,?)",
                topicRef.getTopicMapId(),
                instanceOf,
                roleSpec.getId(),
                topicRef.getId(),
                associatedTopicRef.getId(),
                association.isImported() ? 1 : 0
        );

        getJdbcTemplate().update("UPDATE tmtopic SET IsAssociation = 1 WHERE TopicId = ? AND TopicMapId = ?",
                instanceOf,
                topicRef.getTopicMapId());
    }

    private String getAssociationInstanceOf(Topic topicRef, Topic associatedTopicRef) {
        String instanceOf;

        List<String> instanceOfs = getJdbcTemplate().queryForList("SELECT TopicId FROM tmtopic WHERE IsAssociation = 1 AND TopicMapId = ? AND TopicId IN (SELECT TopicId FROM tmbasename WHERE Scope = ? AND TopicMapId = ?) AND TopicId IN (SELECT TopicId FROM tmbasename WHERE Scope = ? AND TopicMapId = ?)",
                String.class,
                topicRef.getTopicMapId(),
                topicRef.getInstanceOf().getId(),
                topicRef.getTopicMapId(),
                associatedTopicRef.getInstanceOf().getId(),
                associatedTopicRef.getTopicMapId());
        if (instanceOfs.size() > 0) {
            instanceOf = instanceOfs.get(0);
        } else {
            instanceOf = "emne-emne";
        }

        return instanceOf;
    }

    public List<TopicAssociation> getTopicAssociations(Topic topic) {

        if (topic == null) {
            return new ArrayList<>();
        }

        String sql = " SELECT distinct tmassociation.InstanceOf, tmassociation.AssociatedTopicRef, tmassociation.Imported, tmbasename.Basename, tmbasename.Scope FROM tmassociation"
                   +    " INNER JOIN tmbasename ON (tmassociation.TopicMapId = tmbasename.TopicMapId) AND (tmassociation.InstanceOf = tmbasename.TopicId) AND (tmassociation.Rolespec = tmbasename.Scope)"
                   + " WHERE (tmassociation.TopicRef = ? AND tmassociation.TopicMapId = ?) ORDER BY tmbasename.Basename";


        List<TopicAssociation> associations = getJdbcTemplate().query(sql, new TopicAssociationRowMapper(topic),
                topic.getId(),
                topic.getTopicMapId());

        sql = " SELECT tmtopic.TopicId, tmtopic.TopicMapId, tmtopic.InstanceOf, tmtopic.SubjectIdentity, tmbasename.Basename, tmbasename.Scope, tmtopic.IsTopicType, tmtopic.IsAssociation"
            + " FROM tmtopic"
            + "   INNER JOIN tmbasename ON (tmtopic.TopicId = tmbasename.TopicId) AND (tmtopic.TopicMapId = tmbasename.TopicMapId)"
            + "   INNER JOIN tmassociation ON (tmtopic.TopicId = tmassociation.TopicRef) AND (tmtopic.TopicMapId = tmassociation.TopicMapId) "
            + " WHERE tmassociation.AssociatedTopicRef = ? AND tmtopic.TopicMapId = ? ORDER BY tmbasename.Basename";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, topic.getId(), topic.getTopicMapId());
        for (Map<String, Object> row : rows) {
            String topicId = (String)row.get("TopicId");
            for (TopicAssociation association : associations) {
                Topic associatedTopic = association.getAssociatedTopicRef();
                if (associatedTopic.getId().equalsIgnoreCase(topicId)) {
                    Topic instanceOf = new Topic();
                    instanceOf.setId((String)row.get("InstanceOf"));
                    associatedTopic.setInstanceOf(instanceOf);
                    associatedTopic.setSubjectIdentity((String)row.get("SubjectIdentity"));
                    associatedTopic.setBaseName((String)row.get("Basename"));
                }
            }
        }

        // Update topics with usage count
        List<Topic> topics = new ArrayList<>();
        for (TopicAssociation a : associations) {
            topics.add(a.getAssociatedTopicRef());
        }

        topicUsageCounter.updateTopicUsageCount(topics);
        return associations;
    }

    public boolean isTopicAssociatedWithInstanceOf(String topicId, int topicMapId, String instanceOf) {
        String sql = "SELECT count(*) FROM tmassociation WHERE (topicref = ? OR associatedtopicref = ?) AND topicmapid = ? AND instanceof = ?";
        return  getJdbcTemplate().queryForObject(sql,Integer.class, topicId,topicId,topicMapId, instanceOf) > 0;
    }

    public void deleteTopicAssociations(Topic topic) {
        getJdbcTemplate().update("DELETE FROM tmassociation WHERE (TopicRef = ? OR AssociatedTopicRef = ?) AND TopicMapId = ?",
                topic.getId(),
                topic.getId(),
                topic.getTopicMapId()
        );
    }

    public void setTopicUsageCounter(TopicUsageCounter topicUsageCounter) {
        this.topicUsageCounter = topicUsageCounter;
    }
}
