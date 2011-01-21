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
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class JdbcTopicAssociationDaoTest extends AbstractTestJdbcTopicMap {
    Topic topic1, topic2;
    TopicAssociation association1, association2;
    JdbcTopicAssociationDao associationDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        associationDao = new JdbcTopicAssociationDao();
        associationDao.setDataSource(dataSource);

        TopicUsageCounter topicUsageCounter = new TopicUsageCounter();
        topicUsageCounter.setDataSource(dataSource);
        associationDao.setTopicUsageCounter(topicUsageCounter);

        topic1 = new Topic("topic1", topicMap.getId());
        topic1.setBaseName("topic1");
        topic1.setInstanceOf(instanceOf);
        topicDao.setTopic(topic1);

        topic2 = new Topic("topic2", topicMap.getId());
        topic2.setBaseName("topic2");
        topic2.setInstanceOf(instanceOf);
        topicDao.setTopic(topic2);

        Topic associationType = new Topic("emne-emne", topicMap.getId());
        associationType.setIsAssociation(true);

        List<TopicBaseName> basenames = new ArrayList<TopicBaseName>();
        TopicBaseName basename = new TopicBaseName();
        basename.setBaseName("is related to");
        basename.setScope("topic");
        basenames.add(basename);
        associationType.setBaseNames(basenames);

        topicDao.setTopic(associationType);


        association1 = new TopicAssociation();
        association2 = new TopicAssociation();

        association1.setTopicRef(topic1);
        association1.setAssociatedTopicRef(topic2);

        association2.setTopicRef(topic2);
        association2.setAssociatedTopicRef(topic1);

        association1.setRolespec(new Topic(topic1.getInstanceOf().getId(), topic1.getInstanceOf().getTopicMapId()));
        association2.setRolespec(new Topic(topic2.getInstanceOf().getId(), topic2.getInstanceOf().getTopicMapId()));
    }

    @Test
    public void testDeleteTopicAssociation() throws Exception {
        associationDao.addTopicAssociation(association1);
        associationDao.addTopicAssociation(association2);

        associationDao.deleteTopicAssociation(association1);
        associationDao.deleteTopicAssociation(association2);

        List<TopicAssociation> topicAssociations = associationDao.getTopicAssociations(topic1);
        assertTrue("associationDao.getTopicAssociations(topic1).size == 0 after delete", topicAssociations.size() == 0);
    }

    @Test
    public void testAddAndGetTopicAssociation() throws Exception {
        associationDao.addTopicAssociation(association1);
        associationDao.addTopicAssociation(association2);

        List<TopicAssociation> topicAssociations = associationDao.getTopicAssociations(topic1);
        assertTrue("associationDao.getTopicAssociations(topic1).size == 1 after add", topicAssociations.size() == 1);
    }
}
