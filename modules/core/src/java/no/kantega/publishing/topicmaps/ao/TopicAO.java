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

import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.Role;
import no.kantega.commons.exception.SystemException;

import java.util.List;

@Deprecated
public class TopicAO {
    private static final String AKSESS_TOPIC_DAO = "aksessTopicDao";


    public static Topic getTopic(int topicMapId, String topicId) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getTopic(topicMapId, topicId);
    }

    public static void deleteTopic(Topic topic) throws SystemException {
        TopicDao topicDao = getTopicDao();
        topicDao.deleteTopic(topic);
    }

    public static void deleteTopic(Topic topic, boolean deleteRelatedTables) throws SystemException {
        TopicDao topicDao = getTopicDao();
        topicDao.deleteTopic(topic, deleteRelatedTables);
    }

    public static void setTopic(Topic topic) throws SystemException {
        TopicDao topicDao = getTopicDao();
        topicDao.setTopic(topic);
    }

    
    /**
     * Remove topic from specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public static void removeTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        TopicDao topicDao = getTopicDao();
        topicDao.deleteTopicToContentAssociation(topic, contentId);
    }

    public static void removeTopicSIDAssociation(Topic topic, SecurityIdentifier securityIdentifier) throws SystemException {
        TopicDao topicDao = getTopicDao();
        topicDao.deleteTopicToSecurityIdentifierAssociation(topic, securityIdentifier);
    }

    public static void addTopicSIDAssociation(Topic topic, SecurityIdentifier sid) throws SystemException {
        TopicDao topicDao = getTopicDao();
        topicDao.addTopicToSecurityIdentifierAssociation(topic, sid);
    }

    public static void addTopicContentAssociation(Topic topic, int contentId) {
        TopicDao topicDao = getTopicDao();
        topicDao.addTopicToContentAssociation(topic, contentId);
    }


    public static List<Role> getRolesByTopic(Topic topic) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getRolesForTopic(topic);
    }

    public static List<Topic> getAllTopics() throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getAllTopics();
    }

    public static List<Topic> getTopicsByTopicMapId(int topicMapId) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getTopicsByTopicMapId(topicMapId);
    }

    public static List<Topic> getTopicTypes(int topicMapId) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getTopicTypesForTopicMapId(topicMapId);
    }

    public static List<Topic> getTopicsByInstance(Topic instance) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getTopicsByTopicInstance(instance);
    }

    public static List<Topic> getTopicsByContentId(int contentId) throws SystemException {
        TopicDao topicDao = getTopicDao();

        return topicDao.getTopicsByContentId(contentId);
    }

    public static List<Topic> getTopicsBySID(SecurityIdentifier sid) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getTopicsForSecurityIdentifier(sid);
    }

    public static List<Topic> getTopicsByNameAndTopicMapId(String topicName, int topicMapId) throws SystemException {
        TopicDao topicDao = getTopicDao();
        return topicDao.getTopicsByNameAndTopicMapId(topicName, topicMapId);
    }

    public static List<Topic> getTopicsByNameAndInstance(String topicName, Topic instance) throws SystemException {
        TopicDao topicDao = getTopicDao();

        return topicDao.getTopicsByNameAndTopicInstance(topicName, instance);
    }

    public static void deleteTopicAssociationsForContent(int contentId) {
        TopicDao topicDao = getTopicDao();
        topicDao.deleteTopicAssociationsForContent(contentId);
    }

    private static TopicDao getTopicDao() {
        return (TopicDao) RootContext.getInstance().getBean(AKSESS_TOPIC_DAO);
    }
}
