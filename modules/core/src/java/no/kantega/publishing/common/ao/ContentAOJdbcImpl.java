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
import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.AssociationIdListComparator;
import no.kantega.publishing.common.ContentComparator;
import no.kantega.publishing.common.ao.rowmapper.AssociationRowMapper;
import no.kantega.publishing.common.ao.rowmapper.ContentAttributeRowMapper;
import no.kantega.publishing.common.ao.rowmapper.ContentRowMapper;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.AttributeHandler;
import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.TransactionLockException;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.publishing.topicmaps.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.join;

/**
 *
 */
public class ContentAOJdbcImpl extends NamedParameterJdbcDaoSupport implements ContentAO {
    private static final Logger log = LoggerFactory.getLogger(ContentAOJdbcImpl.class);

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Autowired
    private TopicDao topicDao;

    @Override
    public ContentIdentifier deleteContent(ContentIdentifier cid) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        ContentIdentifier parent = getParent(cid);

        int id = cid.getContentId();
        // Slett tilgangsrettigheter
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        jdbcTemplate.update("delete from objectpermissions where ObjectSecurityId in (select AssociationId from associations where ContentId = ?) and ObjectType = ?",
                id, ObjectType.ASSOCIATION);
        // Slett knytninger dette elementet har til andre element og andre elements knytning til dette
        jdbcTemplate.update("delete from associations where ContentId = ?", id);

        // Slett innholdsattributter
        String deleteAttributesSql = "delete from contentattributes where ContentVersionId in (select ContentVersionId from contentversion where ContentId = ?)";
        if(dbConnectionFactory.isMySQL()) {
            deleteAttributesSql = "delete contentattributes from contentattributes,contentversion where contentattributes.contentversionid=contentversion.contentversionid and contentversion.contentid=?";
        }
        jdbcTemplate.update(deleteAttributesSql, id);

        try {
            // Slett høring
            jdbcTemplate.update("delete from hearing where ContentVersionId in (select ContentVersionId from contentversion where ContentId = ?)", id);
            jdbcTemplate.update("delete from hearinginvitee where HearingId not in (select HearingId from hearing)");
            jdbcTemplate.update("delete from hearingcomment where HearingId not in (select HearingId from hearing)");
        } catch (DataAccessException e1) {
            // Kunden bruker ikke høring, har ikke tabeller for høring
        }

        // Slett vedlegg
        jdbcTemplate.update("delete from attachments where ContentId = ?", id);
        jdbcTemplate.update("delete from contentversion where ContentId = ?", id);
        jdbcTemplate.update("delete from content where ContentId = ?", id);

