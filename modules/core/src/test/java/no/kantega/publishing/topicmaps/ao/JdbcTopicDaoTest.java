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

import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class JdbcTopicDaoTest {
    private Topic description;
    @Autowired
    protected TopicDao topicDao;
    @Autowired
    protected TopicMapDao topicMapDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected TopicMap topicMap;

    protected Topic instanceOf;

    @Before
    public void setUp() throws Exception {
        TopicMap newTopicMap = new TopicMap();
        newTopicMap.setName("My topicmap");
        topicMap = topicMapDao.saveOrUpdateTopicMap(newTopicMap);
        description = new Topic("description", topicMap.getId());
        description.setBaseName("Description");

        instanceOf = new Topic("topic", topicMap.getId());
        instanceOf.setIsTopicType(false);
        instanceOf.setBaseName("topic");
        instanceOf.setIsSelectable(true);
    }

    @Test
    public void persistingTopicShouldPreserveIdAndBasename() throws Exception {

        // Given
        topicDao.setTopic(description);

        // When
        Topic fromDb = topicDao.getTopic(topicMap.getId(), "description");

        // Then
        assertNotNull(fromDb);
        assertEquals(description.getId(), fromDb.getId());
        assertEquals(description.getBaseName(), fromDb.getBaseName());

    }

    @Test
    public void securityIdentifierShouldBeAssociatedWithTopic() {
        // Given
        Topic firstTopic = saveTopic("firsttopic");
        Topic secondTopic = saveTopic("secondtopic");
        final SecurityIdentifier firstUser = new User();
        firstUser.setId("user1");
        final SecurityIdentifier secondUser = new User();
        firstUser.setId("user2");

        // When
       topicDao.addTopicToSecurityIdentifierAssociation(firstTopic, firstUser);
        topicDao.addTopicToSecurityIdentifierAssociation(secondTopic, secondUser);

        // Then
        final List<Topic> foundTopics = topicDao.getTopicsForSecurityIdentifier(firstUser);
        assertEquals(1, foundTopics.size());
        assertEquals(firstTopic.getId(), foundTopics.get(0).getId());
    }


    @Test
    public void deletedSecurityIdentifyerAssociationShouldBeRemoved() {
        // Given
        Topic topic = saveTopic("firsttopic");
        final SecurityIdentifier user = new User();
        user.setId("user1");

        // When
        topicDao.addTopicToSecurityIdentifierAssociation(topic, user);
        topicDao.deleteTopicToSecurityIdentifierAssociation(topic, user);

        // Then
        final List<Topic> foundTopics = topicDao.getTopicsForSecurityIdentifier(user);
        assertEquals(0, foundTopics.size());
    }

    @Test
    public void associatingRoleWithTopicShouldReturnRole() {
        // Given
        Topic topic = saveMyTopic();

        SecurityIdentifier role = new Role();
        role.setId("role");

        SecurityIdentifier user = new User();
        user.setId("user");

        topicDao.addTopicToSecurityIdentifierAssociation(topic, user);
        topicDao.addTopicToSecurityIdentifierAssociation(topic, role);

        // When
        final List<Role> foundRoles = topicDao.getRolesForTopic(topic);

        // Then
        assertEquals(1, foundRoles.size());
        assertEquals(role.getId(), foundRoles.get(0).getId());


    }

    @Test
    public void savingTopicTypeShouldReturnItAsTopicType() {
        // Given
        Topic topic = saveMyTopic();
        topic.setIsTopicType(true);
        topicDao.setTopic(topic);

        // When
        List<Topic> foundTopicTypes = topicDao.getTopicTypesForTopicMapId(topicMap.getId());

        // Then

        assertEquals(1, foundTopicTypes.size());
        assertEquals(topic.getId(), foundTopicTypes.get(0).getId());

    }

    @Test
    public void gettingTopicByInstanceShouldReturnSameInstance() {
        // Given
        Topic one = new Topic("one", topicMap.getId());

        {
            one.setBaseName("one");
            one.setInstanceOf(instanceOf);
            topicDao.setTopic(one);
        }
        {
            Topic two = new Topic("two", topicMap.getId());
            two.setBaseName("two");
            two.setInstanceOf(instanceOf);
            topicDao.setTopic(two);
        }

        // When
        List<Topic> foundTopics = topicDao.getTopicsByTopicInstance(instanceOf);

        // Then
        assertEquals(2, foundTopics.size());
    }

    @Test
    public void gettingTopicsByBasenameAndInstanceShouldReturnTopicsWithSameBasename() {
        // Given
        {
            Topic topic = new Topic("thetopic", topicMap.getId());
            topic.setBaseName("thebasename");
            topic.setInstanceOf(instanceOf);
            topicDao.setTopic(topic);
        }
        {
            Topic anotherTopic = new Topic("anothertopic", topicMap.getId());
            anotherTopic.setBaseName("anotherbasename");
            anotherTopic.setInstanceOf(instanceOf);
            topicDao.setTopic(anotherTopic);
        }


        // When
        List<Topic> foundTopics = topicDao.getTopicsByNameAndTopicInstance("anotherbasename", instanceOf);

        assertEquals(1, foundTopics.size());
        assertEquals("anotherbasename", foundTopics.get(0).getBaseName());

    }
    @Test
    public void gettingTopicsByBasenameAndMapIdShouldReturnTopicsWithSameBasename() {

        // Given
        {
            Topic topic = new Topic("thetopic", topicMap.getId());
            topic.setBaseName("thebasename");
            topic.setInstanceOf(instanceOf);
            topicDao.setTopic(topic);
        }
        {
            Topic anotherTopic = new Topic("anothertopic", topicMap.getId());
            anotherTopic.setBaseName("anotherbasename");
            anotherTopic.setInstanceOf(instanceOf);
            topicDao.setTopic(anotherTopic);
        }


        // When
        List<Topic> foundTopics = topicDao.getTopicsByNameAndTopicMapId("anotherbasename", topicMap.getId());

        assertEquals(1, foundTopics.size());
        assertEquals("anotherbasename", foundTopics.get(0).getBaseName());


    }

    @Test
    public void persistingTopicShouldPreserveOccurrences() throws Exception {

        // Given
        topicDao.setTopic(description);

        Topic topic = new Topic("mytopic", topicMap.getId());
        topic.setBaseName("topic");
        topic.setInstanceOf(instanceOf);


        TopicOccurence myDescription = new TopicOccurence();
        myDescription.setInstanceOf(description);
        myDescription.setResourceData("Topic description");

        topic.addOccurence(myDescription);

        // When
        topicDao.setTopic(topic);

        Topic fromDb = topicDao.getTopic(topicMap.getId(), "mytopic");

        // Then
        assertEquals(1, fromDb.getOccurences().size());
        assertEquals("Topic description", fromDb.getOccurences().get(0).getResourceData());
    }

    @Test
    public void deletingTopicShouldRemoveTopic() {
        // Given
        final Topic topic = saveMyTopic();

        // When
        topicDao.deleteTopic(topic);

        // Then
        assertNull("Topic existed after deletion", topicDao.getTopic(topic.getTopicMapId(), topic.getId()));
    }

    @Test
    public void contentAssociationShouldBePersisted() throws Exception {
        // Given
        Topic topic = saveMyTopic();

        // When
        topicDao.addTopicToContentAssociation(topic, 1);
        List<Topic> associatedTopics = topicDao.getTopicsByContentId(1);

        // Then
        assertEquals(1, associatedTopics.size());
        assertEquals("mytopic", associatedTopics.get(0).getId());
    }

    @Test
    public void deletingTopicToContentAssociationShouldRemoveAssociation() throws Exception {
        // Given
        Topic topic = saveMyTopic();

        topicDao.addTopicToContentAssociation(topic, 1);

        // When
        topicDao.deleteTopicAssociationsForContent(1);
        List<Topic> topics = topicDao.getTopicsByContentId(1);

        // Then
        assertEquals(0, topics.size());
    }

    @Test
    public void deletingAllAssociationsForContentShouldRemoveAssociatons() throws Exception {

        // Given
        Topic topic = saveMyTopic();

        topicDao.addTopicToContentAssociation(topic, 1);

        // When
        topicDao.deleteTopicAssociationsForContent(1);

        List<Topic> foundTopics = topicDao.getTopicsByContentId(1);

        // Then
        assertEquals(0, foundTopics.size());
    }

    @Test
    public void shouldReturnAllTopics() throws Exception {
        // Given
        Topic topic = saveMyTopic();

        // When
        final List<Topic> foundTopics = topicDao.getAllTopics();

        // Then
        assertFalse(foundTopics.isEmpty());
        assertTrue(foundTopics.contains(topic));
    }

    @Test
    public void shouldReturnTopicsByTopicMapId() throws Exception {
        // Given
        saveTopic("topic1");
        saveTopic("topic2");

        // When
        final List<Topic> foundTopics = topicDao.getTopicsByTopicMapId(topicMap.getId());

        // Then
        assertEquals(2, foundTopics.size());
    }

    private Topic saveMyTopic() {
        return saveTopic("mytopic");
    }

    private Topic saveTopic(String id) {
        Topic topic = new Topic(id, topicMap.getId());
        topic.setBaseName("topic");
        topic.setInstanceOf(instanceOf);
        topicDao.setTopic(topic);
        return topic;
    }

    @After
    public void after(){
        deleteFromTables(jdbcTemplate, "tmtopic", "tmbasename", "role2topic", "ct2topic");
    }
}
