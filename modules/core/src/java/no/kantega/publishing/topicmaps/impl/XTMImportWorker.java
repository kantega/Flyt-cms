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

package no.kantega.publishing.topicmaps.impl;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.CachedXPathAPI;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.FileReader;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.XPathHelper;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.ao.TopicAssociationAO;

public class XTMImportWorker {
    private static final String SOURCE = "aksess.XTMImportWorker";

    public int topicMapId = -1;

    public XTMImportWorker(int topicMapId) {
        this.topicMapId = topicMapId;
    }

    private void addTopic (Element elmTopic) throws TransformerException, SystemException {
        Topic topic = new Topic();
        String id = elmTopic.getAttribute("id");
        if (id != null) {
            topic.setId(id);
            topic.setTopicMapId(topicMapId);

            Element topicRef = (Element)XPathAPI.selectSingleNode(elmTopic, "instanceOf/topicRef");
            if (topicRef != null) {
                String instanceOf = topicRef.getAttribute("xlink:href");
                if (instanceOf != null && instanceOf.charAt(0) == '#') {
                    instanceOf = instanceOf.substring(1, instanceOf.length());
                    topic.setInstanceOf(new Topic(instanceOf));
                }
            }

            Element subjectIndicatorRef = (Element)XPathAPI.selectSingleNode(elmTopic, "subjectIdentity/subjectIndicatorRef");
            if (subjectIndicatorRef != null) {
                String subjectIdentity = subjectIndicatorRef.getAttribute("xlink:href");
                if (subjectIdentity != null) {
                    topic.setSubjectIdentity(subjectIdentity);
                }
            }

            // Finn basenames
            NodeList elmBaseNames = XPathAPI.selectNodeList(elmTopic, "baseName");
            if (elmBaseNames.getLength() > 0) {
                List baseNames = new ArrayList();
                for (int i = 0; i < elmBaseNames.getLength(); i++) {
                    Element elmBaseName = (Element)elmBaseNames.item(i);
                    //TODO: legg til støtte for å spørre etter flere språk
                    // hopp over nynorsknoder
                    Element elmScope = (Element)XPathAPI.selectSingleNode(elmBaseName, "scope/subjectIndicatorRef");
                    if (elmScope != null) {
                        String subjectIndRef = elmScope.getAttribute("xlink:href");
                        if (subjectIndRef != null && subjectIndRef.indexOf("#nno") != -1) {
                            continue;
                        }
                    }
                    
                    TopicBaseName baseName = new TopicBaseName();

                    String name  = XPathHelper.getString(elmBaseName, "baseNameString");
                    baseName.setBaseName(name);

                    String scope = null;
                    topicRef = (Element)XPathAPI.selectSingleNode(elmBaseName, "scope/subjectIndicatorRef");
                    if (topicRef == null) {
                        topicRef = (Element)XPathAPI.selectSingleNode(elmBaseName, "scope/topicRef"); 
                    }
                    if (topicRef != null) {
                        scope = topicRef.getAttribute("xlink:href");
                        if (scope != null && scope.charAt(0) == '#') {
                            scope = scope.substring(1, scope.length());
                        }
                    }
                    baseName.setScope(scope);

                    baseNames.add(baseName);
                }
                topic.setBaseNames(baseNames);
            }

            // Finn occurences
            NodeList elmOccurrences = XPathAPI.selectNodeList(elmTopic, "occurrence");
            List occurences = new ArrayList();
            for (int i = 0; i < elmOccurrences.getLength(); i++) {
                Element elmOccurrence = (Element)elmOccurrences.item(i);
                TopicOccurence occurence = new TopicOccurence();

                String resourceData  = XPathHelper.getString(elmOccurrence, "resourceData");
                occurence.setResourceData(resourceData);

                String instanceOf = null;
                topicRef = (Element)XPathAPI.selectSingleNode(elmOccurrence, "instanceOf/topicRef");
                if (topicRef != null) {
                    instanceOf = topicRef.getAttribute("xlink:href");
                    if (instanceOf != null && instanceOf.charAt(0) == '#') {
                        instanceOf = instanceOf.substring(1, instanceOf.length());
                    }
                    occurence.setInstanceOf(new Topic(instanceOf));
                }
                occurences.add(occurence);
            }
            topic.setOccurences(occurences);

            // Slett knytninger, da disse legges inn på nytt
            TopicAssociationAO.deleteTopicAssociations(topic);

            // Legg til i base
            TopicAO.setTopic(topic);
        }
    }

