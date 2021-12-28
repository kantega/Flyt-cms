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
import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.ao.TopicAssociationDao;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.publishing.topicmaps.ao.TopicMapDao;
import no.kantega.publishing.topicmaps.data.*;
import no.kantega.publishing.topicmaps.data.exception.ImportTopicMapException;
import no.kantega.publishing.topicmaps.impl.XTMImportWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static java.util.Objects.nonNull;

public class TopicMapService {
    private static final Logger log = LoggerFactory.getLogger(TopicMapService.class);

    private TopicMapDao topicMapDao;

    private TopicDao topicDao;

    private TopicAssociationDao topicAssociationDao;

    private EventLog eventLog;

    HttpServletRequest request = null;
    SecuritySession securitySession = null;

    public TopicMapService(HttpServletRequest request) throws SystemException {
        this.request = request;
        this.securitySession = SecuritySession.getInstance(request);
        initDao();
    }


    public TopicMapService(SecuritySession securitySession) throws SystemException {
        this.securitySession = securitySession;
        initDao();
    }

    private void initDao() {
        ApplicationContext context = RootContext.getInstance();
        topicAssociationDao = context.getBean(TopicAssociationDao.class);
        topicDao = context.getBean(TopicDao.class);
        topicMapDao = context.getBean(TopicMapDao.class);
        eventLog = context.getBean(EventLog.class);
    }


    public void deleteTopicMap(int id) throws SystemException, ObjectInUseException {
        topicMapDao.deleteTopicMap(id);
    }

    public ImportedTopicMap importTopicMap(int id) throws ImportTopicMapException {
        TopicMap topicMap = topicMapDao.getTopicMapById(id);
        XTMImportWorker importWorker = new XTMImportWorker(id);
        Document document = openDocument(topicMap);
        List<Topic> topics;
        List<TopicAssociation> topicAssociations;
        try {
            topics = importWorker.getTopicsFromDocument(document);
            topicAssociations = importWorker.getTopicAssociationsFromDocument(document);
            for(TopicAssociation topicAssociation: topicAssociations){
                Topic topicRef = topicAssociation.getTopicRef();
                Topic associatedTopicRef = topicAssociation.getAssociatedTopicRef();
                for(Topic topic:topics){
                    if(topic.getSubjectIdentity() != null && topic.getSubjectIdentity().equalsIgnoreCase(topicRef.getId())){
                        topicAssociation.setTopicRef(topic);
                    }
                    if(topic.getSubjectIdentity() != null && topic.getSubjectIdentity().equalsIgnoreCase(associatedTopicRef.getId())){
                        topicAssociation.setAssociatedTopicRef(topic);
                    }
                }
            }
        } catch (TransformerException | XPathException e) {
            throw new ImportTopicMapException("Error importing topic map from url:" + topicMap.getUrl() + ". Verify url and try again.", e);
        }
        return  new ImportedTopicMap(topicMap,topics,topicAssociations);
    }

    public void saveImportedTopicMap(ImportedTopicMap importedTopicMap) throws ObjectInUseException {
        int topicMapId = importedTopicMap.getTopicMap().getId();
        topicDao.deleteAllImportedTopics(topicMapId);
        for(Topic topic : importedTopicMap.getTopicList()){
            saveImportedTopic(topicMapId, topic);
        }
        for(TopicAssociation topicAssociation: importedTopicMap.getTopicAssociationList()){
            saveImportedAssociation(topicMapId, topicAssociation);
        }
        topicDao.deleteNonexistingTopicsTags(topicMapId);
    }

    private void saveImportedAssociation(int topicMapId, TopicAssociation topicAssociation) {
        log.debug("Saving imported assosication between " + topicAssociation.getTopicRef().getId() + " and " + topicAssociation.getAssociatedTopicRef().getId());
        topicAssociationDao.addTopicAssociation(topicAssociation);

        Topic instanceOf = topicAssociation.getInstanceOf();
        instanceOf.setBaseName("er relatert til");
        for(TopicBaseName topicBaseName: instanceOf.getBaseNames()){
            topicBaseName.setScope(topicAssociation.getRolespec().getId());
        }
        /**
         * When saving the instanceof the association, this may already be saved as the association is a bi-directional.
         * The setTopic method deletes all basenames related to the topic.
         * Therefore the basenames of this instanceof must be added to the existing topic.
         */
        Topic existingInstanceOf = topicDao.getTopic(instanceOf.getTopicMapId(),instanceOf.getId());
        if (existingInstanceOf == null){
            instanceOf.setImported(true);
            instanceOf.setTopicMapId(topicMapId);
            instanceOf.setIsTopicType(true);
            topicDao.setTopic(instanceOf);
        } else {
            existingInstanceOf.getBaseNames().addAll(instanceOf.getBaseNames());
            topicDao.setTopic(existingInstanceOf);
        }
    }

