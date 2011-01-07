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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ImageAttribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.AssociationHelper;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.MetadataTemplateCache;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.security.SecuritySession;

import java.util.*;

import org.w3c.dom.Element;

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
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(param.getMainParentId());

            Content parent = aksessService.getContent(cid);
            List associations = parent.getAssociations();
            int allParents[] = new int[associations.size()];
            for (int i = 0; i < associations.size(); i++) {
                Association p = (Association)associations.get(i);
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

        List associations = AssociationHelper.createAssociationsFromParentIds(param.getParentIds());
        for (int i = 0; i < associations.size(); i++) {
            Association association = (Association)associations.get(i);
            if (association.getParentAssociationId() == param.getMainParentId()) {
                foundCurrentAssociation = true;
                association.setCurrent(true);
            }
            association.setCategory(new AssociationCategory(param.getCategoryId()));

            content.addAssociation(association);
        }

        // Check if user has removed main parent from list of parents, in case just use first parent as main parent
        if (!foundCurrentAssociation) {
            Association association = (Association)associations.get(0);
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
        content.setType(contentTemplate.getContentType());

        // Inherit owner, language etc from parent
        ContentIdentifier parentCid = new ContentIdentifier();
        parentCid.setAssociationId(param.getMainParentId());
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


    public static void updateAttributesFromTemplate(Content content) throws SystemException, InvalidFileException, InvalidTemplateException {
        updateAttributesFromTemplate(content, null);
    }

    public static void updateAttributesFromTemplate(Content content, Map<String, String> defaultValues) throws SystemException, InvalidFileException, InvalidTemplateException {
        updateAttributesFromTemplate(content, AttributeDataType.CONTENT_DATA, defaultValues);
        updateAttributesFromTemplate(content, AttributeDataType.META_DATA, defaultValues);
    }

    private static void updateAttributesFromTemplate(Content content, int attributeType, Map<String, String> defaultValues) throws SystemException, InvalidFileException, InvalidTemplateException {
        ContentTemplate template = null;

        if (defaultValues == null) {
            defaultValues = new HashMap<String, String>();
        }

        int templateId = -1;
        if (attributeType == AttributeDataType.CONTENT_DATA) {
            template = ContentTemplateCache.getTemplateById(content.getContentTemplateId(), true);
        } else {
            if (content.getMetaDataTemplateId() != -1) {
                template = MetadataTemplateCache.getTemplateById(content.getMetaDataTemplateId(), true);
            } else {
                // Set to empty list if no template specified
                content.setAttributes(new ArrayList(), attributeType);
            }
        }

        if (template == null) {
            return;
        }

        // Some attributes are mapped to specific properties in the Content object, search for these
        String titleField = null;
        String descField = null;
        String imageField = null;

        List<Element> attributes = template.getAttributeElements();
        List newAttributes = new ArrayList();
        for (Element attr : attributes) {
            String name = attr.getAttribute("name");
            String type = attr.getAttribute("type");
            if (type == null || type.length() == 0) {
                type = "text";
            }
            type = type.substring(0, 1).toUpperCase() + type.substring(1, type.length()).toLowerCase();

            Attribute attribute = null;
            try {
                attribute = (Attribute) Class.forName(Aksess.ATTRIBUTE_CLASS_PATH + type + "Attribute").newInstance();
            } catch (ClassNotFoundException e) {
                throw new InvalidTemplateException("Feil i skjemadefinisjon, ukjent attributt " + type + ", fil:" + template.getName(), SOURCE, null);
            } catch (Exception e) {
                throw new SystemException("Feil ved oppretting av klasse for attributt" + type, SOURCE, e);
            }

            attribute.setType(attributeType);


            attribute.setConfig(attr, defaultValues);

            String field = attribute.getField();
            if (field != null && field.length() > 0) {
                field = field.toLowerCase();
                if (field.indexOf(ContentProperty.TITLE) != -1) {
                    titleField = attribute.getName();
                } else if (field.indexOf(ContentProperty.DESCRIPTION) != -1) {
                    descField = attribute.getName();
                } else if (field.indexOf(ContentProperty.IMAGE) != -1) {
                    imageField = attribute.getName();
                }
            }

            // Save old values
            Attribute oldAttribute = content.getAttribute(name, attributeType);
            if (oldAttribute != null) {
                attribute.cloneValue(oldAttribute);
            }

            newAttributes.add(attribute);
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
                for (int i = 0; i < newAttributes.size(); i++) {
                    Attribute attr = (Attribute)newAttributes.get(i);
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
            if (attributeType == AttributeDataType.CONTENT_DATA) {
                if (titleField == null) {
                    throw new InvalidTemplateException("The template includes no attributes for the page title.  Add mapto=title on one attribute:" + template.getName(), SOURCE, null);
                }
            }
        }

        content.setAttributes(newAttributes, attributeType);
    }


    private static void copyProperty(Content from, Content dest, String property) throws SystemException {
        if (property.equalsIgnoreCase(ContentProperty.PUBLISH_DATE)) {
            dest.setPublishDate(from.getPublishDate());
        } else if (property.equalsIgnoreCase(ContentProperty.EXPIRE_DATE)) {
            dest.setExpireDate(from.getExpireDate());
        } else if (property.equalsIgnoreCase(ContentProperty.TOPICS)) {
            List topics1 = TopicAO.getTopicsByContentId(from.getId());

            List topics2 = dest.getTopics();
            if (topics2 == null) {
                dest.setTopics(topics2);
            } else {
                // Copy only topics which dont exists from before
                for (int i = 0; i < topics1.size(); i++) {
                    dest.addTopic((Topic)topics1.get(i));
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
}
