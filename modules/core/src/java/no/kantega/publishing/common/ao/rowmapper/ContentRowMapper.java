package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentRowMapper implements RowMapper<Content> {
    private static AssociationRowMapper associationRowMapper = new AssociationRowMapper();
    private boolean getAssociationInfo = false;

    public ContentRowMapper(boolean getAssociationInfo) {
        this.getAssociationInfo = getAssociationInfo;
    }

    public Content mapRow(ResultSet rs, int i) throws SQLException {
        Content content = new Content();

        // Content table
        content.setId(rs.getInt("ContentId"));

        content.setType(ContentType.getContentTypeAsEnum(rs.getInt("ContentType")));
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

        // ContentVersion table
        content.setVersionId(rs.getInt("ContentVersionId"));
        content.setVersion(rs.getInt("Version"));
        content.setStatus(ContentStatus.getContentStatusAsEnum(rs.getInt("Status")));
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
        content.setNumberOfComments(rs.getInt("NumberOfComments"));
        content.setChangeFromDate(rs.getTimestamp("ChangeFrom"));

        content.setMinorChange(rs.getInt("IsMinorChange") == 1);
        content.setLastMajorChange(rs.getTimestamp("LastMajorChange"));
        content.setLastMajorChangeBy(rs.getString("LastMajorChangeBy"));

        // Info som avhenger av i hvilken kontekst dette er publisert
        if (getAssociationInfo) {
            content.addAssociation(associationRowMapper.mapRow(rs, 0));
        }
        return content;
    }
}
