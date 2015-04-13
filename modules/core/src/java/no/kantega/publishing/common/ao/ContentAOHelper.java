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

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.UnPersistAttributeBehaviour;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ListAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.factory.ClassNameAttributeFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public class ContentAOHelper {
    private static final Logger log = LoggerFactory.getLogger(ContentAOHelper.class);
    private static final ClassNameAttributeFactory attributeFactory = new ClassNameAttributeFactory();
    private static final Pattern REPEATER_PATTERN = Pattern.compile("\\].");


    public static Content getContentFromRS(ResultSet rs, boolean getAssociationInfo) throws SQLException {
        Content content = new Content();

        // Felter fra Content
        content.setId(rs.getInt("ContentId"));

        content.setType(ContentType.getContentTypeAsEnum(rs.getInt("ContentType")));
        content.setContentTemplateId(rs.getInt("ContentTemplateId"));
        content.setMetaDataTemplateId(rs.getInt("MetaDataTemplateId"));
        content.setDisplayTemplateId(rs.getInt("DisplayTemplateId"));
        content.setDocumentTypeId(rs.getInt("DocumentTypeId"));
        content.setGroupId(rs.getInt("GroupId"));
        content.setOwner(StringUtils.defaultIfEmpty(rs.getString("Owner"), ""));

        if (content.getType() != ContentType.PAGE) {
            content.setLocation(rs.getString("Location"));
        }
        content.setAlias(rs.getString("Alias"));
        content.setPublishDate(rs.getTimestamp("PublishDate"));
        content.setExpireDate(rs.getTimestamp("ExpireDate"));
        content.setExpireAction(ExpireAction.valueOf(rs.getString("ExpireAction")));
        content.setVisibilityStatus(ContentVisibilityStatus.fromId(rs.getInt("VisibilityStatus")));

        content.setNumberOfNotes(rs.getInt("NumberOfNotes"));
        content.setOwnerPerson(StringUtils.defaultIfEmpty(rs.getString("OwnerPerson"), ""));

        content.setRevisionDate(rs.getTimestamp("RevisionDate"));
        content.setForumId(rs.getLong("ForumId"));
        content.setDoOpenInNewWindow(rs.getBoolean("OpenInNewWindow"));
        content.setDocumentTypeIdForChildren(rs.getInt("DocumentTypeIdForChildren"));

        // Felter fra ContentVersion
        content.setVersionId(rs.getInt("ContentVersionId"));
        content.setVersion(rs.getInt("Version"));
        content.setStatus(ContentStatus.getContentStatusAsEnum(rs.getInt("Status")));
        content.setLanguage(rs.getInt("Language"));
        content.setTitle(rs.getString("Title"));
        content.setAltTitle(rs.getString("AltTitle"));
        content.setDescription(rs.getString("Description"));
        content.setImage(rs.getString("Image"));
        content.setKeywords(StringUtils.defaultIfEmpty(rs.getString("Keywords"), ""));

        content.setPublisher(rs.getString("Publisher"));
        content.setLastModified(rs.getTimestamp("LastModified"));
        content.setModifiedBy(rs.getString("LastModifiedBy"));
        content.setChangeDescription(StringUtils.defaultIfEmpty(rs.getString("ChangeDescription"), ""));

        content.setApprovedBy(StringUtils.defaultIfEmpty(rs.getString("ApprovedBy"), ""));

        content.setLocked(rs.getBoolean("IsLocked"));
        content.setRatingScore(rs.getFloat("RatingScore"));
        content.setNumberOfRatings(rs.getInt("NumberOfRatings"));
        content.setSearchable(rs.getBoolean("IsSearchable"));
        content.setNumberOfComments(rs.getInt("NumberOfComments"));
        content.setChangeFromDate(rs.getTimestamp("ChangeFrom"));

        content.setMinorChange(rs.getBoolean("IsMinorChange"));
        content.setLastMajorChange(rs.getTimestamp("LastMajorChange"));
        content.setLastMajorChangeBy(rs.getString("LastMajorChangeBy"));

        // Info som avhenger av i hvilken kontekst dette er publisert
        if (getAssociationInfo) {
            content.addAssociation(AssociationAO.getAssociationFromRS(rs));
        }
        return content;
    }


    public static void addAttributeFromRS(Content content, ResultSet rs) throws SQLException, SystemException {

        String attributeType = StringUtils.defaultIfEmpty(rs.getString("AttributeType"), "Text");

        AttributeDataType attributeDataType = AttributeDataType.getDataTypeAsEnum(rs.getInt("DataType"));

        String attributeNameIncludingPath = rs.getString("Name");
        String value = rs.getString("Value");

        List<Attribute> attributes = content.getAttributes(attributeDataType);
        Attribute parentAttribute = null;

        String path[] = REPEATER_PATTERN.split(attributeNameIncludingPath);
        for (String pathElement : path) {
            if (pathElement.contains("[")) {
                // Repeater attribute
                String rowStr = pathElement.substring(pathElement.indexOf("[") + 1, pathElement.length());
                int row = Integer.parseInt(rowStr, 10);
                pathElement = pathElement.substring(0, pathElement.indexOf("["));

                // Check if repeater has been created
                RepeaterAttribute repeater = createOrGetExistingRepeaterAttributeByName(attributes, pathElement, attributeDataType);
                repeater.setParent(parentAttribute);

                parentAttribute = repeater;

                addCorrectNumberOfRows(row, repeater);

                // Get correct row
                attributes = repeater.getRow(row);
            } else {
                // Normal attribute
                Attribute attribute = null;
                try {
                    attribute = attributeFactory.newAttribute(attributeType);
                } catch (Exception e) {
                    log.error("Error instantiating attribute " + attributeType, e);
                    throw new SystemException("Feil ved oppretting av klasse for attributt " + attributeType, e);
                }
                attribute.setParent(parentAttribute);

                attributes.add(attribute);

                value = removeExtraCharsFromListAttribute(value, attribute);
                attribute.setValue(value);
                attribute.setName(pathElement);

                UnPersistAttributeBehaviour behaviour = attribute.getFetchBehaviour();
                behaviour.unpersistAttribute(rs, attribute);

            }
        }

    }

    private static void addCorrectNumberOfRows(int row, RepeaterAttribute repeater) {
        List<Attribute> attributes;// Add number of necessary rows
        while (repeater.getNumberOfRows() < row + 1) {
            attributes = new ArrayList<>();
            repeater.addRow(attributes);
        }
    }

    private static String removeExtraCharsFromListAttribute(String value, Attribute attribute) {
        if (attribute instanceof ListAttribute && value != null && value.length() > 0) {
            // Lists are stored with , in front and end to make them searchable via SQL
            if (value.charAt(0) == ',') {
                value = value.substring(1, value.length());
            }
            if (value.charAt(value.length() - 1) == ',') {
                value = value.substring(0, value.length() - 1);
            }
        }
        return value;
    }

    private static RepeaterAttribute createOrGetExistingRepeaterAttributeByName(List<Attribute> attributes, String pathElement, AttributeDataType attributeDataType) {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(pathElement) && attribute instanceof RepeaterAttribute) {
                return (RepeaterAttribute)attribute;
            }
        }

        RepeaterAttribute repeater = new RepeaterAttribute();
        repeater.setName(pathElement);
        repeater.setType(attributeDataType);
        attributes.add(repeater);

        return repeater;
    }

}

