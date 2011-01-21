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
    public void shouldGetTopicMapAfterSave() throws Exception {
        // Given
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        // When
        dao.saveOrUpdateTopicMap(topicMap);
        List<TopicMap> topicMapsFromDB = dao.getTopicMaps();

        // Then
        assertEquals(topicMapsFromDB.size(), 1);
    }

    @Test
    public void shouldPreserveTopicMapNameWhenSaved() throws Exception {
        // Given
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        // When
        TopicMap topicMapAfterSave = dao.saveOrUpdateTopicMap(topicMap);
        TopicMap topicMapFromDB = dao.getTopicMapById(topicMapAfterSave.getId());

        // Then
        assertNotNull("topicMapFromDB != null", topicMapFromDB);
        assertEquals(topicMap.getName(), topicMapFromDB.getName());
    }

    @Test
    public void shouldUpdateNewTopicMapNameWhenSaved() throws Exception {
        // Given
        TopicMap originalTopicMap = new TopicMap();
        originalTopicMap.setName("My topicmap");
        originalTopicMap = dao.saveOrUpdateTopicMap(originalTopicMap);

        // When
        originalTopicMap.setName("New name");
        dao.saveOrUpdateTopicMap(originalTopicMap);
        TopicMap topicMapAfterChange = dao.getTopicMapById(originalTopicMap.getId());

        // Then
        assertEquals(originalTopicMap.getName(), topicMapAfterChange.getName());
    }

    @Test
    public void shouldSetIdWhenTopicMapIsSaved() throws Exception {
        // Given
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        // When
        TopicMap topicMapFromDB = dao.saveOrUpdateTopicMap(topicMap);

        // Then
        assertNotNull("topicMapAfterSave != null", topicMapFromDB);
        assertTrue("topicMapAfterSave.getId() > 0", topicMapFromDB.getId() > 0);

    }

    @Test
    public void shouldReturnTopicMapByName() throws Exception {
        // Given
        TopicMap topicMap = new TopicMap();
        topicMap.setName("My topicmap");

        // When
        dao.saveOrUpdateTopicMap(topicMap);
        TopicMap topicMapFromDB = dao.getTopicMapByName("My top");

        // Then
        assertNotNull("dao.getTopicMapByName('My top') != null", topicMapFromDB);
    }

    @Test
    public void shouldReturnNullForUnknownTopicMapName() throws Exception {
        // Given
        String topicName = "Undefined";

        // When
        TopicMap topicMap3 = dao.getTopicMapByName(topicName);

        // Then
        assertNull("dao.getTopicMapByName('None') == null", topicMap3);

    }


    @Test
    public void shouldReturnNullWhenTopicMapIsDeleted() throws Exception {
        // Given
        TopicMap topicMap = new TopicMap();
        topicMap.setName("shouldReturnNullWhenTopicMapIsDeleted");

        TopicMap topicMapFromDB = dao.saveOrUpdateTopicMap(topicMap);

        // When
        dao.deleteTopicMap(topicMapFromDB.getId());
        List<TopicMap> topicMaps = dao.getTopicMaps();

        // Then
        assertEquals(0, topicMaps.size());
    }
}