    private void saveImportedTopic(int topicMapId, Topic topic) {
        log.debug("Saving imported topic: {}", topic.getBaseName());
        if (topic.getBaseNames().size() == 0) {
            topic.setBaseName(topic.getId());
        }
        for(TopicBaseName topicBaseName: topic.getBaseNames()){
            if (nonNull(topic.getInstanceOf())) {
                topicBaseName.setScope(topic.getInstanceOf().getId());
            }
        }
        topicDao.setTopic(topic);
        createInstanceOf(topicMapId, topic);
    }

    private void createInstanceOf(int topicMapId, Topic topic) {
        Topic instanceOf = topic.getInstanceOf();
        if(instanceOf != null){
            Topic savedInstanceOf = topicDao.getTopic(topicMapId, instanceOf.getId());
            if(savedInstanceOf == null){
                if(instanceOf.getBaseName() == null || instanceOf.getBaseName().isEmpty()){
                    instanceOf.setBaseName(instanceOf.getId());
                }
                instanceOf.setImported(true);
                instanceOf.setTopicMapId(topicMapId);
                instanceOf.setIsTopicType(true);
                topicDao.setTopic(instanceOf);
            }
        }
    }

    private Document openDocument(TopicMap topicMap) throws ImportTopicMapException {
        Document doc;
        try {
            doc = XMLHelper.openDocument(new URL(topicMap.getUrl()));
        } catch (MalformedURLException | SystemException e) {
            throw new ImportTopicMapException("Error importing topic map from url:" + topicMap.getUrl() + ". Verify url and try again.", e);
        }

        return doc;
    }

    public TopicMap getTopicMap(int id) throws SystemException {
        return topicMapDao.getTopicMapById(id);
    }

    public TopicMap getTopicMapByName(String name) throws SystemException{
        return topicMapDao.getTopicMapByName(name);
    }

    public TopicMap setTopicMap(TopicMap topicMap) throws SystemException {
        return topicMapDao.saveOrUpdateTopicMap(topicMap);
    }

    public List<TopicMap> getTopicMaps() throws SystemException {
        return topicMapDao.getTopicMaps();
    }

    public Topic getTopic(int topicMapId, String topicId) throws SystemException {
        return topicDao.getTopic(topicMapId, topicId);
    }

    public void setTopic(Topic topic) throws SystemException {
        eventLog.log(securitySession, request, Event.SAVE_TOPIC, topic.getBaseName());
        List<TopicOccurence> occurences = topic.getOccurences();
        if (occurences != null) {
            for (TopicOccurence occurence : occurences) {
                // Create topicoccurences instanceof if they do not exist
                if (occurence.getInstanceOf() != null) {
                    Topic instanceOf = occurence.getInstanceOf();
                    if (instanceOf != null && topicDao.getTopic(topic.getTopicMapId(), instanceOf.getId()) == null) {
                        topicDao.setTopic(instanceOf);
                    }
                }
            }
        }

        topicDao.setTopic(topic);
    }

    public void deleteTopic(Topic topic) throws SystemException {
        eventLog.log(securitySession, request, Event.DELETE_TOPIC, topic.getBaseName());
        topicDao.deleteTopic(topic);
        topicAssociationDao.deleteTopicAssociations(topic);
    }


    public List<Topic> getTopicsByContentId(int contentId) throws SystemException {
        return topicDao.getTopicsByContentId(contentId);
    }


    public List<Topic> getAllTopics() throws SystemException {
        return topicDao.getAllTopics();
    }

    public List<Topic> getTopicsByTopicMapId(int topicMapId) throws SystemException {
        return topicDao.getTopicsByTopicMapId(topicMapId);
    }

    public List<Topic> getTopicTypes(int topicMapId) throws SystemException {
        return topicDao.getTopicTypesForTopicMapId(topicMapId);
    }