    private void addAssociation(Element elmAssociation) throws TransformerException, SystemException {
        // Topics er knyttet begge veier, opprettes som to topic associations
        TopicAssociation association1 = new TopicAssociation();
        TopicAssociation association2 = new TopicAssociation();

        // Instans
        Element topicRef = (Element)XPathAPI.selectSingleNode(elmAssociation, "instanceOf/topicRef");
        if (topicRef != null) {
            String instanceOf = topicRef.getAttribute("xlink:href");
            if (instanceOf != null && instanceOf.charAt(0) == '#') {
                instanceOf = instanceOf.substring(1, instanceOf.length());
                association1.setInstanceOf(new Topic(instanceOf));
                association2.setInstanceOf(new Topic(instanceOf));
            }
        }

        NodeList elmMembers = XPathAPI.selectNodeList(elmAssociation, "member");
        if (elmMembers.getLength() == 2) {
            Element member1 = (Element)elmMembers.item(0);
            Element member2 = (Element)elmMembers.item(1);

            Topic topic1 = new Topic();
            topic1.setTopicMapId(topicMapId);

            Topic topic2 = new Topic();
            topic2.setTopicMapId(topicMapId);

            topicRef = (Element)XPathAPI.selectSingleNode(member1, "topicRef");
            if (topicRef != null) {
                String id = topicRef.getAttribute("xlink:href");
                if (id != null && id.charAt(0) == '#') {
                    id = id.substring(1, id.length());
                    topic1.setId(id);
                }
            }

            topicRef = (Element)XPathAPI.selectSingleNode(member2, "topicRef");
            if (topicRef != null) {
                String id = topicRef.getAttribute("xlink:href");
                if (id != null && id.charAt(0) == '#') {
                    id = id.substring(1, id.length());
                    topic2.setId(id);
                }
            }

            association1.setTopicRef(topic1);
            association1.setAssociatedTopicRef(topic2);

            association2.setTopicRef(topic2);
            association2.setAssociatedTopicRef(topic1);

            // Rolleforhold
            Element roleSpec = (Element)XPathAPI.selectSingleNode(member1, "roleSpec/topicRef");
            if (roleSpec != null) {
                String instanceOf = roleSpec.getAttribute("xlink:href");
                if (instanceOf != null && instanceOf.charAt(0) == '#') {
                    instanceOf = instanceOf.substring(1, instanceOf.length());
                    association1.setRolespec(new Topic(instanceOf));
                }
            }

            roleSpec = (Element)XPathAPI.selectSingleNode(member2, "roleSpec/topicRef");
            if (roleSpec != null) {
                String instanceOf = roleSpec.getAttribute("xlink:href");
                if (instanceOf != null && instanceOf.charAt(0) == '#') {
                    instanceOf = instanceOf.substring(1, instanceOf.length());
                    association2.setRolespec(new Topic(instanceOf));
                }
            }

            TopicAssociationAO.addTopicAssociation(association1);
            TopicAssociationAO.addTopicAssociation(association2);

        }
    }

    public void importXTM(Document xmlTopicMap) throws SystemException {

        try {
            // Legg inn topics
            NodeList topics = new CachedXPathAPI().selectNodeList(xmlTopicMap.getDocumentElement(), "topic");
            for (int i = 0; i < topics.getLength(); i++) {
                Element elmTopic = (Element)topics.item(i);
                addTopic(elmTopic);
            }
            System.out.println("Antall topics:" + topics.getLength());

            // Legg inn
            NodeList associations = XPathAPI.selectNodeList(xmlTopicMap.getDocumentElement(), "association");
            for (int i = 0; i < associations.getLength(); i++) {
                Element elmAssociation = (Element)associations.item(i);
                addAssociation(elmAssociation);
            }
            System.out.println("Antall associations:" + associations.getLength());

        } catch (TransformerException e) {
            throw new SystemException("Uventet XML/XPath feil", SOURCE, e);
        }
    }

    public static void main(String args[]) throws Exception {
        String filename = "c:\\livsit\\structure.xml";

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new FileReader(filename)));

        long start = new Date().getTime();
        XTMImportWorker xtmworker = new XTMImportWorker(1);
        xtmworker.importXTM(doc);
        long end = new Date().getTime();
        System.out.println("Tidsforbruk:" + (end-start) + " ms");
    }
}
