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
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
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

    public TopicMap getTopicMap(int id) throws SystemException {
        return TopicMapAO.getTopicMap(id);
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
        // En knytning mellom to emner (topics) går alltid begge veier, dette blir representert som to innslag i basen
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

    public List getTopicsBySID(SecurityIdentifier sid) throws SystemException {
        return TopicAO.getTopicsBySID(sid);
    }

    public List getRolesByTopic(Topic topic) throws SystemException {
        return TopicAO.getRolesByTopic(topic);
    }

    public void removeTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        TopicAO.removeTopicContentAssociation(topic, contentId);
    }

    public void addTopicSIDAssociation(Topic topic, SecurityIdentifier sid) throws SystemException {
        TopicAO.addTopicSIDAssociation(topic, sid);
    }

    public void removeTopicSIDAssociation(Topic topic, SecurityIdentifier sid) throws SystemException {
        TopicAO.removeTopicSIDAssociation(topic, sid);
    }
}
