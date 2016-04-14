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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.util.XPathHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

public class XTMImportWorker{
    private static final Logger log = LoggerFactory.getLogger(XTMImportWorker.class);

    //In xtm 1.0 attribute xlink:href is used, in xtm 2.0 attribute href is used.
    private static final String[] ATTRIBUTE_HREF = {"xlink:href","href"};
    public int topicMapId = -1;

    public XTMImportWorker(int topicMapId) {
        this.topicMapId = topicMapId;
    }

    public List<Topic> getTopicsFromDocument(Document document) throws TransformerException, XPathExpressionException {
        NodeList topics = (NodeList)XPathFactory.newInstance().newXPath().evaluate("//topic", document, XPathConstants.NODESET);
        List<Topic> topicList = new ArrayList<>(topics.getLength());
        for (int i = 0; i < topics.getLength(); i++) {
            Element elmTopic = (Element)topics.item(i);
            Topic topic = getTopicFromElement(elmTopic);
            topic.setImported(true);

            if (shouldTopicBeImported(topic)){
                topicList.add(topic);
            }
        }
        return topicList;
    }

    private boolean shouldTopicBeImported(Topic topic) {
        //Ignore topics that don't have subjectidenty and instanceof
        boolean shouldBeImported = topic.getInstanceOf() != null || topic.getSubjectIdentity() != null;
        if( ! shouldBeImported){
            log.debug("Skipping topics that don't have subjectidenty and instanceof: " + topic.getId());
        }
        return shouldBeImported;
    }

    private Topic getTopicFromElement(Element topicElement) throws TransformerException, XPathExpressionException {
        Topic topic = new Topic();
        String id = topicElement.getAttribute("id");
        if (id != null) {
            topic.setId(removeIdPrefix(id));
            topic.setTopicMapId(topicMapId);

            String instanceOf = getAttributeValue(topicElement, ATTRIBUTE_HREF, "instanceOf/topicRef","instanceOf/subjectIndicatorRef");
            if(instanceOf != null){
                instanceOf = removeLeadingSquare(instanceOf);
                instanceOf = removeIdPrefix(instanceOf);
                topic.setInstanceOf(new Topic(instanceOf, topicMapId));
            }

            String subjectIdentity = getAttributeValue(topicElement,ATTRIBUTE_HREF, "subjectIdentity/subjectIndicatorRef", "subjectIdentifier");

            if (subjectIdentity != null) {
                subjectIdentity = removeIdPrefix(subjectIdentity);
                topic.setSubjectIdentity(subjectIdentity);
            }

            List<TopicBaseName> baseNames = getBaseNamesForTopic(topicElement);
            topic.setBaseNames(baseNames);

            List<TopicOccurence> occurences = getOccurencesForTopic(topicElement);
            topic.setOccurences(occurences);
        }
        return topic;
    }

    private String getAttributeValue(Element element, String[] attributes, String... xpaths)
            throws TransformerException, XPathExpressionException {
        String attributeValue = null;
        XPathFactory factory = XPathFactory.newInstance();
        for(String xpath: xpaths){
            Element attributeElement = (Element)factory.newXPath().evaluate(xpath, element, XPathConstants.NODE);
            if (attributeElement != null) {
                int index = 0;
                while ((attributeValue == null || attributeValue.isEmpty()) && index < attributes.length){
                    attributeValue = attributeElement.getAttribute(attributes[index]);
                    index++;
                }
            }
        }

        return attributeValue;
    }

    private String removeLeadingSquare(String value){
        if(value != null && value.length() > 0 && value.charAt(0) == '#' ){
            value = value.substring(1, value.length());
        }
        return value;
    }

    private List<TopicBaseName> getBaseNamesForTopic(Element topicElement)
            throws TransformerException, XPathExpressionException {
        NodeList elmBaseNames = selectNodeList(topicElement, "baseName", "name");
        List<TopicBaseName> baseNames = new ArrayList<>(elmBaseNames.getLength());
        for (int i = 0; i < elmBaseNames.getLength(); i++) {
            Element elmBaseName = (Element)elmBaseNames.item(i);

            TopicBaseName baseName = new TopicBaseName();
            String name  = getString(elmBaseName, "baseNameString", "value");
            baseName.setBaseName(name);

            String scope = getAttributeValue(elmBaseName, ATTRIBUTE_HREF, "scope/subjectIndicatorRef", "scope/topicRef");
            if (scope != null) {
                //TODO: Add support to query for mulitiple languages
                // skip nynorsk, samisk and english
                if (notWantedLanguage(scope)) {
                    continue;
                }
                scope = removeLeadingSquare(scope);
                baseName.setScope(scope);
            }
            baseNames.add(baseName);
        }
        return baseNames;
    }

    private boolean notWantedLanguage(String scope) {
        return scope.contains("#nno") || scope.contains("#eng") || scope.contains("#sme")
            || scope.contains("639-eng") || scope.contains("639-nno")
            || scope.contains("639-sme") || scope.contains("639-smj") || scope.contains("639-sma");
    }