        return parent;
    }

    @Override
    public void forAllContentObjects(final ContentHandler contentHandler, ContentHandlerStopper stopper) {
        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("SELECT ContentId FROM content");

            ResultSet resultSet = p.executeQuery();

            while(resultSet.next() && !stopper.isStopRequested()) {
                ContentIdentifier contentIdentifier =  ContentIdentifier.fromContentId(resultSet.getInt("ContentId"));

                try {
                    Content content = getContent(contentIdentifier, false);
                    if (content != null) {
                        contentHandler.handleContent(content);
                    }
                } catch (Exception ex) {
                    log.error("Error getting content " + contentIdentifier, ex);
                }
            }

        } catch (SystemException | SQLException e) {
            log.error("Error iterating over all content", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteContentVersion(ContentIdentifier cid, boolean deleteActiveVersion) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        int id = cid.getContentId();
        int version = cid.getVersion();
        int language = cid.getLanguage();

        try {
            Map<String, Object> values = getJdbcTemplate().queryForMap("select ContentVersionId, IsActive from contentversion where ContentId = ? and Version = ? and Language = ?", id, version, language);
            int contentVersionId = ((Number) values.get("ContentVersionId")).intValue();
            int isActive = ((Number) values.get("IsActive")).intValue();

            if (!deleteActiveVersion && isActive == 1) {
                return;
            }

            getJdbcTemplate().update("delete from contentattributes where ContentVersionId = ?", contentVersionId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Could not find contentversion with contentid {} version {} and language {}", id, version, language);
        }
    }

    @Override
    public List<Content> getAllContentVersions(ContentIdentifier cid) {
        return getJdbcTemplate().query("select * from content, contentversion where content.ContentId = contentversion.ContentId and contentversion.Language = ? and content.ContentId = ? order by contentversion.Version desc", new ContentRowMapper(false), cid.getLanguage(), cid.getContentId());
    }


    @Override
    public Content checkOutContent(ContentIdentifier cid) {
        Content content = getContent(cid, true);
        content.setIsCheckedOut(true);

        return content;
    }


    @Override
    public Content getContent(ContentIdentifier cid, boolean isAdminMode) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        int requestedVersion = cid.getVersion();
        int contentVersionId = -1;

        int contentId = cid.getContentId();
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (isAdminMode) {
            if (requestedVersion == -1) {
                // When in administration mode users should see last version
                List<Integer> contentVersionIds = jdbcTemplate.queryForList("select ContentVersionId from contentversion where ContentId = ? order by ContentVersionId desc", Integer.class, contentId);
                if (contentVersionIds.isEmpty()) {
                    return null;
                }else {
                    contentVersionId = contentVersionIds.get(0);
                }
            } else {

                try {
                    contentVersionId = jdbcTemplate.queryForInt("select ContentVersionId from contentversion where ContentId = ? and Version = ? order by ContentVersionId desc", contentId, requestedVersion);
                } catch (EmptyResultDataAccessException e) {
                    return null;
                }
            }
        } else if(cid.getStatus() == ContentStatus.HEARING) {
            // Find version for hearing, if no hearing is found, active version is returned
            int activeversion = jdbcTemplate.queryForInt("select ContentVersionId from contentversion where ContentId = ? and contentversion.IsActive = 1 order by ContentVersionId desc", contentId);
            contentVersionId = jdbcTemplate.queryForInt("select ContentVersionId from contentversion where ContentId = ? AND Status = ? AND ContentVersionId > ? order by ContentVersionId desc", contentId, ContentStatus.HEARING.getTypeAsInt(), activeversion);
        } else {
            // Others should see active version
            contentVersionId = -1;
        }


        StringBuilder query = new StringBuilder("select * from content, contentversion where content.ContentId = contentversion.ContentId");
        List<Integer> params = new ArrayList<>(2);
        if (contentVersionId != -1) {
            // Hent angitt versjon
            query.append(" and contentversion.ContentVersionId = ?");
            params.add(contentVersionId);
        } else {
            // Hent aktiv versjon
            query.append(" and contentversion.IsActive = 1");
        }
        query.append(" and content.ContentId = ? order by ContentVersionId");
        params.add(contentId);

        Content content = null;
        try {
            content = jdbcTemplate.queryForObject(query.toString(), new ContentRowMapper(false), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        List<Association> associations = jdbcTemplate.query("SELECT * FROM associations WHERE ContentId = ? AND (IsDeleted IS NULL OR IsDeleted = 0)", new AssociationRowMapper(), contentId);

        // Get associations for this page
        boolean foundCurrentAssociation = false;
        for(Association a : associations){
            if (!foundCurrentAssociation) {
                // Dersom knytningsid ikke er angitt bruker vi default for angitt site
                int associationId = cid.getAssociationId();
                if ((associationId == a.getId()) || (associationId == -1
                        && a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE
                        && a.getSiteId() == cid.getSiteId())) {
                    foundCurrentAssociation = true;
                    a.setCurrent(true);
                }
            }
            content.addAssociation(a);
        }

        if (!foundCurrentAssociation) {
            // Knytningsid er ikke angitt, og heller ikke site, bruk den første
            for (Association a : associations) {
                if (a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                    foundCurrentAssociation = true;
                    a.setCurrent(true);
                    break;
                }
            }

            if (!foundCurrentAssociation && associations.size() > 0) {
                Association a = associations.get(0);
                a.setCurrent(true);
                log.debug( "Fant ingen defaultknytning:" + contentId);
            }
        }

        if (content.getAssociation() == null) {
            // All associations to page are deleted, dont return page
            return null;
        }

        // Get content attributes
        jdbcTemplate.query("select * from contentattributes where ContentVersionId = ?", new ContentAttributeRowMapper(content), content.getVersionId());

        List<Topic> topics = topicDao.getTopicsByContentId(contentId);
        content.setTopics(topics);

        return content;
    }

    /**
     * Looks up the published page associated with a User's {@link OrgUnit}.
     *
     * @param orgUnit Organization unit belonging to a user.
     * @return Content object of the organization unit page; {@code null} if it does not exist.
     * @throws SystemException
     */
    @Override
    public Content getContent(OrgUnit orgUnit) throws SystemException {
        Content content = null;

        try(Connection conn = dbConnectionFactory.getConnection()){

            PreparedStatement ps = conn.prepareStatement("select ContentId, ContentTemplateId from content where Owner = ? and (ContentTemplateId = 7 or ContentTemplateId = 13) order by ContentTemplateId");
            ps.setString(1, orgUnit.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int contentId = rs.getInt("ContentId");
                ContentIdentifier contentIdentifier =  ContentIdentifier.fromContentId(contentId);
                content = getContent(contentIdentifier, true);
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        return content;
    }

    @Override
    public String getTitleByAssociationId(int associationId) throws ContentNotFoundException{
        // When content it cross published the query result is the same title duplicated.
        List<String> titles = getJdbcTemplate().queryForList("select contentversion.title from content, contentversion, associations where content.ContentId = contentversion.ContentId and associations.AssociationId=? and contentversion.Status in (?) and contentversion.IsActive = 1 and content.ContentId = associations.ContentId and associations.IsDeleted = 0", String.class, associationId, ContentStatus.PUBLISHED.getTypeAsInt());
        if (titles.isEmpty()) {
            throw new ContentNotFoundException("Content with associationId " + associationId);
        } else {
            return titles.get(0);
        }
    }

    @Override
    public List<WorkList<Content>> getMyContentList(User user) throws SystemException {
        List<WorkList<Content>> workList = new ArrayList<>();

        WorkList<Content> draft = new WorkList<>();
        draft.setDescription("draft");

        WorkList<Content> waiting = new WorkList<>();
        waiting.setDescription("waiting");

        WorkList<Content> rejected = new WorkList<>();
        rejected.setDescription("rejected");

        WorkList<Content> lastpublished = new WorkList<>();
        lastpublished.setDescription("lastpublished");

        WorkList<Content> remind = new WorkList<>();
        remind.setDescription("remind");

        try(Connection c = dbConnectionFactory.getConnection()){

            // Get drafts, pages waiting for approval and rejected pages
            PreparedStatement st = c.prepareStatement("select * from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.Status in (?,?,?) and content.ContentId = associations.ContentId and associations.IsDeleted = 0 and contentversion.LastModifiedBy = ? and associations.Type = ? order by contentversion.Status, contentversion.LastModified desc");
            st.setInt(1, ContentStatus.DRAFT.getTypeAsInt());
            st.setInt(2, ContentStatus.WAITING_FOR_APPROVAL.getTypeAsInt());
            st.setInt(3, ContentStatus.REJECTED.getTypeAsInt());
            st.setString(4, user.getId());
            st.setInt(5, AssociationType.DEFAULT_POSTING_FOR_SITE);
            ResultSet rs = st.executeQuery();

            int prevContentId = -1;
            while (rs.next()) {
                Content content = ContentAOHelper.getContentFromRS(rs, true);
                if (content.getId() != prevContentId) {
                    prevContentId = content.getId();
                    if (content.getStatus() == ContentStatus.DRAFT) {
                        draft.add(content);
                    } else if (content.getStatus() == ContentStatus.WAITING_FOR_APPROVAL) {
                        waiting.add(content);
                    } else if (content.getStatus() == ContentStatus.REJECTED) {
                        rejected.add(content);
                    }
                }
            }

            st.close();
            rs.close();

            // Get pages which expire soon
            st = c.prepareStatement("select * from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.Status = ? and content.ExpireAction = ? and content.ContentId = associations.ContentId and associations.IsDeleted = 0 and contentversion.LastModifiedBy = ? and content.ExpireDate > ? and content.ExpireDate < ? order by content.ExpireDate desc");
            st.setInt(1, ContentStatus.PUBLISHED.getTypeAsInt());
            st.setString(2, ExpireAction.REMIND.name());
            st.setString(3, user.getId());
            st.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            st.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()+(long)1000*60*60*24*30));

            rs = st.executeQuery();
            prevContentId = -1;
            while (rs.next()) {
                Content content = ContentAOHelper.getContentFromRS(rs, true);
                if (content.getId() != prevContentId) {
                    prevContentId = content.getId();
                    remind.add(content);
                }
            }

            st.close();
            rs.close();

            // Get the 10 last modified pages
            st = c.prepareStatement("select * from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.Status = ? and content.ContentId = associations.ContentId and associations.IsDeleted = 0 and contentversion.LastModifiedBy = ? and LastModified > ? order by contentversion.LastModified desc");
            st.setInt(1, ContentStatus.PUBLISHED.getTypeAsInt());
            st.setString(2, user.getId());
            st.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()- 1000L *60*60*24*90));
            rs = st.executeQuery();
            int i = 0;
            prevContentId = -1;
            while (rs.next() && i < 10) {
                Content content = ContentAOHelper.getContentFromRS(rs, true);
                if (content.getId() != prevContentId) {
                    prevContentId = content.getId();
                    lastpublished.add(content);
                    i++;
                }
            }

            st.close();
            rs.close();

            if (draft.size() > 0) {
                workList.add(draft);
            }

            if (waiting.size() > 0) {
                workList.add(waiting);
            }

            if (rejected.size() > 0) {
                workList.add(rejected);
            }

            if (remind.size() > 0) {
                workList.add(remind);
            }

            if (lastpublished.size() > 0) {
                workList.add(lastpublished);
            }

        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
        return workList;
    }

    @Override
    public List<Content> getContentListForApproval() throws SystemException {
        List<Content> contentList = new ArrayList<>();

        try(Connection c = dbConnectionFactory.getConnection()){

            // Hent content og contentversion
            PreparedStatement st = c.prepareStatement("select * from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.Status in (?) and content.ContentId = associations.ContentId and associations.IsDeleted = 0 order by contentversion.Title");
            st.setInt(1, ContentStatus.WAITING_FOR_APPROVAL.getTypeAsInt());
            ResultSet rs = st.executeQuery();
            int prevContentId = -1;
            while (rs.next()) {
                Content content = ContentAOHelper.getContentFromRS(rs, true);
                if (content.getId() != prevContentId) {
                    prevContentId = content.getId();
                    contentList.add(content);
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
        return contentList;
    }

    @Override
    public List<Content> getContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, boolean getAttributes) {
        return getContentList(contentQuery, maxElements, sort, getAttributes, false);
    }

    @Override
    public List<Content> getContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) {
        final Map<Integer, Content> contentMap   = new HashMap<>();
        final List<Content> contentList = new ArrayList<>();

        doForEachInContentList(contentQuery, maxElements, sort, new ContentHandler() {
            public void handleContent(Content content) {
                contentList.add(content);
                contentMap.put(content.getVersionId(), content);
            }
        });


        int listSize = contentList.size();
        if (listSize > 0 && getAttributes) {
            // Hent attributter
            String attrquery = "select * from contentattributes where ContentVersionId in (" + join(contentMap.keySet(), ',') +") order by ContentVersionId";
            getNamedParameterJdbcTemplate().query(attrquery, Collections.<String, Object>emptyMap(), new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    int cvid = rs.getInt("ContentVersionId");
                    Content current = contentMap.get(cvid);
                    if (current != null) {
                        ContentAOHelper.addAttributeFromRS(current, rs);
                    }
                }
            });
        }

        if (listSize > 0 && getTopics) {
            // Hent topics
            for (Content content : contentList) {
                List<Topic> topics = topicDao.getTopicsByContentId(content.getId());
                content.setTopics(topics);
            }
        }

        if (sort != null) {
            // Sorter lista
            String sort1 = sort.getSort1();
            String sort2 = sort.getSort2();

            List<ContentIdentifier> cids = contentQuery.getContentList();
            if (cids != null && ContentProperty.PRIORITY.equalsIgnoreCase(sort1)) {
                Comparator<Content> comparator = new AssociationIdListComparator(cids);
                Collections.sort(contentList, comparator);
            } else {
                // Kan sorteres etter inntil to kriterier
                if (sort2 != null) {
                    Comparator<Content> comparator = new ContentComparator(this, sort2, sort.sortDescending());
                    Collections.sort(contentList, comparator);
                }

                if (!contentQuery.useSqlSort() && sort1 != null) {
                    Comparator<Content> comparator = new ContentComparator(this, sort1, sort.sortDescending());
                    Collections.sort(contentList, comparator);
                }
            }
        }

        return contentList;
    }

    @Override
    public void doForEachInContentList(final ContentQuery contentQuery, final int maxElements, SortOrder sort, final ContentHandler handler) {
        if (sort != null) {
            contentQuery.setSortOrder(sort);
        }

        // Query will be faster if we don't get all records
        contentQuery.setMaxRecords(maxElements);

        ContentQuery.QueryWithParameters queryWithParameters = contentQuery.getQueryWithParameters();

        if (queryWithParameters != null) { // null is returned when querying for attributes, and attribute-value-pair does not exists
            getNamedParameterJdbcTemplate().query(queryWithParameters.getQuery(), queryWithParameters.getParams(), new RowCallbackHandler() {
            private int count = 0;
            private ContentRowMapper contentRowMapper = new ContentRowMapper(true);
            private Set<Integer> handledContentIds = new HashSet<>();
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int contentId = rs.getInt("ContentId");
                if(handledContentIds.add(contentId) && (maxElements == -1 || count < maxElements + contentQuery.getOffset())){
                    Content content = contentRowMapper.mapRow(rs, count++);
                    handler.handleContent(content);
                }
            }
        });
        }
    }


    @Override
    public ContentIdentifier getParent(ContentIdentifier cid) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        int parentAssociationId = getJdbcTemplate().queryForInt("select ParentAssociationId from associations where AssociationId = ?", cid.getAssociationId());
        ContentIdentifier parentCid =  ContentIdentifier.fromAssociationId(parentAssociationId);
        parentCid.setLanguage(cid.getLanguage());
        return parentCid;
    }


    @Override
    public Content checkInContent(Content content, ContentStatus newStatus) throws SystemException {

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            // We only use transactions if it has been enabled
            if (dbConnectionFactory.useTransactions()) {
                c.setAutoCommit(false);
            }

            // Try to lock content in database
            addContentTransactionLock(content.getId(), c);

            // Get old version if exists
            Content oldContent = null;
            boolean isNew = content.isNew();
            if (!isNew) {
                ContentIdentifier oldCid =  ContentIdentifier.fromAssociationId(content.getAssociation().getAssociationId());
                oldContent = getContent(oldCid, true);
            }

            boolean newVersionIsActive = false;

            if (isNew) {
                // New page, always active
                newVersionIsActive = true;
            }

            // Insert base information, no history
            insertOrUpdateContentTable(c, content);

            deleteTempContentVersion(content);

            if (isNew || newStatus == ContentStatus.PUBLISHED) {
                newVersionIsActive = true;
            }

            if (!isNew) {
                newVersionIsActive = archiveOldVersion(c, content, newStatus, newVersionIsActive);
            }

            addContentVersion(c, content, newStatus, newVersionIsActive);

            setVersionAsActive(c, content);

            // Add page attributes
            insertAttributes(c, content, AttributeDataType.CONTENT_DATA);
            insertAttributes(c, content, AttributeDataType.META_DATA);

            // Insert associations if new
            List<Association> associations = content.getAssociations();
            for (Association association : associations) {
                if (association.getId() == -1) {
                    // New association: add
                    association.setContentId(content.getId());
                    AssociationAO.addAssociation(association);
                }
            }

            // Update contentid on attachments saved in database before the page was saved
            List<Attachment> attachments = content.getAttachments();
            if (attachments != null) {
                for (Attachment a : attachments) {
                    a.setContentId(content.getId());
                    AttachmentAO.setAttachment(a);
                }
            }

            // Update contentid on multimedia saved in database before the page was saved
            List<Multimedia> multimedia = content.getMultimedia();
            if (multimedia != null) {
                for (Multimedia m : multimedia) {
                    PreparedStatement st = c.prepareStatement("update multimedia set ContentId = ?  where Id = ?");
                    st.setInt(1, content.getId());
                    st.setInt(2, m.getId());
                    st.executeUpdate();
                }
            }


            // Delete all existing topic associations before insertion.
            topicDao.deleteTopicAssociationsForContent(content.getId());

            // Insert topics
            List<Topic> topics = content.getTopics();
            if (topics != null) {
                for (Topic t : topics) {
                    topicDao.addTopicToContentAssociation(t, content.getId());
                }
            }

            // Update subpages if these fields are changed
            if(oldContent != null ) {
                if (!oldContent.getOwner().equals(content.getOwner())) {
                    updateChildren(content.getAssociation().getId(), "owner", content.getOwner(), oldContent.getOwner());
                }
                if (!oldContent.getOwnerPerson().equals(content.getOwnerPerson())) {
                    updateChildren(content.getAssociation().getId(), "ownerperson", content.getOwnerPerson(), oldContent.getOwnerPerson());
                }
                if (oldContent.getGroupId() != content.getGroupId()) {
                    updateChildren(content.getAssociation().getId(), "GroupId", String.valueOf(content.getGroupId()), String.valueOf(oldContent.getGroupId()));
                }
            }

            // Set page as not checked out
            content.setIsCheckedOut(false);

            // Mark as not modified
            content.setIsModified(false);

            // Remove lock
            removeContentTransactionLock(content.getId(), c);

            // We only use transactions for databases which support it
            if (dbConnectionFactory.useTransactions()) {
                c.commit();
            }
        } catch (TransactionLockException tle) {
            if (c != null) {
                try {
                    if (dbConnectionFactory.useTransactions() && c != null) {
                        c.rollback();
                    }
                } catch (SQLException e1) {
                    log.error("Error rolling back transaction", e1);
                }
            }
            throw tle;
        } catch (Exception e) {
            if (c != null) {
                try {
                    if (dbConnectionFactory.useTransactions() && c != null) {
                        c.rollback();
                    }
                } catch (SQLException e1) {
                    log.error("Error rolling back transaction", e);
                }
            }
            throw new SystemException("Feil ved lagring", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                // Could not close connection, probably closed already
            }
        }


        // Delete old versions (only keep last n versions)
        ContentTemplate ct = ContentTemplateCache.getTemplateById(content.getContentTemplateId());

        int keepVersions = ct.computeKeepVersions();
        // -1 = keep all versions
        if (keepVersions != -1) {
            deleteOldContentVersions(content, keepVersions);
        }

        // Set new status
        content.setStatus(newStatus);

        return content;
    }

    private void setVersionAsActive(Connection c, Content content) throws SQLException {
        PreparedStatement st;
        ResultSet rs;

        // Update status to active if no active version exists
        st = c.prepareStatement("select * from contentversion where IsActive = 1 and ContentId = ?");
        st.setInt(1, content.getId());
        rs = st.executeQuery();
        if (!rs.next()) {
            // No active version found, set this one as active
            st = c.prepareStatement("update contentversion set IsActive = 1 where ContentVersionId = ?");
            st.setInt(1, content.getVersionId());
            st.executeUpdate();
        }
    }

    private boolean archiveOldVersion(Connection c, Content content, ContentStatus newStatus, boolean newVersionIsActive) throws SQLException {
        // Find next version
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        List<Integer> currentVersions = jdbcTemplate.queryForList("select version from contentversion where ContentId = ? order by version desc", Integer.class, content.getId());
        if (!currentVersions.isEmpty()) {
            content.setVersion(currentVersions.get(0) + 1);
        }

        if (newStatus == ContentStatus.PUBLISHED) {
            // Set newStatus = ARCHIVED on currently active version
            jdbcTemplate.update("update contentversion set Status = ?, isActive = 0 where ContentId = ? and isActive = 1", ContentStatus.ARCHIVED.getTypeAsInt(), content.getId());

            // Publisert blir aktiv versjon
            newVersionIsActive = true;
        }
        return newVersionIsActive;
    }

    private void deleteTempContentVersion(Content content) {
        // If this is a draft, rejected page etc delete previous version
        if (content.getStatus() == ContentStatus.DRAFT || content.getStatus() == ContentStatus.WAITING_FOR_APPROVAL || content.getStatus() == ContentStatus.REJECTED) {
            // Delete this (previous) version
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(content.getAssociation().getId());
            cid.setVersion(content.getVersion());
            cid.setLanguage(content.getLanguage());
            deleteContentVersion(cid, true);
        }
    }


    private void addContentVersion(Connection c, Content content, ContentStatus newStatus, boolean activeVersion) throws SQLException {
        // Insert new version
        PreparedStatement contentVersionSt = c.prepareStatement("insert into contentversion (ContentId, Version, Status, IsActive, Language, Title, AltTitle, Description, Image, Keywords, Publisher, LastModified, LastModifiedBy, ChangeDescription, ApprovedBy, ChangeFrom, IsMinorChange, LastMajorChange, LastMajorChangeBy) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        contentVersionSt.setInt(1, content.getId());
        contentVersionSt.setInt(2, content.getVersion());
        contentVersionSt.setInt(3, newStatus.getTypeAsInt());
        contentVersionSt.setInt(4, activeVersion ? 1 : 0);
        contentVersionSt.setInt(5, content.getLanguage());
        contentVersionSt.setString(6, content.getTitle());
        contentVersionSt.setString(7, content.getAltTitle());
        contentVersionSt.setString(8, content.getDescription());
        contentVersionSt.setString(9, content.getImage());
        contentVersionSt.setString(10, content.getKeywords());
        contentVersionSt.setString(11, content.getPublisher());
        contentVersionSt.setTimestamp(12, new Timestamp(new Date().getTime()));
        contentVersionSt.setString(13, content.getModifiedBy());
        contentVersionSt.setString(14, content.getChangeDescription());
        contentVersionSt.setString(15, content.getApprovedBy());
        contentVersionSt.setTimestamp(16, content.getChangeFromDate() == null ? null : new Timestamp(content.getChangeFromDate().getTime()));
        contentVersionSt.setInt(17, content.isMinorChange() ? 1 : 0);
        contentVersionSt.setTimestamp(18, content.getLastMajorChange() == null ? new Timestamp(new Date().getTime()) : new Timestamp(content.getLastMajorChange().getTime()));
        contentVersionSt.setString(19, content.getLastMajorChangeBy());

        contentVersionSt.execute();

        ResultSet rs = contentVersionSt.getGeneratedKeys();
        if (rs.next()) {
            content.setVersionId(rs.getInt(1));
        }
        rs.close();
        contentVersionSt.close();
    }

    private void insertOrUpdateContentTable(Connection c, Content content) throws SQLException {
        PreparedStatement contentSt;

        boolean isNew = content.isNew();
        if (isNew) {
            contentSt = c.prepareStatement("insert into content (ContentType, ContentTemplateId, MetadataTemplateId, DisplayTemplateId, DocumentTypeId, GroupId, Owner, OwnerPerson, Location, Alias, PublishDate, ExpireDate, RevisionDate, ExpireAction, VisibilityStatus, ForumId, NumberOfNotes, OpenInNewWindow, DocumentTypeIdForChildren, IsLocked, RatingScore, NumberOfRatings, IsSearchable, NumberOfComments) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,?,?,?,0,0,?,0)", Statement.RETURN_GENERATED_KEYS);
        } else {
            // Update
            contentSt = c.prepareStatement("update content set ContentType = ?, ContentTemplateId = ?, MetaDataTemplateId = ?, DisplayTemplateId = ?, DocumentTypeId = ?, GroupId = ?, Owner = ?, OwnerPerson=?, Location = ?, Alias = ?, PublishDate = ?, ExpireDate = ?, RevisionDate=?, ExpireAction = ?, VisibilityStatus = ?, ForumId=?, OpenInNewWindow=?, DocumentTypeIdForChildren = ?, IsLocked = ?, IsSearchable = ? where ContentId = ?");
        }

        int p = 1;
        contentSt.setInt(p++, content.getType().getTypeAsInt());
        contentSt.setInt(p++, content.getContentTemplateId());
        contentSt.setInt(p++, content.getMetaDataTemplateId());
        contentSt.setInt(p++, content.getDisplayTemplateId());
        contentSt.setInt(p++, content.getDocumentTypeId());
        contentSt.setInt(p++, content.getGroupId());
        contentSt.setString(p++, content.getOwner());
        contentSt.setString(p++, content.getOwnerPerson());
        contentSt.setString(p++, content.getLocation());
        contentSt.setString(p++, content.getAlias());
        contentSt.setTimestamp(p++, content.getPublishDate() == null ? null : new Timestamp(content.getPublishDate().getTime()));
        contentSt.setTimestamp(p++, content.getExpireDate() == null ? null : new Timestamp(content.getExpireDate().getTime()));
        contentSt.setTimestamp(p++, content.getRevisionDate() == null ? null : new Timestamp(content.getRevisionDate().getTime()));
        contentSt.setString(p++, content.getExpireAction().name());
        contentSt.setInt(p++, content.getVisibilityStatus());
        contentSt.setLong(p++, content.getForumId());
        contentSt.setInt(p++, content.isOpenInNewWindow() ? 1:0);
        contentSt.setInt(p++, content.getDocumentTypeIdForChildren());
        contentSt.setInt(p++, content.isLocked() ? 1:0);
        contentSt.setInt(p++, content.isSearchable() ? 1:0);
        if (!isNew) {
            contentSt.setInt(p, content.getId());
        }

        contentSt.execute();

        if (isNew) {
            // Finn id til nytt objekt
            ResultSet rs = contentSt.getGeneratedKeys();
            if (rs.next()) {
                content.setId(rs.getInt(1));
            }
            rs.close();
        }
        contentSt.close();


        if (isNew) {
            // GroupId benyttes for å angi at en side arver egenskaper, f.eks meny, design fra en annen side
            if (content.getGroupId() <= 0) {
                PreparedStatement st = c.prepareStatement("update content set GroupId = ? where ContentId = ?");
                st.setInt(1, content.getId());
                st.setInt(2, content.getId());
                st.execute();
                st.close();
            }
        }
    }

    /**
     * Mechanism to prevent multiple instances or servers modifying same content object at the same time.
     * Will normally block when database is configured to use transactions,
     * if not it will throw an exception since the same TransactionId cannot be inserted in the transactionlocks table
     * @param contentId
     * @param c
     * @throws TransactionLockException
     */
    private void addContentTransactionLock(int contentId, Connection c) throws TransactionLockException {
        if (contentId != -1) {
            try {
                PreparedStatement lockSt = c.prepareStatement("INSERT INTO transactionlocks VALUES (?,?)");
                lockSt.setString(1, "content-" + contentId);
                lockSt.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                lockSt.executeUpdate();
            } catch (SQLException e) {
                throw new TransactionLockException("Error locking contentId:" + contentId, e);
            }
        }
    }

    /**
     * Remove transaction lock
     * @param contentId
     * @param c
     * @throws SQLException
     */
    private void removeContentTransactionLock(int contentId, Connection c) throws SQLException {
        PreparedStatement unlockSt = c.prepareStatement("DELETE from transactionlocks WHERE TransactionId = ?");
        unlockSt.setString(1, "content-" + contentId);
        unlockSt.executeUpdate();
    }

    /**
     * Updated field on subpages
     * @param associationId
     * @param field
     * @param newValue
     * @param oldValue
     * @throws SQLException
     */
    private void updateChildren(int associationId, String field, String newValue, String oldValue) throws SQLException {

        List<Integer> childrenIds = getJdbcTemplate().queryForList("select content.contentid from content, associations where content." + field + " = ? and associations.path like ? and content.ContentId=associations.ContentId",
                Integer.class, oldValue, "%/" + associationId + "/%");

        if (childrenIds.size() > 0) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("value", newValue);
            params.put("childrenids", childrenIds);
            getNamedParameterJdbcTemplate().update("update content set " + field + " = :value where ContentId in (:childrenids)", params);
        }
    }

    /**
     * Delete old contentversions
     * @param content
     * @param maxVersions
     * @throws SystemException
     */
    private void deleteOldContentVersions(Content content, int maxVersions) {
        try(Connection c = dbConnectionFactory.getConnection()){

            PreparedStatement st = c.prepareStatement("select * from contentversion where ContentId = ? and Status <> ? order by Version desc");
            st.setInt(1, content.getId());
            st.setInt(2, ContentStatus.PUBLISHED.getTypeAsInt());
            ResultSet rs = st.executeQuery();
            int noVersions = 0;
            while(rs.next()) {
                int ver = rs.getInt("Version");
                noVersions++;
                if (noVersions > maxVersions ) {
                    // Slett denne versjonen
                    ContentIdentifier cid =  ContentIdentifier.fromAssociationId(content.getAssociation().getId());
                    cid.setVersion(ver);
                    cid.setLanguage(content.getLanguage());

                    deleteContentVersion(cid, false);
                }
            }

            st.close();

        } catch (SQLException e) {
            throw new SystemException("Feil ved lagring", e);
        }
    }

    @Override
    public Content setContentStatus(ContentIdentifier cid, ContentStatus newStatus, Date newPublishDate, String userId) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);

        int contentId = cid.getContentId();
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        List<Integer> versions = jdbcTemplate.queryForList("select Version from contentversion where ContentId = ? AND status IN (?, ?) order by version desc", Integer.class, contentId, ContentStatus.WAITING_FOR_APPROVAL.getTypeAsInt(), ContentStatus.PUBLISHED_WAITING.getTypeAsInt());
        if(versions.isEmpty()){
            throw new IllegalStateException("Could not fint content version");
        }
        int version = versions.get(0);

        if (version != -1) {
            if (newStatus == ContentStatus.PUBLISHED) {
                // Sett status = arkivert på aktiv versjon
                jdbcTemplate.update("update contentversion set status = ?, isActive = 0 where ContentId = ? and isActive = 1", ContentStatus.ARCHIVED.getTypeAsInt(), contentId);

                jdbcTemplate.update("update contentversion set status = ?, isActive = 1, ApprovedBy = ?, ChangeFrom = null where ContentId = ? and Version = ?",
                        ContentStatus.PUBLISHED.getTypeAsInt(), userId, contentId, version);

                if (newPublishDate != null) {
                    // Set publish date if not set
                    jdbcTemplate.update("update content set PublishDate = ? where ContentId = ?", newPublishDate, contentId);
                }
            } else {
                jdbcTemplate.update("update contentversion set status = ? where ContentId = ? and Version = ?",
                        newStatus.getTypeAsInt(), contentId, cid.getVersion() == -1 ? version : cid.getVersion());
            }
        }


        Content content = getContent(cid, false);

        content.setStatus(newStatus);

        return content;
    }

    private void insertAttributes(final Connection c, final Content content, final int type) throws SQLException, SystemException {
        content.doForEachAttribute(type, new AttributeHandler() {
            public void handleAttribute(Attribute attr) {
                PersistAttributeBehaviour attributeSaver = attr.getSaveBehaviour();
                try {
                    attributeSaver.persistAttribute(c, content, attr);
                } catch (SQLException e) {
                    log.error("Error persisting attribute " + attr, e);
                    throw new SystemException("Error saving attribute", e);
                }
            }
        });
    }

    @Override
    public int getNextExpiredContentId(int after) throws SystemException {

        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("SELECT ContentId FROM content WHERE ExpireDate < ? AND VisibilityStatus = ? AND ContentId > ? ORDER BY ContentId");
            p.setTimestamp(1, new Timestamp(new Date().getTime()));
            p.setInt(2, ContentVisibilityStatus.ACTIVE);
            p.setInt(3, after);
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("ContentId");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), e);
        }
    }

    @Override
    public int getNextWaitingContentId(int after) throws SystemException {

        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("SELECT ContentId FROM content WHERE PublishDate > ? AND VisibilityStatus = ? AND ContentId > ? ORDER BY ContentId");
            p.setTimestamp(1, new Timestamp(new Date().getTime()));
            p.setInt(2, ContentVisibilityStatus.ACTIVE);
            p.setInt(3, after);
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("ContentId");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), e);
        }
    }


    @Override
    public int getNextActivationContentId(int after) throws SystemException {

        try(Connection c = dbConnectionFactory.getConnection()){
            long now = new Date().getTime() + 1000*60*1;
            PreparedStatement p = c.prepareStatement("SELECT DISTINCT content.ContentId FROM content,contentversion " +
                    "WHERE content.contentId=contentversion.contentid " +
                    "AND ((PublishDate < ? AND VisibilityStatus = ?) " +
                    "OR (VisibilityStatus IN (?,?) AND (ExpireDate IS NULL OR ExpireDate > ?)) " +
                    "OR (Status = ? AND ChangeFrom < ?)) " +
                    "AND content.ContentId > ? " +
                    "ORDER BY content.ContentId");
            p.setTimestamp(1, new Timestamp(now));
            p.setInt(2, ContentVisibilityStatus.WAITING);
            p.setInt(3, ContentVisibilityStatus.ARCHIVED);
            p.setInt(4, ContentVisibilityStatus.EXPIRED);
            p.setTimestamp(5, new Timestamp(now));
            p.setInt(6, ContentStatus.PUBLISHED_WAITING.getTypeAsInt());
            p.setTimestamp(7, new Timestamp(now));
            p.setInt(8, after);
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("ContentId");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), e);
        }
    }


    @Override
    public void setContentVisibilityStatus(int contentId, int newStatus) throws SystemException {

        try(Connection c = dbConnectionFactory.getConnection()){

            PreparedStatement tmp = c.prepareStatement("update content set VisibilityStatus = ? where ContentId = ?");
            tmp.setInt(1, newStatus);
            tmp.setInt(2, contentId);
            tmp.execute();
            tmp.close();

        } catch (SQLException e) {
            throw new SystemException("Feil ved setting av visningsstatus", e);
        }
    }

    @Override
    public void setNumberOfNotes(int contentId, int count) throws SystemException {

        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("UPDATE content SET NumberOfNotes = ? WHERE ContentId = ?");
            p.setInt(1, count);
            p.setInt(2, contentId);
            p.executeUpdate();

        } catch (SQLException e) {
            throw new SystemException("Feil ved setting av NumberOfNotes", e);
        }
    }

    @Override
    public List<UserContentChanges> getNoChangesPerUser(int months) throws SystemException {
        List<UserContentChanges> ucclist = new ArrayList<>();
        try(Connection c = dbConnectionFactory.getConnection()){

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MONTH, -months);

            PreparedStatement p = c.prepareStatement("select count(contentversion.lastmodifiedby) as nochanges, contentversion.lastmodifiedby from contentversion where contentversion.lastmodified > ? group by lastmodifiedby order by nochanges desc");
            p.setDate(1, new java.sql.Date(calendar.getTime().getTime()));
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                int count = rs.getInt(1);
                String userName = rs.getString(2);
                if (userName != null) {
                    UserContentChanges ucc = new UserContentChanges();
                    ucc.setNoChanges(count);
                    ucc.setUserName(userName);
                    ucclist.add(ucc);
                }
            }
            return ucclist;
        } catch (SQLException e) {
            throw new SystemException("SQL error",e);
        }
    }

    @Override
    public int getContentCount() throws SystemException {
        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("SELECT COUNT(*) AS count FROM content WHERE VisibilityStatus = ? AND ContentType = ?");
            p.setInt(1, ContentVisibilityStatus.ACTIVE);
            p.setInt(2, ContentType.PAGE.getTypeAsInt());
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), e);
        }
    }

    @Override
    public int getLinkCount() throws SystemException {
        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("SELECT COUNT(*) AS count FROM content WHERE VisibilityStatus = ? AND ContentType = ?");
            p.setInt(1, ContentVisibilityStatus.ACTIVE);
            p.setInt(2, ContentType.LINK.getTypeAsInt());
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), e);
        }
    }

    @Override
    public int getContentProducerCount() throws SystemException {
        try(Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("SELECT COUNT(DISTINCT LastModifiedBy) AS count FROM contentversion");
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), e);
        }
    }

    @Override
    public void updateContentFromTemplates(TemplateConfiguration templateConfiguration) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        for (DisplayTemplate dt : templateConfiguration.getDisplayTemplates()) {
            int contentTemplateId = dt.getContentTemplate().getId();
            int metadataTemplateId = -1;
            if (dt.getMetaDataTemplate() != null) {
                metadataTemplateId = dt.getMetaDataTemplate().getId();
            }

            // Update database with correct value for ContentTemplateId and MetadataTemplateId
            jdbcTemplate.update("update content set ContentTemplateId = ? where DisplayTemplateId = ? and ContentTemplateId <> ?", contentTemplateId, dt.getId(), contentTemplateId);
            jdbcTemplate.update("update content set MetaDataTemplateId = ? where DisplayTemplateId = ? and MetaDataTemplateId <> ?", metadataTemplateId, dt.getId(), metadataTemplateId);
        }

        for (ContentTemplate ct : templateConfiguration.getContentTemplates()) {
            jdbcTemplate.update("update content set ContentType = ? where ContentTemplateId = ? and ContentType <> ?", ct.getContentType().getTypeAsInt(), ct.getId(), ct.getContentType().getTypeAsInt());
        }
    }

    @Override
    public boolean hasBeenPublished(int contentId) {
        if (contentId == -1) {
            return false;
        }
        int cnt = getJdbcTemplate().queryForInt("select count(*) from contentversion where ContentId = ? and status IN (?,?)", contentId, ContentStatus.PUBLISHED.getTypeAsInt(), ContentStatus.ARCHIVED.getTypeAsInt());
        return cnt > 0;
    }

    /**
     * Set new rating score (average rating) for content
     * @param contentId - ContentId
     * @param score - score
     * @param numberOfRatings - numberOfRatings
     */
    public void setRating(int contentId, float score, int numberOfRatings) {
        JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
        template.update("update content set RatingScore = ?, NumberOfRatings = ? where ContentId = ?", score, numberOfRatings, contentId);
    }

    /**
     * Set number of comments for content
     * @param contentId - ContentId
     * @param numberOfComments - numberOfComments
     */
    public void setNumberOfComments(int contentId, int numberOfComments) {
        JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
        template.update("update content set NumberOfComments = ? where ContentId = ?", numberOfComments, contentId);
    }



    @Override
    public void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren) throws SystemException{
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        int contentId = cid.getContentId();
        try(Connection c = dbConnectionFactory.getConnection()){

            PreparedStatement p = c.prepareStatement("UPDATE content SET PublishDate = ?, ExpireDate = ? WHERE ContentId = ?");
            p.setTimestamp(1, publishDate == null ? null : new java.sql.Timestamp(publishDate.getTime()));
            p.setTimestamp(2, expireDate == null ? null : new java.sql.Timestamp(expireDate.getTime()));
            p.setInt(3, contentId);
            p.executeUpdate();

            if (updateChildren) {
                p = c.prepareStatement("UPDATE content SET PublishDate = ?, ExpireDate = ? WHERE ContentId IN (SELECT ContentId FROM associations WHERE Path LIKE ?)");
                p.setTimestamp(1, publishDate == null ? null : new java.sql.Timestamp(publishDate.getTime()));
                p.setTimestamp(2, expireDate == null ? null : new java.sql.Timestamp(expireDate.getTime()));
                p.setString(3, "%/" + cid.getAssociationId() +"/%");
                p.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SystemException("SQL error",e);
        }
    }


}
