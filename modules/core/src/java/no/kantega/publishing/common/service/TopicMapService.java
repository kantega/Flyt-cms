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

package no.kantega.publishing.common.service;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.topicmaps.ao.TopicMapAO;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.ao.TopicAssociationAO;
import no.kantega.publishing.topicmaps.data.TopicMap;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class TopicMapService {

    HttpServletRequest request = null;
    SecuritySession securitySession = null;

    public TopicMapService(HttpServletRequest request) throws SystemException {
        this.request = request;
        this.securitySession = SecuritySession.getInstance(request);
    }

    public TopicMapService(SecuritySession securitySession) throws SystemException {
        this.securitySession = securitySession;
    }

    public void deleteTopicMap(int id) throws SystemException, ObjectInUseException {
        TopicMapAO.deleteTopicMap(id);
    }

    public void importTopicMap(int id) throws SystemException {
        TopicMapAO.importTopicMap(id);
    }

    public TopicMap getTopicMap(int id) throws SystemException {
        return TopicMapAO.getTopicMap(id);
    }

    public TopicMap getTopicMapByName(String name) throws SystemException{
        return TopicMapAO.getTopicMapByName(name);
    }

    public TopicMap setTopicMap(TopicMap topicMap) throws SystemException {
        return TopicMapAO.setTopicMap(topicMap);
    }

    public List<TopicMap> getTopicMaps() throws SystemException {
        return TopicMapAO.getTopicMaps();
    }

    public Topic getTopic(int topicMapId, String topicId) throws SystemException {
        return TopicAO.getTopic(topicMapId, topicId);
    }

    public void setTopic(Topic topic) throws SystemException {
        EventLog.log(securitySession, request, Event.SAVE_TOPIC, topic.getBaseName());
        List<TopicOccurence> occurences = topic.getOccurences();
        if (occurences != null) {
            for (TopicOccurence occurence : occurences) {
                // Create topicoccurences instanceof if they do not exist
                if (occurence.getInstanceOf() != null) {
                    Topic instanceOf = occurence.getInstanceOf();
                    if (instanceOf != null && TopicAO.getTopic(topic.getTopicMapId(), instanceOf.getId()) == null) {
                        TopicAO.setTopic(instanceOf);
                    }
                }
            }
        }

        TopicAO.setTopic(topic);
    }

    public void deleteTopic(Topic topic) throws SystemException {
        EventLog.log(securitySession, request, Event.DELETE_TOPIC, topic.getBaseName());
        TopicAO.deleteTopic(topic);
        TopicAssociationAO.deleteTopicAssociations(topic);
    }
    

    public List getTopicsByContentId(int contentId) throws SystemException {
        return TopicAO.getTopicsByContentId(contentId);
    }


    public List<Topic> getAllTopics() throws SystemException {
        return TopicAO.getAllTopics();
    }

    public List<Topic> getTopicsByTopicMapId(int topicMapId) throws SystemException {
        return TopicAO.getTopicsByTopicMapId(topicMapId);
    }

    public List<Topic> getTopicTypes(int topicMapId) throws SystemException {
        return TopicAO.getTopicTypes(topicMapId);
    }

    public List<Topic> getTopicsByInstance(Topic instance) throws SystemException {
        return TopicAO.getTopicsByInstance(instance);
    }

    public List<Topic> getTopicsByNameAndTopicMapId(String topicName, int topicMapId) throws SystemException {
        return TopicAO.getTopicsByNameAndTopicMapId(topicName, topicMapId);
    }

    public List<Topic> getTopicsByNameAndInstance(String topicName, Topic instance) throws SystemException {
        return TopicAO.getTopicsByNameAndInstance(topicName, instance);
    }

    public List<TopicAssociation> getTopicAssociations(Topic atopic) throws SystemException {
        return TopicAssociationAO.getTopicAssociations(atopic);
    }

    public void addTopicAssociation(Topic topic1, Topic topic2) throws SystemException {
        topic1 = TopicAO.getTopic(topic1.getTopicMapId(), topic1.getId());
        topic2 = TopicAO.getTopic(topic2.getTopicMapId(), topic2.getId());

        if (topic1 == null || topic2 == null || topic1.getTopicMapId() != topic2.getTopicMapId()) {
            return;
        }

        // En knytning mellom to emner (topics) g�r alltid begge veier, dette blir representert som to innslag i basen
        TopicAssociation association1 = new TopicAssociation();
        TopicAssociation association2 = new TopicAssociation();

        association1.setTopicRef(topic1);
        association1.setAssociatedTopicRef(topic2);

        association2.setTopicRef(topic2);
        association2.setAssociatedTopicRef(topic1);

        association1.setRolespec(new Topic(topic1.getInstanceOf().getId(), topic1.getInstanceOf().getTopicMapId()));
        association2.setRolespec(new Topic(topic2.getInstanceOf().getId(), topic2.getInstanceOf().getTopicMapId()));

        TopicAssociationAO.addTopicAssociation(association1);
        TopicAssociationAO.addTopicAssociation(association2);
    }


    /**
     * Remove association between two topics
     * @param topic1 - topic 1
     * @param topic2 - topic 2
     * @throws SystemException
     */
    public void removeTopicAssociation(Topic topic1, Topic topic2) throws SystemException {
        TopicAssociation association1 = new TopicAssociation();
        TopicAssociation association2 = new TopicAssociation();

        association1.setTopicRef(topic1);
        association1.setAssociatedTopicRef(topic2);

        association2.setTopicRef(topic2);
        association2.setAssociatedTopicRef(topic1);

        TopicAssociationAO.deleteTopicAssociation(association1);
        TopicAssociationAO.deleteTopicAssociation(association2);
    }

    /**
     * Get topics connected securityidentifier (user or role)
     * @param securityIdentifier - User or Role
     * @return
     * @throws SystemException
     */
    public List<Topic> getTopicsBySID(SecurityIdentifier securityIdentifier) throws SystemException {
        return TopicAO.getTopicsBySID(securityIdentifier);
    }

    /**
     * Get all roles which are connected to this topic
     * @param topic - topic
     * @return - list of <Role>
     * @throws SystemException
     */
    public List<Role> getRolesByTopic(Topic topic) throws SystemException {
        return TopicAO.getRolesByTopic(topic);
    }


    /**
     * Adds topic to specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public void addTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        TopicAO.addTopicContentAssociation(topic, contentId);
    }


    /**
     * Remove topic from specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public void removeTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        TopicAO.removeTopicContentAssociation(topic, contentId);
    }

    /**
     * Add association between User or Role and topic
     * @param topic - Topic
     * @param securityIdentifier - User or Role
     * @throws SystemException
     */
    public void addTopicSIDAssociation(Topic topic, SecurityIdentifier securityIdentifier) throws SystemException {
        TopicAO.addTopicSIDAssociation(topic, securityIdentifier);
    }

    /**
     * Remove association between User or Role and topic
     * @param topic - Topic
     * @param securityIdentifier - User or Role
     * @throws SystemException
     */
    public void removeTopicSIDAssociation(Topic topic, SecurityIdentifier securityIdentifier) throws SystemException {
        TopicAO.removeTopicSIDAssociation(topic, securityIdentifier);
    }

    public List<Topic> getTopicsInUseByChildrenOf(int contentId, int topicMapId) {
        return TopicAO.getTopicsInUseByChildrenOf(contentId, topicMapId);
    }
}