    private NodeList selectNodeList(Element element, String... elementNames)
            throws TransformerException, XPathExpressionException {
        NodeList elements = null;
        XPathFactory factory = XPathFactory.newInstance();
        for(String elementName: elementNames){
            if(elements == null || elements.getLength() == 0){
                elements = (NodeList)factory.newXPath().evaluate(elementName, element, XPathConstants.NODESET);

            }
        }
        return elements;
    }

    private String getString(Element element, String... xpaths){
        String value = null;
        for(String xpath: xpaths){
            if(value == null || value.isEmpty()){
                value = XPathHelper.getString(element, xpath);
            }
        }
        return value;
    }

    private List<TopicOccurence> getOccurencesForTopic(Element topicElement)
            throws TransformerException, XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList elmOccurrences = (NodeList)xpath.evaluate("occurrence", topicElement, XPathConstants.NODESET);
        List<TopicOccurence> occurences = new ArrayList<>(elmOccurrences.getLength());
        for (int i = 0; i < elmOccurrences.getLength(); i++) {
            Element elmOccurrence = (Element)elmOccurrences.item(i);
            TopicOccurence occurence = new TopicOccurence();

            String resourceData  = XPathHelper.getString(elmOccurrence, "resourceData");
            occurence.setResourceData(resourceData);

            String occurenceInstanceOf = getAttributeValue(elmOccurrence, ATTRIBUTE_HREF, "type/topicRef","instanceOf/subjectIndicatorRef");
            if (occurenceInstanceOf != null ) {
                occurenceInstanceOf = removeLeadingSquare(occurenceInstanceOf);
                occurence.setInstanceOf(new Topic(occurenceInstanceOf, topicMapId));
            }
            occurences.add(occurence);
        }
        return occurences;
    }

    public List<TopicAssociation> getTopicAssociationsFromDocument(Document document)
            throws TransformerException, XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList associations = (NodeList)xpath.evaluate("association", document.getDocumentElement(), XPathConstants.NODESET);
        List<TopicAssociation> topicAssociations = new ArrayList<>(associations.getLength());
        for (int i = 0; i < associations.getLength(); i++) {
            Element elmAssociation = (Element)associations.item(i);

            // Topics are bidirectional -> two associations
            TopicAssociation association1 = new TopicAssociation();
            TopicAssociation association2 = new TopicAssociation();

            String instanceOf = getAttributeValue(elmAssociation,ATTRIBUTE_HREF,"type/topicRef","instanceOf/subjectIndicatorRef");
            if (instanceOf != null) {
                instanceOf = removeIdPrefix(instanceOf);
                association1.setInstanceOf(new Topic(instanceOf, topicMapId));
                association2.setInstanceOf(new Topic(instanceOf, topicMapId));
            }

            NodeList elmMembers = selectNodeList(elmAssociation,"member", "role");
            if (elmMembers.getLength() == 2) {
                Element member1 = (Element)elmMembers.item(0);
                Element member2 = (Element)elmMembers.item(1);

                Topic topic1 = new Topic();
                topic1.setTopicMapId(topicMapId);

                Topic topic2 = new Topic();
                topic2.setTopicMapId(topicMapId);

                addIdToTopic(member1, topic1);
                addIdToTopic(member2, topic2);

                association1.setTopicRef(topic1);
                association1.setAssociatedTopicRef(topic2);

                association2.setTopicRef(topic2);
                association2.setAssociatedTopicRef(topic1);

                association1.setImported(true);
                association2.setImported(true);

                // Rolleforhold
                addRoleSpecToAssociation(member1, association1);
                addRoleSpecToAssociation(member2, association2);

                topicAssociations.add(association1);
                topicAssociations.add(association2);
            }
        }
        return topicAssociations;
    }

    private void addRoleSpecToAssociation(Element memberElement,TopicAssociation association)
            throws TransformerException, XPathExpressionException {
        String roleSpec = getAttributeValue(memberElement, ATTRIBUTE_HREF, "type/topicRef","roleSpec/subjectIndicatorRef");
        if (roleSpec != null ) {
            roleSpec = removeIdPrefix(roleSpec);
            association.setRolespec(new Topic(roleSpec, topicMapId));
        }
    }

    private void addIdToTopic(Element element, Topic topic) throws TransformerException, XPathExpressionException {
        String id = getAttributeValue(element,ATTRIBUTE_HREF, "topicRef", "subjectIndicatorRef");
        if (id != null ) {
            id = removeLeadingSquare(id);
            id = removeIdPrefix(id);
            topic.setId(id);
        }
    }

    private String removeIdPrefix(String id){
        String[] idPrefixArray;
        try {
            Configuration configuration = Aksess.getConfiguration();
            idPrefixArray = configuration.getString("topic.import.id.prefix").split(",");
            for(String prefix: idPrefixArray){
                if(id.indexOf(prefix) >= 0 && id.indexOf(prefix) < 1){
                    id = id.substring(id.indexOf(prefix)+ prefix.length(), id.length());
                }
            }
        } catch (Exception e) {
            //Do nothing, no prefixes removed
        }
        return id;
    }

}
