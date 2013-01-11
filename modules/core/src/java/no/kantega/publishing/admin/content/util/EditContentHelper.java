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

package no.kantega.publishing.admin.content.util;

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.AssociationHelper;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.MetadataTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ImageAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.factory.AttributeFactory;
import no.kantega.publishing.common.factory.ClassNameAttributeFactory;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.data.Topic;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import java.util.*;

public class EditContentHelper {
    private static final String SOURCE = "aksess.admin.EditContentHelper";

    /**
     * Create a new Content object
     * @param securitySession - SecuritySession
     * @param param - ContentCreateParameters
     * @return new Content object
     * @throws SystemException -
     * @throws NotAuthorizedException -
     * @throws InvalidFileException -
     * @throws InvalidTemplateException -
     */
    public static Content createContent(SecuritySession securitySession, ContentCreateParameters param) throws SystemException, NotAuthorizedException, InvalidFileException, InvalidTemplateException {
        if (securitySession == null || !securitySession.isLoggedIn()) {
            throw new NotAuthorizedException("Not logged in", SOURCE);
        }
        ContentManagementService aksessService = new ContentManagementService(securitySession);

        boolean inheritGroup = true;

        Content content = new Content();

        // Set status = checkedout
        content.setIsCheckedOut(true);

        if (param.getParentIds() == null) {
            // Parent ids not specified, create based on mainParent
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(param.getMainParentId());

            Content parent = aksessService.getContent(cid);
            List<Association> associations = parent.getAssociations();
            int allParents[] = new int[associations.size()];
            for (int i = 0; i < associations.size(); i++) {
                Association p = associations.get(i);
                allParents[i] = p.getId();
            }
            param.setParentIds(allParents);
        }

        // Set author
        content.setPublisher(securitySession.getUser().getName());

        if (param.getDisplayTemplateId() != -1) {
            // Set values from displaytemplate
            DisplayTemplate displayTemplate = aksessService.getDisplayTemplate(param.getDisplayTemplateId());

            content.setDisplayTemplateId(param.getDisplayTemplateId());
            content.setContentTemplateId(displayTemplate.getContentTemplate().getId());
            if (displayTemplate.getMetaDataTemplate() != null) {
                content.setMetaDataTemplateId(displayTemplate.getMetaDataTemplate().getId());
            }

            param.setContentTemplateId(displayTemplate.getContentTemplate().getId());

            if (displayTemplate.isNewGroup()) {
                inheritGroup = false;
            }

            if (displayTemplate.getDefaultForumId() != null) {
                content.setForumId(displayTemplate.getDefaultForumId());
            }
        } else {
            content.setContentTemplateId(param.getContentTemplateId());
        }

        // Create associations as specified
        boolean foundCurrentAssociation = false;

        List<Association> associations = AssociationHelper.createAssociationsFromParentIds(param.getParentIds());
        for (Association association : associations) {
            if (association.getParentAssociationId() == param.getMainParentId()) {
                foundCurrentAssociation = true;
                association.setCurrent(true);
            }
            association.setCategory(new AssociationCategory(param.getCategoryId()));

            content.addAssociation(association);
        }

        // Check if user has removed main parent from list of parents, in case just use first parent as main parent
        if (!foundCurrentAssociation) {
            Association association = associations.get(0);
            association.setCurrent(true);
            param.setMainParentId(association.getParentAssociationId());
        }

        // Set values from contenttemplate
        ContentTemplate contentTemplate = aksessService.getContentTemplate(param.getContentTemplateId());
        if (contentTemplate.getDocumentType() != null) {
            content.setDocumentTypeId(contentTemplate.getDocumentType().getId());
        }
        if (contentTemplate.getDocumentTypeForChildren() != null) {
            content.setDocumentTypeIdForChildren(contentTemplate.getDocumentTypeForChildren().getId());
        }
        if (contentTemplate.getDefaultPageUrlAlias() != null && contentTemplate.getDefaultPageUrlAlias().length() > 0) {
            content.setAlias(contentTemplate.getDefaultPageUrlAlias());
        }

        content.setSearchable(contentTemplate.isSearchable());


        content.setType(contentTemplate.getContentType());

        // Inherit owner, language etc from parent
        ContentIdentifier parentCid =  ContentIdentifier.fromAssociationId(param.getMainParentId());
        Content parent = aksessService.getContent(parentCid);
        content.setSecurityId(parent.getSecurityId());
        content.setOwner(parent.getOwner());
        content.setOwnerPerson(parent.getOwnerPerson());
        content.setLanguage(parent.getLanguage());

        // Set documenttype if documentype is not set for this object and defaultDocumentTypeIdForChildren is set for parent
        if (content.getDocumentTypeId() <= 0 && parent.getDocumentTypeIdForChildren() > 0) {
            content.setDocumentTypeId(parent.getDocumentTypeIdForChildren());
        }

        if (inheritGroup) {
            // Inherit properties from pages above.  Used to create things which should be special for a subtree, eg a menu
            content.setGroupId(parent.getGroupId());
        }

        // Set default expiredate if specified in contenttemplate
        if (contentTemplate.getExpireMonths() != null && contentTemplate.getExpireMonths() > 0) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MONTH, contentTemplate.getExpireMonths());
            content.setExpireDate(calendar.getTime());
        }
        if (contentTemplate.getExpireAction() != null) {
            content.setExpireAction(contentTemplate.getExpireAction());
        }

        // Inherit properties set to be inherited in contenttemplate
        setDefaultProperties(content);

        return content;
    }

    public static void addRepeaterRow(Content content, String rowPath, int attributeType) throws InvalidTemplateException {
        ContentTemplate template = null;

        if (attributeType == AttributeDataType.CONTENT_DATA) {
            template = ContentTemplateCache.getTemplateById(content.getContentTemplateId(), true);
        } else {
            if (content.getMetaDataTemplateId() != -1) {
                template = MetadataTemplateCache.getTemplateById(content.getMetaDataTemplateId(), true);
            } else {
                // Set to empty list if no template specified
                content.setAttributes(new ArrayList<Attribute>(), attributeType);
            }
        }

        if (template == null) {
            return;
        }

        Attribute attr = getAttributeByName(content.getAttributes(attributeType), rowPath);
        if (attr != null &&  attr instanceof RepeaterAttribute) {
            RepeaterAttribute repeaterAttribute = (RepeaterAttribute)attr;

            List<Attribute> newAttributes = new ArrayList<Attribute>();
            repeaterAttribute.addRow(newAttributes);

            List<Element> xmlElements = getXMLElementsForRepeater(rowPath, template);

            addAttributes(template, attributeType, new HashMap<String, String>(), repeaterAttribute, null, newAttributes, new ArrayList<Attribute>(), xmlElements);
        }
    }

    private static List<Element> getXMLElementsForRepeater(String rowPath, ContentTemplate template) {
        List<Element> xmlElements = new ArrayList<Element>();
        for (Element xmlElement : template.getAttributeElements()) {
            String name = xmlElement.getAttribute("name");
            if (name != null && name.equals(rowPath)) {
                NodeList nodes = xmlElement.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node n = nodes.item(i);
                    if (n instanceof Element) {
                        xmlElements.add((Element)n);
                    }
                }
            }
        }
        return xmlElements;
    }

    public static void updateAttributesFromTemplate(Content content) throws SystemException, InvalidFileException, InvalidTemplateException {
        content.setAttributesAreUpdatedFromTemplate(true);
        updateAttributesFromTemplate(content, null);
    }

    public static void updateAttributesFromTemplate(Content content, Map<String, String> defaultValues) throws SystemException, InvalidFileException, InvalidTemplateException {
        content.setAttributesAreUpdatedFromTemplate(true);
        updateAttributesFromTemplate(content, AttributeDataType.CONTENT_DATA, defaultValues);
        updateAttributesFromTemplate(content, AttributeDataType.META_DATA, defaultValues);
    }

    private static void updateAttributesFromTemplate(Content content, int attributeType, Map<String, String> defaultValues) throws SystemException, InvalidFileException, InvalidTemplateException {
        ContentTemplate template = null;

        if (defaultValues == null) {
            defaultValues = new HashMap<String, String>();
        }

        if (attributeType == AttributeDataType.CONTENT_DATA) {
            template = ContentTemplateCache.getTemplateById(content.getContentTemplateId(), true);
        } else {
            if (content.getMetaDataTemplateId() != -1) {
                template = MetadataTemplateCache.getTemplateById(content.getMetaDataTemplateId(), true);
            } else {
                // Set to empty list if no template specified
                content.setAttributes(new ArrayList<Attribute>(), attributeType);
            }
        }

        if (template == null) {
            return;
        }

        List<Attribute> newAttributes = new ArrayList<Attribute>();

        addAttributes(template, attributeType, defaultValues, null, null, newAttributes, content.getAttributes(attributeType), template.getAttributeElements());

        addDefaultFieldMapping(attributeType, template, template.getAttributeElements(), newAttributes);

        content.setAttributes(newAttributes, attributeType);
    }

    private static void addDefaultFieldMapping(int attributeType, ContentTemplate template, List<Element> attributes, List<Attribute> newAttributes) throws InvalidTemplateException {
        // Some attributes are mapped to specific properties in the Content object, search for these
        // These are always located at root level, never inside a repeater
        String titleField = null;
        String descField = null;
        String imageField = null;

        for (Element xmlAttribute : attributes) {
            String name = xmlAttribute.getAttribute("name");

            String field = xmlAttribute.getAttribute("mapto");
            if (field == null) {
                field = xmlAttribute.getAttribute("field");
            }

            if (field != null && field.length() > 0) {
                field = field.toLowerCase();
                if (field.contains(ContentProperty.TITLE)) {
                    titleField = name;
                } else if (field.contains(ContentProperty.DESCRIPTION)) {
                    descField = name;
                } else if (field.contains(ContentProperty.IMAGE)) {
                    imageField = name;
                }
            }

        }


        if (attributeType == AttributeDataType.CONTENT_DATA) {
            /*
            * If mapping of attributes to Content properties are not specified
            * map them as follows:
            *  - First image -> content.image
            *  - First text > 255 chars -> content.description
            *  - First text < 255 chars -> content.title
            */

            if ((titleField == null) || (descField == null) || (imageField == null)) {
                for (Attribute attr : newAttributes) {
                    if (attr instanceof ImageAttribute && imageField == null) {
                        attr.setField(ContentProperty.IMAGE);
                        imageField = attr.getName();
                    }
                    if (attr instanceof TextAttribute && titleField == null) {
                        int maxlength = attr.getMaxLength();
                        if (maxlength < 255) {
                            attr.setField(ContentProperty.TITLE);
                            titleField = attr.getName();
                        }
                    }
                    if (attr instanceof TextAttribute && descField == null && (!attr.getName().equalsIgnoreCase(titleField))) {
                        int maxlength = attr.getMaxLength();
                        if (maxlength >= 255) {
                            attr.setField(ContentProperty.DESCRIPTION);
                            descField = attr.getName();
                        }
                    }
                }
            }

            if (titleField == null) {
                throw new InvalidTemplateException("The template includes no attributes for the page title.  Add mapto=title on one attribute:" + template.getName(), SOURCE, null);
            }
        }
    }

    /**
     * Create attributes recursively
     * @param attributeType - type of attributes to create, content or metadata
     * @param defaultValues - used to initialize attributes with default values
     * @param newParentAttribute - parent of attributes
     * @param oldParentAttribute - parent of oldattributes
     * @param newAttributes - list with new attributes
     * @param oldAttributes - list with old attributes
     * @param xmlAttributes - XML element with definition
     * @throws SystemException -
     * @throws InvalidTemplateException -
     */
    private static void addAttributes(ContentTemplate template, int attributeType, Map<String, String> defaultValues, @Nullable RepeaterAttribute newParentAttribute, @Nullable RepeaterAttribute oldParentAttribute, List<Attribute> newAttributes, List<Attribute> oldAttributes, List<Element> xmlAttributes) throws SystemException, InvalidTemplateException {
        for (Element xmlAttribute : xmlAttributes) {

            String name = xmlAttribute.getAttribute("name");
            String type;
            if (xmlAttribute.getTagName().equalsIgnoreCase("repeater")) {
                type = "repeater";
            } else {
                type = xmlAttribute.getAttribute("type");
            }

            AttributeFactory attributeFactory = new ClassNameAttributeFactory();

            Attribute attribute = null;
            try {
                attribute = attributeFactory.newAttribute(type);
            } catch (ClassNotFoundException e) {
                throw new InvalidTemplateException("Feil i skjemadefinisjon, ukjent attributt " + type + ", fil:" + template.getName(), SOURCE, null);
            } catch (Exception e) {
                throw new SystemException("Feil ved oppretting av klasse for attributt" + type, SOURCE, e);
            }

            attribute.setName(name);
            attribute.setType(attributeType);

            attribute.setConfig(xmlAttribute, defaultValues);

            if (newParentAttribute != null) {
                attribute.setParent(newParentAttribute);
            }

            if (attribute instanceof RepeaterAttribute) {
                /*
                   RepeaterAttribute is a rowset of repeatable attributes
                */
                RepeaterAttribute repeater = (RepeaterAttribute)attribute;

                RepeaterAttribute oldRepeater = null;
                if (oldAttributes != null) {
                    Attribute tmpAttr = getAttributeByName(oldAttributes, repeater.getName());
                    if (tmpAttr != null && tmpAttr instanceof RepeaterAttribute) {
                        oldRepeater = (RepeaterAttribute)tmpAttr;
                    }
                }

                int maxRows = repeater.getMinOccurs();
                if (oldRepeater != null) {
                    maxRows = getMaxNumberOfRows(oldRepeater, repeater);
                }

                for (int rowNumber = 0; rowNumber < maxRows; rowNumber++) {
                    List<Attribute> newRowAttributes = new ArrayList<Attribute>();
                    List<Attribute> oldRowAttributes = null;

                    if (oldAttributes != null && oldRepeater != null) {
                        if (rowNumber < oldRepeater.getNumberOfRows()) {
                            oldRowAttributes = oldRepeater.getRow(rowNumber);
                        }
                    }

                    addAttributes(template, attributeType, defaultValues, repeater, oldRepeater, newRowAttributes, oldRowAttributes, getChildrenAsList(xmlAttribute));
                    repeater.addRow(newRowAttributes);
                }
            }

            newAttributes.add(attribute);

            // Copy value from old attribute
            if (oldAttributes != null) {
                Attribute oldAttribute = getAttributeByName(oldAttributes, attribute.getName());
                if (oldAttribute != null) {
                    attribute.cloneValue(oldAttribute);
                }
            }
        }
    }

    private static List<Element> getChildrenAsList(Element xmlAttribute) {
        List<Element> xmlChildren = new ArrayList<Element>();
        NodeList tmpChildren = xmlAttribute.getChildNodes();
        for (int childNo = 0; childNo < tmpChildren.getLength(); childNo++) {
            Node node = tmpChildren.item(childNo);
            if (node instanceof Element) {
                xmlChildren.add((Element)node);
            }
        }
        return xmlChildren;
    }

    private static Attribute getAttributeByName(List<Attribute> attributes, String name) {
        for (Attribute a : attributes) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    private static int getMaxNumberOfRows(RepeaterAttribute oldParentAttribute, RepeaterAttribute repeater) {
        return Math.min(oldParentAttribute.getNumberOfRows(), repeater.getMaxOccurs());
    }


    private static void copyProperty(Content from, Content dest, String property) throws SystemException {
        if (property.equalsIgnoreCase(ContentProperty.PUBLISH_DATE)) {
            dest.setPublishDate(from.getPublishDate());
        } else if (property.equalsIgnoreCase(ContentProperty.EXPIRE_DATE)) {
            dest.setExpireDate(from.getExpireDate());
        } else if (property.equalsIgnoreCase(ContentProperty.TOPICS)) {
            List<Topic> topics1 = TopicAO.getTopicsByContentId(from.getId());
            if (topics1 != null) {
                // Copy only topics which dont exists from before
                for (Topic topic : topics1) {
                    dest.addTopic(topic);
                }
            }
        }
    }


    private static void inheritPropertiesByTemplate(Content content, ContentTemplate template) throws SystemException, InvalidFileException, InvalidTemplateException {
        List<Element> properties = template.getPropertyElements();

        for (Element property  : properties) {
            String name = property.getAttribute("name");
            String from = property.getAttribute("from");

            if (name != null && from != null && from.length() > 0) {
                try {
                    ContentIdentifier parentCid = ContentIdHelper.findRelativeContentIdentifier(content, from);
                    Content parent = ContentAO.getContent(parentCid, true);
                    if (parent != null) {
                        copyProperty(parent, content, name);
                    }
                } catch (ContentNotFoundException e) {
                    Log.info(SOURCE, "Template:" + template.getName() + " has reference to none existing content", null, null);
                }
            }
        }
    }

    private static void setDefaultProperties(Content content) throws SystemException, InvalidFileException, InvalidTemplateException {
        if (content.getMetaDataTemplateId() > 0) {
            ContentTemplate template = MetadataTemplateCache.getTemplateById(content.getMetaDataTemplateId(), true);
            if (template != null) {
                inheritPropertiesByTemplate(content, template);
            }
        }
        if (content.getContentTemplateId() > 0) {
            ContentTemplate template = ContentTemplateCache.getTemplateById(content.getContentTemplateId(), true);
            if (template != null) {
                inheritPropertiesByTemplate(content, template);
            }
        }
    }

    public static void deleteRepeaterRow(Content content, String rowPath, int attributeType) {
        List<Attribute> attributes = content.getAttributes(attributeType);

        if (rowPath.contains("[")) {
            String repeaterName = rowPath.substring(0, rowPath.indexOf("["));
            int rowNo = Integer.parseInt(rowPath.substring(rowPath.indexOf("[") + 1, rowPath.indexOf("]")));
            Attribute attribute = getAttributeByName(attributes, repeaterName);
            if (attribute instanceof RepeaterAttribute) {
                RepeaterAttribute repeaterAttribute = (RepeaterAttribute)attribute;
                if (repeaterAttribute.getNumberOfRows() > repeaterAttribute.getMinOccurs()) {
                    repeaterAttribute.removeRow(rowNo);
                }
            }
        }
    }
}
