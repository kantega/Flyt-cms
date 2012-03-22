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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.topicmaps.ao.rowmapper.TopicBaseNameRowMapper;
import no.kantega.publishing.topicmaps.ao.rowmapper.TopicOccurenceRowMapper;
import no.kantega.publishing.topicmaps.ao.rowmapper.TopicRowMapper;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JdbcTopicDao extends SimpleJdbcDaoSupport implements TopicDao {
    private static TopicRowMapper topicRowMapper = new TopicRowMapper();
    private static TopicBaseNameRowMapper topicBaseNameRowMapper = new TopicBaseNameRowMapper();
    private static TopicOccurenceRowMapper topicOccurenceRowMapper = new TopicOccurenceRowMapper();

    private TopicUsageCounter topicUsageCounter;

    public Topic getTopic(int topicMapId, String topicId) {

        List<Topic> topics;
        if (topicMapId != -1) {
            topics = getSimpleJdbcTemplate().query("SELECT * FROM tmtopic WHERE TopicId = ? AND TopicMapId = ?", topicRowMapper, topicId, topicMapId);
        } else {
            topics = getSimpleJdbcTemplate().query("SELECT * FROM tmtopic WHERE TopicId = ?", topicRowMapper, topicId);
        }

        if (topics.size() == 0) {
            return null;
        }

        Topic topic = topics.get(0);

        List<TopicBaseName> baseNames = getSimpleJdbcTemplate().query("SELECT * FROM tmbasename WHERE TopicId = ? AND TopicMapId = ?",
                topicBaseNameRowMapper, topic.getId(), topic.getTopicMapId());

        topic.setBaseNames(baseNames);

        String sql = "";
        sql += " SELECT tmbasename.Basename, tmoccurence.ResourceData, tmoccurence.InstanceOf FROM tmoccurence";
        sql += "   INNER JOIN tmbasename ON (tmoccurence.InstanceOf = tmbasename.TopicId) AND (tmoccurence.TopicMapId = tmbasename.TopicMapId)";
        sql += " WHERE tmoccurence.TopicId = ? AND tmoccurence.TopicMapId = ?";

        List<TopicOccurence> occurences = getSimpleJdbcTemplate().query(sql,
                topicOccurenceRowMapper, topic.getId(), topic.getTopicMapId());

        topic.setOccurences(occurences);

        return topic;
    }

    public void deleteTopic(Topic topic) throws SystemException {
        deleteTopic(topic, true);
    }

    public void deleteTopic(Topic topic, boolean deleteRelatedTables) throws SystemException {
        // Slett eksisterende topic
        getSimpleJdbcTemplate().update("DELETE FROM tmtopic WHERE TopicId = ? AND TopicMapId = ?", topic.getId(), topic.getTopicMapId());

        getSimpleJdbcTemplate().update("DELETE FROM tmbasename WHERE TopicId = ? AND TopicMapId = ?", topic.getId(), topic.getTopicMapId());

        getSimpleJdbcTemplate().update("DELETE FROM tmoccurence WHERE TopicId = ? AND TopicMapId = ?", topic.getId(), topic.getTopicMapId());


        if (deleteRelatedTables) {
            getSimpleJdbcTemplate().update("DELETE FROM role2topic WHERE TopicId = ? AND TopicMapId = ?", topic.getId(), topic.getTopicMapId());

            getSimpleJdbcTemplate().update("DELETE FROM ct2topic WHERE TopicId = ? AND TopicMapId = ?", topic.getId(), topic.getTopicMapId());
        }
    }

    public void deleteAllTopics(int topicMapId) {

        getSimpleJdbcTemplate().update("DELETE FROM tmtopic WHERE TopicMapId = ?", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM tmassociation WHERE TopicMapId = ?", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM tmbasename WHERE TopicMapId = ?", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM tmoccurence WHERE TopicMapId = ?", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM role2topic WHERE TopicMapId = ?", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM ct2topic WHERE TopicMapId = ?", topicMapId);

    }

    public void deleteAllImportedTopics(int topicMapId) {

        getSimpleJdbcTemplate().update("DELETE FROM tmbasename WHERE TopicMapId = ? AND TopicId IN (SELECT TopicId FROM tmtopic WHERE imported = 1)", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM tmoccurence WHERE TopicMapId = ? AND TopicId IN (SELECT TopicId FROM tmtopic WHERE imported = 1)", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM role2topic WHERE TopicMapId = ? AND TopicId IN (SELECT TopicId FROM tmtopic WHERE imported = 1)", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM ct2topic WHERE TopicMapId = ? AND TopicId IN (SELECT TopicId FROM tmtopic WHERE imported = 1)", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM tmtopic WHERE TopicMapId = ? AND imported = 1", topicMapId);

        getSimpleJdbcTemplate().update("DELETE FROM tmassociation WHERE TopicMapId = ? AND imported = 1", topicMapId);

    }

    public void setTopic(Topic topic) {
        // Delete topic without support tables and reinsert
        deleteTopic(topic, false);

        getSimpleJdbcTemplate().update("INSERT INTO tmtopic VALUES (?,?,?,?,?,?,?,?,?)",
                topic.getId(), topic.getTopicMapId(),
                topic.getInstanceOf() == null ? null : topic.getInstanceOf().getId(),
                topic.isTopicType() ? 1 : 0,
                topic.isAssociation() ? 1 : 0,
                new java.sql.Timestamp(new Date().getTime()),
                topic.getSubjectIdentity(),
                topic.isSelectable() ? 1 : 0,
                topic.isImported() ? 1 : 0);

        // Topics som andre er instans av merkes med et flagg
        if (topic.getInstanceOf() != null) {
            Topic instanceOf = topic.getInstanceOf();
            getSimpleJdbcTemplate().update("UPDATE tmtopic SET IsTopicType = 1 WHERE TopicId = ? AND TopicMapId = ?", instanceOf.getId(), instanceOf.getTopicMapId());
        }

        List<TopicBaseName> baseNames = topic.getBaseNames();
        if (baseNames != null) {
            for (TopicBaseName tbn : baseNames) {
                getSimpleJdbcTemplate().update("INSERT INTO tmbasename VALUES(?,?,?,?)", topic.getId(), topic.getTopicMapId(), tbn.getScope(), tbn.getBaseName());
            }
        }

        List<TopicOccurence> occurences = topic.getOccurences();
        if (occurences != null) {
            for (TopicOccurence occurence : occurences) {
                if (occurence.getInstanceOf() != null) {
                    getSimpleJdbcTemplate().update("INSERT INTO tmoccurence VALUES(?,?,?,?)", topic.getId(), topic.getTopicMapId(), occurence.getInstanceOf().getId(), occurence.getResourceData());
                }
            }
        }
    }

    public void addTopicToSecurityIdentifierAssociation(Topic topic, SecurityIdentifier securityIdentifier) {
        if (topic == null || securityIdentifier == null) {
            return;
        }

        int noExisting = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(*) FROM role2topic WHERE TopicMapId = ? AND TopicId = ? AND RoleType = ? AND Role = ?",
                topic.getTopicMapId(), topic.getId(), securityIdentifier.getType(), securityIdentifier.getId());
        if (noExisting == 0) {
            getSimpleJdbcTemplate().update("INSERT INTO role2topic VALUES (?, ?, ?, ?)", topic.getTopicMapId(), topic.getId(), securityIdentifier.getType(), securityIdentifier.getId());
        }
    }

    public void deleteTopicToSecurityIdentifierAssociation(Topic topic, SecurityIdentifier securityIdentifier) {
        getSimpleJdbcTemplate().update("DELETE FROM role2topic WHERE TopicMapId = ? AND TopicId = ? AND RoleType = ? AND Role = ?",
                topic.getTopicMapId(), topic.getId(), securityIdentifier.getType(), securityIdentifier.getId());
    }

    public void addTopicToContentAssociation(Topic topic, int contentId) {
        if (topic == null || contentId == -1) {
            return;
        }

        int noExisting = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(*) FROM ct2topic WHERE TopicMapId = ? AND TopicId = ? AND ContentId = ?",
                topic.getTopicMapId(), topic.getId(), contentId);
        if (noExisting == 0) {
            getSimpleJdbcTemplate().update("INSERT INTO ct2topic VALUES (?, ?, ?)", contentId, topic.getTopicMapId(), topic.getId());
        }
    }

    public void deleteTopicToContentAssociation(Topic topic, int contentId) {
        getSimpleJdbcTemplate().update("DELETE FROM ct2topic WHERE TopicId = ? AND TopicMapId = ? AND ContentId = ?",
                topic.getId(), topic.getTopicMapId(), contentId);
    }

    /**
     * Deletes all topic associations for a given content.
     *
     * @param contentId
     */
    public void deleteTopicAssociationsForContent(int contentId) {
        getSimpleJdbcTemplate().update("DELETE FROM ct2topic WHERE ContentId = ?", contentId);
    }

    public List<Role> getRolesForTopic(Topic topic) {
        List<Role> roles = new ArrayList<Role>();

        List<Map<String, Object>> rows = getSimpleJdbcTemplate().queryForList("SELECT Role FROM role2topic WHERE TopicMapId = ? AND TopicId = ? AND RoleType = ? ORDER BY Role",
                topic.getTopicMapId(), topic.getId(), new Role().getType());

        for (Map<String, Object> row : rows) {
            Role role = new Role();
            role.setId((String)row.get("Role"));
            role.setName(role.getId());
            roles.add(role);
        }

        return roles;
    }


    public List<Topic> getTopicsByContentId(int contentId) {
        String sql = "";
        sql += " INNER JOIN ct2topic ON (tmtopic.TopicId = ct2topic.TopicId) AND (tmtopic.TopicMapId = ct2topic.TopicMapId)";
        sql += "   WHERE ct2topic.ContentId = " + contentId + " AND tmbasename.Scope IS NULL";
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getAllTopics() {
        String sql = "";
        sql += "   WHERE tmtopic.IsTopicType = 0 AND tmtopic.IsAssociation = 0 AND tmtopic.InstanceOf IS NOT NULL";
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getTopicsByTopicMapId(int topicMapId) {
        String defaultScope = null;
        try {
            Configuration config = Aksess.getConfiguration();
            defaultScope = config.getString("topic.defaultscope");
        } catch (Exception e) {
            //No config found. Do nothing as there will be no scope added to sqlquery
        }
        String sql = "";
        sql += "   WHERE tmtopic.IsTopicType = 0 AND tmtopic.IsAssociation = 0 AND tmtopic.InstanceOf IS NOT NULL AND tmtopic.TopicMapId = " + topicMapId;
        if(defaultScope != null){
            //There may be added multiple basenames to a topic where the scope sets a language.
            sql += " AND (tmbasename.Scope like '%" + defaultScope + "%' OR tmbasename.Scope IS NULL)";
        }
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getTopicTypesForTopicMapId(int topicMapId) {
        String sql = "";
        sql += "   WHERE tmtopic.IsTopicType = 1 AND tmtopic.TopicMapId = " + topicMapId;
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getTopicsByTopicInstance(Topic instance) {
        String sql = "";
        sql += "   WHERE tmtopic.InstanceOf = '" + instance.getId() + "' ";
        sql += "   AND tmtopic.TopicMapId = " + instance.getTopicMapId();
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getTopicsForSecurityIdentifier(SecurityIdentifier sid) {
        String sql = "";
        sql += " INNER JOIN role2topic ON (tmtopic.TopicId = role2topic.TopicId) AND (tmtopic.TopicMapId = role2topic.TopicMapId)";
        sql += "   WHERE role2topic.Role = '" + sid.getId() + "' AND role2topic.RoleType = '" + sid.getType() + "' AND tmbasename.Scope IS NULL";
        sql += "   ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getTopicsByNameAndTopicMapId(String topicName, int topicMapId) {
        String sql = "";
        if (topicName == null) {
            return new ArrayList<Topic>();
        }

        topicName = topicName.trim();
        topicName = StringHelper.replace(topicName, "'", "");
        topicName = StringHelper.replace(topicName, "\\", "");

        sql += " WHERE tmbasename.Basename LIKE '" + topicName + "%' ";

        if (topicMapId != -1) {
            sql += "   AND tmtopic.TopicMapId = " + topicMapId;
        }

        sql += " AND tmtopic.InstanceOf IS NOT NULL ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }

    public List<Topic> getTopicsByNameAndTopicInstance(String topicName, Topic instance) {
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

        sql += " AND tmtopic.InstanceOf IS NOT NULL ORDER BY tmbasename.Basename";

        return getTopicsBySQLStatement(sql);
    }


    private List<Topic> getTopicsBySQLStatement(String whereClause) {
        String sql = "";
        sql += " SELECT tmtopic.TopicId, tmtopic.TopicMapId, tmtopic.InstanceOf, tmtopic.SubjectIdentity, tmtopic.IsSelectable, tmbasename.Basename, tmbasename.Scope, tmtopic.IsTopicType, tmtopic.IsAssociation, tmtopic.imported";
        sql += "   FROM tmtopic";
        sql += " INNER JOIN tmbasename ON (tmtopic.TopicId = tmbasename.TopicId) AND (tmtopic.TopicMapId = tmbasename.TopicMapId)";

        List<Topic> topics = getSimpleJdbcTemplate().query(sql + whereClause, topicRowMapper);

        // Update with usage count
        topicUsageCounter.updateTopicUsageCount(topics);

        return topics;
    }

    public List<Topic> getTopicsInUseByChildrenOf(int contentId, final int topicMapId) {
        String sql = "SELECT DISTINCT t.topicid,b.topicmapid,b.basename,tp.instanceof FROM associations a RIGHT JOIN content c ON a.contentid=c.contentid RIGHT JOIN ct2topic t ON a.contentid=t.contentid JOIN tmtopic tp ON t.topicid=tp.topicid AND t.topicmapid=tp.topicmapid RIGHT JOIN tmbasename b ON t.topicid=b.topicid AND t.topicmapid=b.topicmapid WHERE c.expiredate > ? AND t.topicmapid=? AND a.isdeleted=0 AND a.path like ? ORDER BY t.topicid";
        SimpleJdbcTemplate template = getSimpleJdbcTemplate();
        return template.query(sql, new RowMapper<Topic>() {
            public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
                Topic t = new Topic();
                t.setId(rs.getString("topicid"));
                t.setBaseName(rs.getString("basename"));
                Topic instanceOf = new Topic();
                instanceOf.setId(rs.getString("instanceof"));
                instanceOf.setTopicMapId(topicMapId);
                t.setInstanceOf(instanceOf);
                t.setTopicMapId(topicMapId);
                return t;
            }
        }, new Date(), topicMapId, "%/" + contentId + "/%");
    }

    public void setTopicUsageCounter(TopicUsageCounter topicUsageCounter) {
        this.topicUsageCounter = topicUsageCounter;
    }
}
