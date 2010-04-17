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

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.admin.content.behaviours.attributes.UnPersistAttributeBehaviour;
import no.kantega.commons.exception.SystemException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class ContentAOHelper {
    private static final String SOURCE = "aksess.ContentAOHelper";

    public static Content getContentFromRS(ResultSet rs, boolean getAssociationInfo) throws SQLException {
        Content content = new Content();

        // Felter fra Content
        content.setId(rs.getInt("ContentId"));

        content.setType(ContentType.getContentTypeAsEnum(rs.getInt("Type")));
        content.setContentTemplateId(rs.getInt("ContentTemplateId"));
        content.setMetaDataTemplateId(rs.getInt("MetaDataTemplateId"));
        content.setDisplayTemplateId(rs.getInt("DisplayTemplateId"));
        content.setDocumentTypeId(rs.getInt("DocumentTypeId"));
        content.setGroupId(rs.getInt("GroupId"));
        content.setOwner(rs.getString("Owner"));
        if (content.getOwner() == null) {
            content.setOwner("");
        }
        if (content.getType() != ContentType.PAGE) {
            content.setLocation(rs.getString("Location"));
        }
        content.setAlias(rs.getString("Alias"));
        content.setPublishDate(rs.getTimestamp("PublishDate"));
        content.setExpireDate(rs.getTimestamp("ExpireDate"));
        content.setExpireAction(rs.getInt("ExpireAction"));
        content.setVisibilityStatus(rs.getInt("VisibilityStatus"));

        content.setNumberOfNotes(rs.getInt("NumberOfNotes"));
        content.setOwnerPerson(rs.getString("OwnerPerson"));
        if (content.getOwnerPerson() == null) {
            content.setOwnerPerson("");
        }
        content.setRevisionDate(rs.getTimestamp("RevisionDate"));
        content.setForumId(rs.getLong("ForumId"));
        content.setDoOpenInNewWindow(rs.getInt("OpenInNewWindow") == 1);
        content.setDocumentTypeIdForChildren(rs.getInt("DocumentTypeIdForChildren"));

        // Felter fra ContentVersion
        content.setVersionId(rs.getInt("ContentVersionId"));
        content.setVersion(rs.getInt("Version"));
        content.setStatus(rs.getInt("Status"));
        content.setLanguage(rs.getInt("Language"));
        content.setTitle(rs.getString("Title"));
        content.setAltTitle(rs.getString("AltTitle"));
        content.setDescription(rs.getString("Description"));
        content.setImage(rs.getString("Image"));
        content.setKeywords(rs.getString("Keywords"));
        if (content.getKeywords() == null) {
            content.setKeywords("");
        }
        content.setPublisher(rs.getString("Publisher"));
        content.setLastModified(rs.getTimestamp("LastModified"));
        content.setModifiedBy(rs.getString("LastModifiedBy"));
        content.setChangeDescription(rs.getString("ChangeDescription"));
        if (content.getChangeDescription() == null) {
            content.setChangeDescription("");
        }
        content.setApprovedBy(rs.getString("ApprovedBy"));
        if (content.getApprovedBy() == null) {
            content.setApprovedBy("");
        }

        content.setLocked(rs.getInt("IsLocked") == 1);
        content.setRatingScore(rs.getFloat("RatingScore"));
        content.setNumberOfRatings(rs.getInt("NumberOfRatings"));
        content.setSearchable(rs.getInt("IsSearchable") == 1);
        content.setChangeFromDate(rs.getTimestamp("ChangeFrom"));        

        // Info som avhenger av i hvilken kontekst dette er publisert
        if (getAssociationInfo) {
            content.addAssociation(AssociationAO.getAssociationFromRS(rs));
        }
        return content;
    }

    public static void addAttributeFromRS(Content content, ResultSet rs) throws SQLException, SystemException {

        String attrType = rs.getString("AttributeType");
        if (attrType == null) {
            attrType = "Text";
        }
        attrType = attrType.substring(0, 1).toUpperCase() + attrType.substring(1, attrType.length()).toLowerCase();

        Attribute attribute = null;
        try {
            attribute = (Attribute)Class.forName(Aksess.ATTRIBUTE_CLASS_PATH + attrType + "Attribute").newInstance();
        } catch (Exception e) {
            throw new SystemException("Feil ved oppretting av klasse for attributt" +  attrType, SOURCE, e);
        }

        int attrDataType = rs.getInt("DataType");

        attribute.setName(rs.getString("Name"));

        UnPersistAttributeBehaviour behaviour = attribute.getFetchBehaviour();
        behaviour.unpersistAttribute(rs, attribute);       

        content.addAttribute(attribute, attrDataType);
    }
}