    public List<Topic> getTopicsByInstance(Topic instance) throws SystemException {
        return topicDao.getTopicsByTopicInstance(instance);
    }

    public List<Topic> getTopicsByNameAndTopicMapId(String topicName, int topicMapId) throws SystemException {
        return topicDao.getTopicsByNameAndTopicMapId(topicName, topicMapId);
    }

    public List<Topic> getTopicsByNameAndInstance(String topicName, Topic instance) throws SystemException {
        return topicDao.getTopicsByNameAndTopicInstance(topicName, instance);
    }

    public List<TopicAssociation> getTopicAssociations(Topic atopic) throws SystemException {
        return topicAssociationDao.getTopicAssociations(atopic);
    }

    public void addTopicAssociation(Topic topic1, Topic topic2) throws SystemException {
        topic1 = topicDao.getTopic(topic1.getTopicMapId(), topic1.getId());
        topic2 = topicDao.getTopic(topic2.getTopicMapId(), topic2.getId());

        if (topic1 == null || topic2 == null || topic1.getTopicMapId() != topic2.getTopicMapId()) {
            return;
        }

        // En knytning mellom to emner (topics) går alltid begge veier, dette blir representert som to innslag i basen
        TopicAssociation association1 = new TopicAssociation();
        TopicAssociation association2 = new TopicAssociation();

        association1.setTopicRef(topic1);
        association1.setAssociatedTopicRef(topic2);

        association2.setTopicRef(topic2);
        association2.setAssociatedTopicRef(topic1);

        association1.setRolespec(new Topic(topic1.getInstanceOf().getId(), topic1.getInstanceOf().getTopicMapId()));
        association2.setRolespec(new Topic(topic2.getInstanceOf().getId(), topic2.getInstanceOf().getTopicMapId()));

        topicAssociationDao.addTopicAssociation(association1);
        topicAssociationDao.addTopicAssociation(association2);
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

        topicAssociationDao.deleteTopicAssociation(association1);
        topicAssociationDao.deleteTopicAssociation(association2);
    }

    /**
     * Get topics connected securityidentifier (user or role)
     * @param securityIdentifier - User or Role
     * @return
     * @throws SystemException
     */
    public List<Topic> getTopicsBySID(SecurityIdentifier securityIdentifier) throws SystemException {
        return topicDao.getTopicsForSecurityIdentifier(securityIdentifier);
    }

    /**
     * Get all roles which are connected to this topic
     * @param topic - topic
     * @return - list of <Role>
     * @throws SystemException
     */
    public List<Role> getRolesByTopic(Topic topic) throws SystemException {
        return topicDao.getRolesForTopic(topic);
    }


    /**
     * Adds topic to specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public void addTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        topicDao.addTopicToContentAssociation(topic, contentId);
    }


    /**
     * Remove topic from specified content id
     * @param topic - topic
     * @param contentId - id of content object
     * @throws SystemException
     */
    public void removeTopicContentAssociation(Topic topic, int contentId) throws SystemException {
        topicDao.deleteTopicToContentAssociation(topic, contentId);
    }

    /**
     * Add association between User or Role and topic
     * @param topic - Topic
     * @param securityIdentifier - User or Role
     * @throws SystemException
     */
    public void addTopicSIDAssociation(Topic topic, SecurityIdentifier securityIdentifier) throws SystemException {
        topicDao.addTopicToSecurityIdentifierAssociation(topic, securityIdentifier);
    }

    /**
     * Remove association between User or Role and topic
     * @param topic - Topic
     * @param securityIdentifier - User or Role
     * @throws SystemException
     */
    public void removeTopicSIDAssociation(Topic topic, SecurityIdentifier securityIdentifier) throws SystemException {
        topicDao.deleteTopicToSecurityIdentifierAssociation(topic, securityIdentifier);
    }

    public List<Topic> getTopicsInUseByChildrenOf(int contentId, int topicMapId) {
        return topicDao.getTopicsInUseByChildrenOf(contentId, topicMapId);
    }

    public boolean isTopicAssociatedWithInstanceOf(Topic topic, String instanceOf){
        return topicAssociationDao.isTopicAssociatedWithInstanceOf(topic.getId(), topic.getTopicMapId(),instanceOf);
    }
}
