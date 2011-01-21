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
package no.kantega.publishing.topicmaps.ao;

import no.kantega.publishing.test.database.DerbyDatabaseCreator;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTopicMapDaoTest {
    JdbcTopicMapDao dao;

    @Before
    public void setUp() throws Exception {
        DataSource dataSource = new DerbyDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("dbschema/aksess-database-derby.sql")).createDatabase();
        dao = new JdbcTopicMapDao();
        dao.setDataSource(dataSource);
    }

    @Test
    public void testGetTopicMaps() throws Exception {
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        dao.setTopicMap(topicMap);

        List<TopicMap> topicMaps = dao.getTopicMaps();

        assertTrue("topicMaps.size() > 0", topicMaps.size() > 0);
    }

    @Test
    public void testGetTopicMapById() throws Exception {
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        TopicMap topicMapAfterSave = dao.setTopicMap(topicMap);

        TopicMap topicMap2 = dao.getTopicMapById(topicMapAfterSave.getId());

        assertNotNull("dao.getTopicMapById != null", topicMap2);

        assertEquals("topicMap.name != topicMap2.name", topicMap.getName(), topicMap2.getName());
    }

    @Test
    public void testSetTopicMap() throws Exception {
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        TopicMap topicMapAfterSave = dao.setTopicMap(topicMap);

        assertNotNull("topicMapAfterSave != null", topicMapAfterSave);
        assertTrue("topicMapAfterSave.getId() > 0", topicMapAfterSave.getId() > 0);

    }

    @Test
    public void testGetTopicMapByName() throws Exception {
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        dao.setTopicMap(topicMap);

        TopicMap topicMap2 = dao.getTopicMapByName("My top");

        assertNotNull("dao.getTopicMapByName('My top') != null", topicMap2);

        TopicMap topicMap3 = dao.getTopicMapByName("None");

        assertNull("dao.getTopicMapByName('None') == null", topicMap3);

    }
}
