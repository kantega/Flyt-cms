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

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JdbcTopicDaoTest extends AbstractTestJdbcTopicMap {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGetTopic() throws Exception {
        Topic description = new Topic("description", topicMap.getId());
        description.setBaseName("Description");

        topicDao.setTopic(description);

        Topic description2 = topicDao.getTopic(topicMap.getId(), "description");

        assertNotNull("topicDao.getTopic != null", description2);
        assertEquals("description.getBaseName == description2.getBaseName", description.getBaseName(), description2.getBaseName());

        Topic topic = new Topic("mytopic", topicMap.getId());
        topic.setBaseName("topic");
        topic.setInstanceOf(instanceOf);

        TopicOccurence myDescription = new TopicOccurence();
        myDescription.setInstanceOf(description);
        myDescription.setResourceData("Topic description");

        topic.addOccurence(myDescription);

        topicDao.setTopic(topic);

        Topic topic2 = topicDao.getTopic(topicMap.getId(), "mytopic");

        assertEquals("topic.occurences.size == 1", 1, topic2.getOccurences().size());
        assertEquals("topic.occurences.decription not saved", "Topic description", topic2.getOccurences().get(0).getResourceData());
    }

    @Test
    public void testAddTopicToContentAssociation() throws Exception {
        Topic topic = new Topic("mytopic", topicMap.getId());
        topic.setBaseName("topic");
        topic.setInstanceOf(instanceOf);

        topicDao.setTopic(topic);
        topicDao.addTopicToContentAssociation(topic, 1);

        assertTrue("topicDao.getTopicsByContentId(1).size > 0", topicDao.getTopicsByContentId(1).size() > 0);
    }

    @Test
    public void testDeleteTopicToContentAssociation() throws Exception {
        Topic topic = new Topic("mytopic", topicMap.getId());
        topic.setBaseName("topic");
        topic.setInstanceOf(instanceOf);

        topicDao.setTopic(topic);
        topicDao.addTopicToContentAssociation(topic, 1);

        topicDao.deleteTopicToContentAssociation(topic, 1);

        List<Topic> topics = topicDao.getTopicsByContentId(1);

        assertTrue("topicDao.getTopicsByContentId(1).size == 0", topics.size() == 0);
    }

    @Test
    public void testDeleteTopicAssociationsForContent() throws Exception {
        Topic topic = new Topic("mytopic", topicMap.getId());
        topic.setBaseName("mytopic");
        topic.setInstanceOf(instanceOf);

        topicDao.setTopic(topic);
        topicDao.addTopicToContentAssociation(topic, 1);

        topicDao.deleteTopicAssociationsForContent(1);

        List<Topic> topics = topicDao.getTopicsByContentId(1);

        assertTrue("topicDao.getTopicsByContentId(1).size == 0", topics.size() == 0);
    }

    @Test
    public void testGetAllTopics() throws Exception {
        Topic mytopic = new Topic("mytopic", topicMap.getId());
        mytopic.setBaseName("mytopic");
        mytopic.setInstanceOf(instanceOf);
        topicDao.setTopic(mytopic);

        assertTrue("topicDao.getAllTopics().size() > 0", topicDao.getAllTopics().size() > 0);
    }

    @Test
    public void testGetTopicsByTopicMapId() throws Exception {
        Topic mytopic = new Topic("mytopic", topicMap.getId());
        mytopic.setBaseName("mytopic");
        mytopic.setInstanceOf(instanceOf);

        topicDao.setTopic(mytopic);

        assertTrue("topicDao.getTopicsByTopicMapId(topicMap.getId()).size() > 0", topicDao.getTopicsByTopicMapId(topicMap.getId()).size() > 0);

    }
}
