package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.ao.rowmapper.AssociationRowMapper;
import no.kantega.publishing.common.ao.rowmapper.AttributeRowMapper;
import no.kantega.publishing.common.ao.rowmapper.ContentRowMapper;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.factory.AttributeFactory;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

public class JdbcContentDao extends JdbcDaoSupport implements ContentDao {
    private AttributeFactory attributeFactory;

    public Content getContent(ContentIdentifier cid, boolean isAdminMode) {
        ContentIdHelper.assureContentIdAndAssociationIdSet(cid);
        int requestedVersion = cid.getVersion();
        int contentVersionId = -1;

        int contentId = cid.getContentId();
        if (isAdminMode) {
            if (requestedVersion == -1) {
                // When in administration mode users should see last version
                contentVersionId = getJdbcTemplate().queryForInt("select ContentVersionId from contentversion where ContentId = ? order by ContentVersionId desc", contentId);
                if (contentVersionId == -1) {
                    return null;
                }
            } else {
                contentVersionId = getJdbcTemplate().queryForInt("select ContentVersionId from contentversion where ContentId = ? and Version = ? order by ContentVersionId desc", contentId, requestedVersion);
                if (contentVersionId == -1) {
                    return null;
                }
            }
        } else if(cid.getStatus() == ContentStatus.HEARING) {
            // Find version for hearing, if no hearing is found, active version is returned
            int activeversion = getJdbcTemplate().queryForInt("select ContentVersionId from contentversion where ContentId = ? and contentversion.IsActive = 1 order by ContentVersionId desc", contentId);
            contentVersionId = getJdbcTemplate().queryForInt("select ContentVersionId from contentversion where ContentId = ? AND Status = ? AND ContentVersionId > ? order by ContentVersionId desc", contentId, ContentStatus.HEARING.getTypeAsInt(), activeversion);
        } else {
            // Others should see active version
            contentVersionId = -1;
        }


        StringBuilder query = new StringBuilder();
        query.append("select * from content, contentversion where content.ContentId = contentversion.ContentId");
        if (contentVersionId != -1) {
            // Get specified version
            query.append(" and contentversion.ContentVersionId = ").append(contentVersionId);
        } else {
            // Get whatever version is active
            query.append(" and contentversion.IsActive = 1");
        }
        query.append(" and content.ContentId = ").append(contentId).append(" order by ContentVersionId");


        // Get data from content and contentversion tables
        ContentRowMapper contentRowMapper = new ContentRowMapper(false);

        List<Content> contents = getJdbcTemplate().query(query.toString(), contentRowMapper);
        if (contents.size() == 0) {
            return null;
        }

        Content content = contents.get(0);

        // Get associations
        AssociationRowMapper associationRowMapper = new AssociationRowMapper();
        List<Association> associations = getJdbcTemplate().query("SELECT * FROM associations WHERE ContentId = ? AND (IsDeleted IS NULL OR IsDeleted = 0)", associationRowMapper, content.getId());
        if (associations.size() == 0) {
            // All associations to page are deleted, dont return page
            return null;
        }
        content.setAssociations(associations);
        updateCurrentAssociation(cid, content);

        // Get attributes
        AttributeRowMapper attributeRowMapper = new AttributeRowMapper(content, attributeFactory);
        getJdbcTemplate().query("select * from contentattributes where ContentVersionId = ?", attributeRowMapper, content.getVersionId());


        List<Topic> topics = TopicAO.getTopicsByContentId(contentId);
        content.setTopics(topics);

        return content;
    }

    private void updateCurrentAssociation(ContentIdentifier cid, Content content) {
        List<Association> associations = content.getAssociations();

        // Get associations for this page
        for (Association association : associations) {
            // Dersom knytningsid ikke er angitt bruker vi default for angitt site
            int associationId = cid.getAssociationId();
            if ((associationId == association.getId())
                    || (associationId == -1
                    && association.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE
                    && association.getSiteId() == cid.getSiteId())) {
                association.setCurrent(true);
                return;
            }
        }

        // Association id neither site id specified, use first association with DEFAULT_POSTING_FOR_SITE as current
        for (Association association : associations) {
            if (association.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                association.setCurrent(true);
                return;
            }
        }

        // Use whatever association
        associations.get(0).setCurrent(true);
    }

    public void setAttributeFactory(AttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory;
    }
}
