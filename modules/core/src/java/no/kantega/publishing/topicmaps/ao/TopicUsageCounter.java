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
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopicUsageCounter extends JdbcDaoSupport {
    public void updateTopicUsageCount(List<Topic> topics) throws SystemException {
        if (topics == null || topics.size() == 0) {
            return;
        }

        List<Object> params = new ArrayList<Object>();

        StringBuilder sql = new StringBuilder();

        sql.append("select count(distinct ContentId) as Cnt, TopicId from ct2topic where TopicMapId = ?");
        params.add(topics.get(0).getTopicMapId());

        sql.append(" and TopicId in (");
        for (int i = 0; i < topics.size(); i++) {
            if (i > 0) sql.append(",");
            sql.append("?");
            params.add(topics.get(i).getId());
        }
        sql.append(") group by TopicId");

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql.toString(), params.toArray());
        for (Map row : rows) {
            Number cnt = (Number)row.get("Cnt");
            String topicId = (String)row.get("TopicId");
            for (Topic topic : topics) {
                if (topic.getId().equals(topicId)) {
                    topic.setNoUsages(cnt.intValue());
                }
            }
        }
    }
}
