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
import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;
import no.kantega.publishing.common.AssociationIdListComparator;
import no.kantega.publishing.common.ContentComparator;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.data.Topic;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 *
 */
public class ContentAO {
    private static final String SOURCE = "aksess.ContentAO";
    private static Logger log = Logger.getLogger(ContentAO.class);

    public static ContentIdentifier deleteContent(ContentIdentifier cid) throws SystemException, ObjectInUseException {
        ContentIdentifier parent = getParent(cid);

        Connection c = null;

        try {
            int id = cid.getContentId();

            c = dbConnectionFactory.getConnection();

            // Slett tilgangsrettigheter
            PreparedStatement st = c.prepareStatement("delete from objectpermissions where ObjectSecurityId in (select AssociationId from associations where ContentId = ?) and ObjectType = ?");
            st.setInt(1, id);
            st.setInt(2, ObjectType.ASSOCIATION);
            st.execute();

            // Slett knytninger dette elementet har til andre element og andre elements knytning til dette
            st = c.prepareStatement("delete from associations where ContentId = ?");
            st.setInt(1, id);
            st.execute();
            st.close();

            // Slett innholdsattributter
            st = c.prepareStatement("delete from contentattributes where ContentVersionId in (select ContentVersionId from contentversion where ContentId = ?)");
            st.setInt(1, id);
            st.execute();
            st.close();

            // Slett høring
            try {
                st = c.prepareStatement("delete from hearing where ContentVersionId in (select ContentVersionId from contentversion where ContentId = ?)");
                st.setInt(1, id);
                st.execute();
                st.close();

                st = c.prepareStatement("delete from hearinginvitee where HearingId not in (select HearingId from hearing)");
                st.execute();
                st.close();

                st = c.prepareStatement("delete from hearingcomment where HearingId not in (select HearingId from hearing)");
                st.execute();
                st.close();
            } catch (Exception e) {
                // Kunden bruker ikke høring, har ikke tabeller for høring
            }

            // Slett vedlegg
            st = c.prepareStatement("delete from attachments where ContentId = ?");
            st.setInt(1, id);
            st.execute();
            st.close();

            // Slett innhold
            st = c.prepareStatement("delete from contentversion where ContentId = ?");
            st.setInt(1, id);
            st.execute();
            st.close();

            st = c.prepareStatement("delete from content where ContentId = ?");
            st.setInt(1, id);
            st.execute();
            st.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return parent;
    }

    public static void forAllContentObjects(final ContentHandler contentHandler, ContentHandlerStopper stopper) {

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT ContentId FROM content");

            ResultSet resultSet = p.executeQuery();

            while(resultSet.next() && !stopper.isStopRequested()) {

                ContentIdentifier contentIdentifier = new ContentIdentifier();
                contentIdentifier.setContentId(resultSet.getInt("ContentId"));

                Content content = null;
                try {
                    content = ContentAO.getContent(contentIdentifier, true);                    
                } catch (Exception ex) {
                    log.error(ex);
                }

                if(content != null) {
                    contentHandler.handleContent(content);
                }
            }

        } catch (SystemException e) {
            log.error(e);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

    }


    public static void deleteContentVersion(ContentIdentifier cid, boolean deleteActiveVersion) throws SystemException {
        int id = cid.getContentId();
        int version = cid.getVersion();
        int language = cid.getLanguage();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            // Check status for page
            PreparedStatement st = c.prepareStatement("select * from contentversion where ContentId = ? and Version = ? and Language = ?");
            st.setInt(1, id);
            st.setInt(2, version);
            st.setInt(3, language);
            ResultSet rs = st.executeQuery();

            int isActive = 0;

            int cvid = -1;
            if (rs.next()) {
                cvid = rs.getInt("ContentVersionId");
                isActive = rs.getInt("IsActive");
            }

            rs.close();
            st.close();

            if (!deleteActiveVersion && isActive == 1) {
                return;
            }

            if (cvid != -1) {
                // Delete page version
                st = c.prepareStatement("delete from contentattributes where ContentVersionId = ?");
                st.setInt(1, cvid);
                st.execute();

                st = c.prepareStatement("delete from contentversion where ContentVersionId = ?");
                st.setInt(1, cvid);
                st.execute();
            }


        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static List getAllContentVersions(ContentIdentifier cid) throws SystemException {
        List contentVersions = new ArrayList();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, "select * from content, contentversion where content.ContentId = contentversion.ContentId and contentversion.Language = " + cid.getLanguage() + " and content.ContentId = " + cid.getContentId() + " order by contentversion.Version desc");
            while (rs.next()) {
                Content content = ContentAOHelper.getContentFromRS(rs, false);
                contentVersions.add(content);
            }
            rs.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
        return contentVersions;
    }


    public static Content checkOutContent(ContentIdentifier cid) throws SystemException {
        Content content = getContent(cid, true);
        content.setIsCheckedOut(true);

        return content;
    }


    public static Content getContent(ContentIdentifier cid, boolean isAdminMode) throws SystemException {

        int version = cid.getVersion();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            if (isAdminMode) {
                if (version == -1) {
                    // When in administration mode users should see last version
                    version = SQLHelper.getInt(c, "select Version from contentversion where ContentId = " + cid.getContentId() +  " order by Version desc" , "Version");
                    if (version == -1) {
                        return null;
                    }
                }
            } else if(cid.getStatus() == ContentStatus.HEARING) {
                // Find version for hearing, if no hearing is found, active version is returned
                int activeversion = SQLHelper.getInt(c, "select Version from contentversion where ContentId = " + cid.getContentId() +" and contentversion.IsActive = 1 order by Version desc" , "Version");
                version = SQLHelper.getInt(c, "select Version from contentversion where ContentId = " + cid.getContentId() +  " AND Status = " +ContentStatus.HEARING +" AND Version > " +activeversion +" order by Version desc" , "Version");

            } else {
                // Others should see active version
                version = -1;
            }


            StringBuffer query = new StringBuffer();
            query.append("select * from content, contentversion where content.ContentId = contentversion.ContentId");
            if (version != -1) {
                // Hent angitt versjon
                query.append(" and contentversion.Version = " + version);
            } else {
                // Hent aktiv versjon
                query.append(" and contentversion.IsActive = 1");
            }
            query.append(" and content.ContentId = " + cid.getContentId() + " order by ContentVersionId");

            // Get data from content and contentversion tables
            ResultSet rs = SQLHelper.getResultSet(c, query.toString());
            if (!rs.next()) {
                return null;
            }
            Content content = ContentAOHelper.getContentFromRS(rs, false);
            rs.close();

            // Get associations for this page
            boolean foundCurrentAssociation = false;
            rs = SQLHelper.getResultSet(c, "SELECT * FROM associations WHERE ContentId = " + content.getId() + " AND (IsDeleted IS NULL OR IsDeleted = 0)");
            while(rs.next()) {
                Association a = AssociationAO.getAssociationFromRS(rs);
                if (!foundCurrentAssociation) {
                    // Dersom knytningsid ikke er angitt bruker vi default for angitt site
                    if ((cid.getAssociationId() == a.getId()) || (cid.getAssociationId() == -1 && a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE && a.getSiteId() == cid.getSiteId())) {
                        foundCurrentAssociation = true;
                        a.setCurrent(true);
                    }
                }
                content.addAssociation(a);
            }
            rs.close();

            if (!foundCurrentAssociation) {
                // Knytningsid er ikke angitt, og heller ikke site, bruk den første
                List associations = content.getAssociations();
                for (int i = 0; i < associations.size(); i++) {
                    Association a = (Association)associations.get(i);
                    if (a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                        foundCurrentAssociation = true;
                        a.setCurrent(true);
                        break;
                    }
                }

                if (!foundCurrentAssociation && associations.size() > 0) {
                    Association a = (Association)associations.get(0);
                    a.setCurrent(true);
                    Log.debug(SOURCE, "Fant ingen defaultknytning:" + cid.getContentId(), null, null);
                }
            }

            if (content.getAssociation() == null) {
                // All associations to page are deleted, dont return page
                return null;
            }

            // Get content attributes
            rs = SQLHelper.getResultSet(c, "select * from contentattributes where ContentVersionId = " + content.getVersionId());
            while(rs.next()) {
                ContentAOHelper.addAttributeFromRS(content, rs);
            }
            rs.close();

            List topics = TopicAO.getTopicsByContentId(cid.getContentId());
            content.setTopics(topics);

            return content;

        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }


    public static List getMyContentList(User user) throws SystemException {
        List workList = new ArrayList();

        WorkList draft = new WorkList();
        draft.setDescription("draft");

        WorkList waiting = new WorkList();
        waiting.setDescription("waiting");

        WorkList rejected = new WorkList();
        rejected.setDescription("rejected");

        WorkList lastpublished = new WorkList();
        lastpublished.setDescription("lastpublished");

        WorkList remind = new WorkList();
        remind.setDescription("remind");

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            // Get drafts, pages waiting for approval and rejected pages
            PreparedStatement st = c.prepareStatement("select * from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.Status in (?,?,?) and content.ContentId = associations.ContentId and associations.IsDeleted = 0 and contentversion.LastModifiedBy = ? order by contentversion.Status, contentversion.LastModified desc");
            st.setInt(1, ContentStatus.DRAFT);
            st.setInt(2, ContentStatus.WAITING_FOR_APPROVAL);
            st.setInt(3, ContentStatus.REJECTED);
            st.setString(4, user.getId());
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
            st.setInt(1, ContentStatus.PUBLISHED);
            st.setInt(2, ExpireAction.REMIND);
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
            st.setInt(1, ContentStatus.PUBLISHED);
            st.setString(2, user.getId());
            st.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()-(long)1000*60*60*24*90));
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
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
        return workList;
    }


    public static List getContentListForApproval() throws SystemException {
        List contentList = new ArrayList();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            // Hent content og contentversion
            PreparedStatement st = c.prepareStatement("select * from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.Status in (?) and content.ContentId = associations.ContentId and associations.IsDeleted = 0 order by contentversion.Title");
            st.setInt(1, ContentStatus.WAITING_FOR_APPROVAL);
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
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
        return contentList;
    }

    public static List<Content> getContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, boolean getAttributes) throws SystemException {
        return getContentList(contentQuery, maxElements, sort, getAttributes, false);
    }

    public static List<Content> getContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) throws SystemException {
        final Map contentMap   = new HashMap();
        final List<Content> contentList = new ArrayList<Content>();

        final StringBuffer cvids = new StringBuffer();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            doForEachInContentList(contentQuery, maxElements, sort, new ContentHandler() {
                public void handleContent(Content content) {
                    contentList.add(content);
                    contentMap.put("" + content.getVersionId(), content);
                    if(cvids.length() != 0) {
                        cvids.append(",");
                    }
                    cvids.append(content.getVersionId());
                }
            });


            int listSize = contentList.size();
            if (listSize > 0 && getAttributes) {
                // Hent attributter
                String attrquery = "select * from contentattributes where ContentVersionId in (" + cvids.toString() + ") order by ContentVersionId";
                ResultSet rs = SQLHelper.getResultSet(c, attrquery);

                while(rs.next()) {
                    int cvid = rs.getInt("ContentVersionId");
                    Content current = (Content)contentMap.get("" + cvid);
                    if (current != null) {
                        ContentAOHelper.addAttributeFromRS(current, rs);
                    }
                }
            }

            if (listSize > 0 && getTopics) {
                // Hent topics
                for (int i = 0; i < contentList.size(); i++) {
                    Content content = (Content) contentList.get(i);
                    List topics = TopicAO.getTopicsByContentId(content.getId());
                    content.setTopics(topics);
                }
            }

            if (sort != null) {
                // Sorter lista
                String sort1 = sort.getSort1();
                String sort2 = sort.getSort2();

                ContentIdentifier[] cids = contentQuery.getContentList();
                if (cids != null && ContentProperty.PRIORITY.equalsIgnoreCase(sort1)) {
                    Comparator comparator = new AssociationIdListComparator(cids);
                    Collections.sort(contentList, comparator);
                } else {
                    // Kan sorteres etter inntil to kriterier
                    if (sort2 != null) {
                        Comparator comparator = new ContentComparator(sort2, sort.sortDescending());
                        Collections.sort(contentList, comparator);
                    }

                    if (!contentQuery.useSqlSort() && sort1 != null) {
                        Comparator comparator = new ContentComparator(sort1, sort.sortDescending());
                        Collections.sort(contentList, comparator);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }


        return contentList;
    }

    public static void doForEachInContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, ContentHandler handler) throws SystemException {
        if (sort != null) {
            contentQuery.setSortOrder(sort);
        }

        // Query will be faster if we don't get all records
        contentQuery.setMaxRecords(maxElements);

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement st = contentQuery.getPreparedStatement(c);

            if (st == null) {
                return;
            }

            Map<Integer, Integer> contentIds = new HashMap<Integer, Integer>();

            // Get content objects
            ResultSet rs = st.executeQuery();
            int count = 0;
            while (rs.next() && (maxElements == -1 || count < maxElements + contentQuery.getOffset())) {
                Content content = ContentAOHelper.getContentFromRS(rs, true);
                if (contentIds.get(content.getId()) == null) {
                    // Only get if not duplicate (join may cause duplicate)
                    if (count >= contentQuery.getOffset()) {
                        contentIds.put(content.getId(), content.getId());
                        handler.handleContent(content);
                    }
                    count++;
                }
            }

            rs.close();

        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }


    public static ContentIdentifier getParent(ContentIdentifier cid) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            int id = SQLHelper.getInt(c, "select ParentAssociationId from associations where AssociationId = " + cid.getAssociationId() , "ParentAssociationId");

            ContentIdentifier parentCid = new ContentIdentifier();
            parentCid.setAssociationId(id);
            parentCid.setLanguage(cid.getLanguage());
            return parentCid;
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }
    }


    public static Content checkInContent(Content content, int newStatus) throws SystemException {

        Connection c = null;
        Content oldContent = null ;
        if(content.getId() != -1) {
            ContentIdentifier oldCid = new ContentIdentifier();
            oldCid.setAssociationId(content.getAssociation().getAssociationId());
            oldContent = getContent(oldCid, true);
        }

        try {
            c = dbConnectionFactory.getConnection();
            // Insert base information, no history
            boolean isNew = false;
            int isActiveVersion = 0;

            PreparedStatement st = null;

            if (content.getId() == -1) {
                // New page, always active
                isNew = true;
                isActiveVersion = 1;
            }

            if (isNew) {
                st = c.prepareStatement("insert into content (Type, ContentTemplateId, MetadataTemplateId, DisplayTemplateId, DocumentTypeId, GroupId, Owner, OwnerPerson, Location, Alias, PublishDate, ExpireDate, RevisionDate, ExpireAction, VisibilityStatus, ForumId, NumberOfNotes, OpenInNewWindow, DocumentTypeIdForChildren, IsLocked, RatingScore, NumberOfRatings, IsSearchable) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,?,?,?,0,0,?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                // Update
                st = c.prepareStatement("update content set Type = ?, ContentTemplateId = ?, MetaDataTemplateId = ?, DisplayTemplateId = ?, DocumentTypeId = ?, GroupId = ?, Owner = ?, OwnerPerson=?, Location = ?, Alias = ?, PublishDate = ?, ExpireDate = ?, RevisionDate=?, ExpireAction = ?, VisibilityStatus = ?, ForumId=?, OpenInNewWindow=?, DocumentTypeIdForChildren = ?, IsLocked = ?, IsSearchable = ? where ContentId = ?");
            }

            int p = 1;
            st.setInt(p++, content.getType().getTypeAsInt());
            st.setInt(p++, content.getContentTemplateId());
            st.setInt(p++, content.getMetaDataTemplateId());
            st.setInt(p++, content.getDisplayTemplateId());
            st.setInt(p++, content.getDocumentTypeId());
            st.setInt(p++, content.getGroupId());
            st.setString(p++, content.getOwner());
            st.setString(p++, content.getOwnerPerson());
            st.setString(p++, content.getLocation());
            st.setString(p++, content.getAlias());
            st.setTimestamp(p++, content.getPublishDate() == null ? null : new java.sql.Timestamp(content.getPublishDate().getTime()));
            st.setTimestamp(p++, content.getExpireDate() == null ? null : new java.sql.Timestamp(content.getExpireDate().getTime()));
            st.setTimestamp(p++, content.getRevisionDate() == null ? null : new java.sql.Timestamp(content.getRevisionDate().getTime()));
            st.setInt(p++, content.getExpireAction());
            st.setInt(p++, content.getVisibilityStatus());
            st.setLong(p++, content.getForumId());
            st.setInt(p++, content.isOpenInNewWindow() ? 1:0);
            st.setInt(p++, content.getDocumentTypeIdForChildren());
            st.setInt(p++, content.isLocked() ? 1:0);
            st.setInt(p++, content.isSearchable() ? 1:0);
            if (content.getId() != -1) {
                st.setInt(p++, content.getId());
            }

            st.execute();

            if (isNew) {
                // Finn id til nytt objekt
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    content.setId(rs.getInt(1));
                }
                rs.close();
            }
            st.close();

            if (isNew) {
                // GroupId benyttes for å angi at en side arver egenskaper, f.eks meny, design fra en annen side
                if (content.getGroupId() <= 0) {
                    st = c.prepareStatement("update content set GroupId = ? where ContentId = ?");
                    st.setInt(1, content.getId());
                    st.setInt(2, content.getId());
                    st.execute();
                    st.close();
                }
            }

            // If this is a draft, rejected page etc delete previous version
            if (content.getStatus() == ContentStatus.DRAFT || content.getStatus() == ContentStatus.WAITING_FOR_APPROVAL || content.getStatus() == ContentStatus.REJECTED) {
                // Delete this (previous) version
                ContentIdentifier cid = new ContentIdentifier();
                cid.setAssociationId(content.getAssociation().getId());
                cid.setVersion(content.getVersion());
                cid.setLanguage(content.getLanguage());
                deleteContentVersion(cid, true);
            }


            // Update subpages if these fields are changed
            if(oldContent != null ) {
                if (!oldContent.getOwner().equals(content.getOwner())) {
                    updateChildren(c, content.getAssociation().getId(), "owner", content.getOwner(), oldContent.getOwner());
                }
                if (!oldContent.getOwnerPerson().equals(content.getOwnerPerson())) {
                    updateChildren(c, content.getAssociation().getId(), "ownerperson", content.getOwnerPerson(), oldContent.getOwnerPerson());
                }
                if (oldContent.getGroupId() != content.getGroupId()) {
                    updateChildren(c, content.getAssociation().getId(), "GroupId", "" + content.getGroupId(), "" + oldContent.getGroupId());
                }
            }

            if (content.getId() == -1 || newStatus == ContentStatus.PUBLISHED) {
                isActiveVersion = 1;
            }

            // Find versionnumber etc
            if (!isNew) {
                // Find next version
                int currentVersion = SQLHelper.getInt(c, "select version from contentversion where ContentId = " + content.getId() + " order by version desc", "version");
                if (currentVersion > 0) {
                    content.setVersion(currentVersion + 1);
                }

                if (newStatus == ContentStatus.PUBLISHED) {
                    // Set newStatus = ARCHIVED on currently active version
                    PreparedStatement tmp = c.prepareStatement("update contentversion set Status = ?, isActive = 0 where ContentId = ? and isActive = 1");
                    tmp.setInt(1, ContentStatus.ARCHIVED);
                    tmp.setInt(2, content.getId());
                    tmp.execute();
                    tmp.close();
                    tmp = null;

                    // Publisert blir aktiv versjon
                    isActiveVersion = 1;
                }
            }

            // Insert new version
            st = c.prepareStatement("insert into contentversion (ContentId, Version, Status, IsActive, Language, Title, AltTitle, Description, Image, Keywords, Publisher, LastModified, LastModifiedBy, ChangeDescription, ApprovedBy, ChangeFrom) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, content.getId());
            st.setInt(2, content.getVersion());
            st.setInt(3, newStatus);
            st.setInt(4, isActiveVersion);
            st.setInt(5, content.getLanguage());
            st.setString(6, content.getTitle());
            st.setString(7, content.getAltTitle());
            st.setString(8, content.getDescription());
            st.setString(9, content.getImage());
            st.setString(10, content.getKeywords());
            st.setString(11, content.getPublisher());
            st.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
            st.setString(13, content.getModifiedBy());
            st.setString(14, content.getChangeDescription());
            st.setString(15, content.getApprovedBy());
            st.setTimestamp(16, content.getChangeFromDate() == null ? null : new java.sql.Timestamp(content.getChangeFromDate().getTime()));

            st.execute();

            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                content.setVersionId(rs.getInt(1));
            }
            rs.close();
            st.close();

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


            // Add page attributes
            insertAttributes(c, content, AttributeDataType.CONTENT_DATA);
            insertAttributes(c, content, AttributeDataType.META_DATA);

            // Insert associations if new
            List associations = content.getAssociations();
            for (int i = 0; i < associations.size(); i++) {
                Association association = (Association)associations.get(i);
                if (association.getId() == -1) {
                    // New association: add
                    association.setContentId(content.getId());
                    AssociationAO.addAssociation(association);
                }
            }

            // Update contentid on attachments saved in database before the page was saved
            List attachments = content.getAttachments();
            if (attachments != null) {
                for (int i = 0; i < attachments.size(); i++) {
                    Attachment a = (Attachment)attachments.get(i);
                    a.setContentId(content.getId());
                    AttachmentAO.setAttachment(a);
                }
            }

            // Insert topics
            List<Topic> topics = content.getTopics();
            if (topics != null) {
                for (Topic t : topics) {
                    TopicAO.addTopicContentAssociation(t, content.getId());
                }
            }

            // Set page as not checked out
            content.setIsCheckedOut(false);

            // Mark as not modified
            content.setIsModified(false);


        } catch (SQLException e) {
            throw new SystemException("Feil ved lagring", SOURCE, e);
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

    /**
     * Updated field on subpages
     * @param c
     * @param associationId
     * @param field
     * @param newValue
     * @param oldValue
     * @throws SQLException
     */
    private static void updateChildren(Connection c, int associationId, String field, String newValue, String oldValue) throws SQLException {

        String query = "select content.contentid from content, associations where content." + field + " = ? and associations.path like ? and content.ContentId=associations.ContentId";
        PreparedStatement st = c.prepareStatement(query);
        st.setString(1, oldValue);
        st.setString(2, "%/" +associationId +"/%");
        ResultSet rs = st.executeQuery();

        StringBuffer whereClause = new StringBuffer();
        while(rs.next()) {
            whereClause.append(String.valueOf(rs.getInt("contentid")));
            if(!rs.isLast()) {
                whereClause.append(",");
            }
        }
        st.close();

        if (whereClause.length() > 0) {
            query = "update content set " + field + " = ? where ContentId in (" + whereClause + ")";
            PreparedStatement cp = c.prepareStatement(query);
            cp.setString(1, newValue);
            int i = cp.executeUpdate();
            cp.close();
        }
    }

    /**
     * Delete old contentversions
     * @param content
     * @param maxVersions
     * @throws SystemException
     */
    private static void deleteOldContentVersions(Content content, int maxVersions) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("select * from contentversion where ContentId = ? and Status <> ? order by Version desc");
            st.setInt(1, content.getId());
            st.setInt(2, ContentStatus.PUBLISHED);
            ResultSet rs = st.executeQuery();
            int noVersions = 0;
            while(rs.next()) {
                int ver = rs.getInt("Version");
                noVersions++;
                if (noVersions > maxVersions ) {
                    // Slett denne versjonen
                    ContentIdentifier cid = new ContentIdentifier();
                    cid.setAssociationId(content.getAssociation().getId());
                    cid.setVersion(ver);
                    cid.setLanguage(content.getLanguage());

                    deleteContentVersion(cid, false);
                }
            }

            st.close();

        } catch (SQLException e) {
            throw new SystemException("Feil ved lagring", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                // Could not close connection, probably closed already
            }
        }
    }

    public static Content setContentStatus(ContentIdentifier cid, int newStatus, Date newPublishDate, String userId) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            int version = SQLHelper.getInt(c, "select Version from contentversion where status = " + ContentStatus.WAITING_FOR_APPROVAL + " order by version desc", "Version");

            if (version != -1) {
                if (newStatus == ContentStatus.PUBLISHED) {
                    // Sett status = arkivert på aktiv versjon
                    PreparedStatement tmp = c.prepareStatement("update contentversion set status = ?, isActive = 0 where ContentId = ? and isActive = 1");
                    tmp.setInt(1, ContentStatus.ARCHIVED);
                    tmp.setInt(2, cid.getContentId());
                    tmp.execute();
                    tmp.close();

                    tmp = c.prepareStatement("update contentversion set status = ?, isActive = 1, ApprovedBy = ? where ContentId = ? and Version = ?");
                    tmp.setInt(1, ContentStatus.PUBLISHED);
                    tmp.setString(2, userId);
                    tmp.setInt(3, cid.getContentId());
                    tmp.setInt(4, version);
                    tmp.execute();
                    tmp.close();
                    tmp = null;

                    if (newPublishDate != null) {
                        // Set publish date if not set
                        tmp = c.prepareStatement("update content set PublishDate = ? where ContentId = ?");
                        tmp.setTimestamp(1, new java.sql.Timestamp(newPublishDate.getTime()));
                        tmp.setInt(2, cid.getContentId());
                        tmp.execute();
                        tmp.close();
                        tmp = null;
                    }
                } else {
                    PreparedStatement tmp = c.prepareStatement("update contentversion set status = ? where ContentId = ? and Version = ?");
                    tmp.setInt(1, newStatus);
                    tmp.setInt(2, cid.getContentId());
                    tmp.setInt(3, cid.getVersion());
                    tmp.execute();
                    tmp.close();
                    tmp = null;
                }
            }

        } catch (SQLException e) {
            throw new SystemException("Feil ved lagring", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                // Could not close connection, probably closed already
            }
        }

        Content content = ContentAO.getContent(cid, false);

        content.setStatus(newStatus);

        return content;
    }

    private static void insertAttributes(Connection c, Content content, int type) throws SQLException, SystemException {
        // Get all attributes and save them
        List attributes = content.getAttributes(type);
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attr = (Attribute)attributes.get(i);
                PersistAttributeBehaviour attributeSaver = attr.getSaveBehaviour();
                attributeSaver.persistAttribute(c, content, attr);
            }
        }
    }

    public static int getNextExpiredContentId(int after) throws SystemException {

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
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
            throw new SystemException("SQL exception: " +e.getMessage(), SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }


    /**
     * Return the id of the next content id which should be activated
     * - because publish date was reached (on a new page or existing page)
     * - because changefrom date was reached (on a existing page)
     * @param after - which content id to start at
     * @return
     * @throws SystemException
     */
    public static int getNextActivationContentId(int after) throws SystemException {

        Connection c = null;
        try {
            long now = new Date().getTime() + 1000*60*1;
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT ContentId FROM content WHERE (PublishDate < ? AND VisibilityStatus = ?) OR (ContentId IN (SELECT ContentId FROM contentversion WHERE Status = ? AND ChangeFrom < ?)) AND ContentId > ? ORDER BY ContentId");
            p.setTimestamp(1, new Timestamp(now));
            p.setInt(2, ContentVisibilityStatus.WAITING);
            p.setTimestamp(3, new Timestamp(now));
            p.setInt(4, ContentStatus.PUBLISHED_WAITING);
            p.setInt(5, after);
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("ContentId");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }


    public static void setContentVisibilityStatus(int contentId, int newStatus) throws SystemException {

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement tmp = c.prepareStatement("update content set VisibilityStatus = ? where ContentId = ?");
            tmp.setInt(1, newStatus);
            tmp.setInt(2, contentId);
            tmp.execute();
            tmp.close();

        } catch (SQLException e) {
            try {
                c.close();
            } catch (SQLException e1) {
                // Could not close connection, probably closed already
            }
            throw new SystemException("Feil ved setting av visningsstatus", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                // Could not close connection, probably closed already
            }
        }
    }

    public static void setNumberOfNotes(int contentId, int count) throws SystemException {

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("UPDATE content SET NumberOfNotes = ? WHERE ContentId = ?");
            p.setInt(1, count);
            p.setInt(2, contentId);
            p.executeUpdate();

        } catch (SQLException e) {
            throw new SystemException("Feil ved setting av NumberOfNotes", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }

    public static List getNoChangesPerUser(int months) throws SystemException {
        List ucclist = new ArrayList();
        Connection c = null;

        try {

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MONTH, -months);

            c = dbConnectionFactory.getConnection();
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
            throw new SystemException("SQL error",SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }


    public static Map getContentIdentifierCacheValues() throws SystemException {
        Map aliases = new HashMap();
        Connection c = null;

        try {

            c = dbConnectionFactory.getConnection();
            String driver = dbConnectionFactory.getDriverName();

            String where = "";
            if (driver.indexOf("oracle") != -1) {
                where = " content.Alias is not null and associations.Type = " + AssociationType.DEFAULT_POSTING_FOR_SITE;
            } else {
                where = " content.Alias is not null and content.Alias <> '' and associations.Type = " + AssociationType.DEFAULT_POSTING_FOR_SITE;
            }

            PreparedStatement p = c.prepareStatement("select associations.AssociationId, associations.SiteId, content.ContentId, content.Alias from associations, content where " + where + " and content.ContentId = associations.ContentId and (associations.IsDeleted = 0 or associations.IsDeleted is null)");
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                int associationId = rs.getInt("AssociationId");
                int siteId = rs.getInt("SiteId");
                int contentId = rs.getInt("ContentId");
                String alias = rs.getString("alias");

                List cids = (List)aliases.get(alias);

                if (cids == null) {
                    // Alias ligger ikke i cache
                    cids = new ArrayList();
                    aliases.put(alias, cids);
                }

                ContentIdentifier cid = new ContentIdentifier();
                cid.setAssociationId(associationId);
                cid.setContentId(contentId);
                cid.setSiteId(siteId);
                cids.add(cid);                
            }
            return aliases;
        } catch (SQLException e) {
            throw new SystemException("SQL error",SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public static int getContentCount() throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT COUNT(*) AS count FROM content WHERE VisibilityStatus = ? AND Type = ?");
            p.setInt(1, ContentVisibilityStatus.ACTIVE);
            p.setInt(2, ContentType.PAGE.getTypeAsInt());
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }

    public static int getLinkCount() throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT COUNT(*) AS count FROM content WHERE VisibilityStatus = ? AND Type = ?");
            p.setInt(1, ContentVisibilityStatus.ACTIVE);
            p.setInt(2, ContentType.LINK.getTypeAsInt());
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }

    public static int getContentProducerCount() throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT COUNT(DISTINCT LastModifiedBy) AS count FROM contentversion");
            ResultSet rs = p.executeQuery();
            if(!rs.next()) {
                return -1;
            } else {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SystemException("SQL exception: " +e.getMessage(), SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }

    public static void updateContentFromTemplates(TemplateConfiguration templateConfiguration) {
        for (DisplayTemplate dt : templateConfiguration.getDisplayTemplates()) {
            int contentTemplateId = dt.getContentTemplate().getId();
            int metadataTemplateId = -1;
            if (dt.getMetaDataTemplate() != null) {
                metadataTemplateId = dt.getMetaDataTemplate().getId();
            }

            // Update database with correct value for ContentTemplateId and MetadataTemplateId
            JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
            template.update("update content set ContentTemplateId = ? where DisplayTemplateId = ? and ContentTemplateId <> ?", new Object[] { contentTemplateId, dt.getId(), contentTemplateId});
            template.update("update content set MetaDataTemplateId = ? where DisplayTemplateId = ? and MetaDataTemplateId <> ?", new Object[] { metadataTemplateId, dt.getId(), metadataTemplateId});
        }

        for (ContentTemplate ct : templateConfiguration.getContentTemplates()) {
            // Update database with correct value for type
            JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
            template.update("update content set Type = ? where ContentTemplateId = ? and Type <> ?", new Object[] { ct.getContentType().getTypeAsInt(), ct.getId(), ct.getContentType().getTypeAsInt() });
        }

        /*

        TODO: Should documenttype be changed?

         */
    }

    /**
     * Check if page is published or has been published
     * @param contentId - ContentId
     * @return
     */
    public static boolean hasBeenPublished(int contentId) {
        JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
        int cnt = template.queryForInt("select count(*) from contentversion where ContentId = ? and status IN (?,?)", new Object[] {contentId, ContentStatus.PUBLISHED, ContentStatus.ARCHIVED});
        return cnt > 0;
    }

    /**
     * Set new rating score (average rating) for content
     * @param contentId - ContentId
     * @param score - score
     * @param numberOfRatings - numberOfRatings
     */
    public static void setRating(int contentId, float score, int numberOfRatings) {
        JdbcTemplate template = dbConnectionFactory.getJdbcTemplate();
        template.update("update content set RatingScore = ?, NumberOfRatings = ? where ContentId = ?", new Object[] {score, numberOfRatings, contentId});
    }
    
    
    /**
     * Updates publish date and expire date on a content object and all child objects
     * @param cid - ContentIdentifier to content object
     * @param publishDate - new publish date
     * @param expireDate - new expire date
     * @param updateChildren - true = update children / false = dont update children
     */
    public static void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren) {
        int contentId = cid.getContentId();
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
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
            throw new SystemException("SQL error",SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }


    public interface ContentHandlerStopper {
        public boolean isStopRequested();
    }
}
