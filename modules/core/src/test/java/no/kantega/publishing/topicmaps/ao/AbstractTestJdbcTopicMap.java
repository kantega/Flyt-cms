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

import no.kantega.publishing.test.database.DerbyDatabaseCreator;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;

import javax.sql.DataSource;

public class AbstractTestJdbcTopicMap {
    protected JdbcTopicDao topicDao;
    protected TopicMap topicMap;
    protected Topic instanceOf;
    protected DataSource dataSource;

    public void setUp() throws Exception {
        dataSource = new DerbyDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("dbschema/aksess-database-derby.sql")).createDatabase();
        JdbcTopicMapDao topicMapDao = new JdbcTopicMapDao();
        topicMapDao.setDataSource(dataSource);

        TopicMap newTopicMap = new TopicMap();
        newTopicMap.setName("My topicmap");
        topicMap = topicMapDao.saveOrUpdateTopicMap(newTopicMap);

        topicDao = new JdbcTopicDao();
        topicDao.setDataSource(dataSource);

        TopicUsageCounter usageCounter = new TopicUsageCounter();
        usageCounter.setDataSource(dataSource);
        topicDao.setTopicUsageCounter(usageCounter);

        instanceOf = new Topic("topic", topicMap.getId());
        instanceOf.setIsTopicType(false);
        instanceOf.setBaseName("topic");
        instanceOf.setIsSelectable(true);
    }

}
