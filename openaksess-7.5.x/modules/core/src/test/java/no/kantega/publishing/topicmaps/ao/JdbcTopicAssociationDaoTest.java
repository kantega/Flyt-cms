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
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class JdbcTopicAssociationDaoTest {
    Topic topic1, topic2;
    TopicAssociation association1, association2;
    @Autowired
    private TopicAssociationDao associationDao;

    @Autowired
    protected TopicDao topicDao;
    @Autowired
    protected TopicMapDao topicMapDao;

    protected TopicMap topicMap;

    protected Topic instanceOf;

    @Before
    public void setUp() throws Exception {
        TopicMap newTopicMap = new TopicMap();
        newTopicMap.setName("My topicmap");
        topicMap = topicMapDao.saveOrUpdateTopicMap(newTopicMap);

        instanceOf = new Topic("topic", topicMap.getId());
        instanceOf.setIsTopicType(false);
        instanceOf.setBaseName("topic");
        instanceOf.setIsSelectable(true);

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

    @Test
    public void shouldHaveAssociation() throws Exception {
        association1.setInstanceOf(instanceOf);
        associationDao.addTopicAssociation(association1);
        boolean topicAssociatedWithOther = associationDao.isTopicAssociatedWithInstanceOf(association1.getTopicRef().getId(), association1.getAssociatedTopicRef().getTopicMapId(),"topic");
        assertTrue("TopicAssociation is added", topicAssociatedWithOther);
    }

    @Test
    public void shouldHaveNoneAssociations() throws Exception {
        associationDao.addTopicAssociation(association1);
        boolean topicAssociatedWithOther = associationDao.isTopicAssociatedWithInstanceOf(association1.getTopicRef().getId(), association1.getAssociatedTopicRef().getTopicMapId(),"topic");
        assertFalse("TopicAssociation is not added", topicAssociatedWithOther);
    }
}
