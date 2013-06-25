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

package no.kantega.publishing.common.data;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ContentQuery {
    private static final Logger log = LoggerFactory.getLogger(ContentQuery.class);
    private static final String SOURCE = "aksess.ContentQuery";

    private ContentIdentifier associatedId = null;
    private AssociationCategory associationCategory = null;
    private ContentIdentifier[] contentList = null;
    private Date modifiedDate = null;
    private Date publishDateFrom = null;
    private Date publishDateTo = null;
    private Date expireDateFrom = null;
    private Date expireDateTo = null;
    private Date revisionDateFrom = null;
    private Date revisionDateTo = null;

    private ContentIdentifier[] pathElementIds = null; // include only pages under given id in path
    private ContentIdentifier[] excludedPathElementIds = null; // exclude pages under given id in path

    private int[] contentTemplate = null;
    private ContentType contentType = null;
    private int[] documentType = null;
    private int[] excludedDocumentTypes = null;
    private int[] displayTemplate = null;
    private int language = -1;
    private int siteId = -1;
    private String keyword = null;
    private String owner   = null;
    private String ownerPerson = null;
    private List<Topic> topics = null;
    private int topicMapId = -1;
    private Topic topicType = null;
    private String sql = null;
    private boolean showArchived = false;
    private boolean showExpired = false;
    private String onHearingFor = null;
    private SortOrder sortOrder = null;
    private boolean includeDrafts = false;
    private boolean includeWaitingForApproval = false;
    private int[] excludedAssociationTypes = null;

    private int maxRecords = -1;
    private int offset = 0;

    private List<Attribute> attributes  = null;

    private boolean intersectingTopics = false;

    public ContentQuery() {
    }

    public PreparedStatement getPreparedStatement(Connection c) throws SQLException {

        QueryWithParameters qp = getQueryWithParameters();
        //QueryWithParameters is null when there is no content found 
        if (qp == null){
            return null;
        }
        String query = qp.getQuery();
        List<Object> parameters= qp.getParams();

        PreparedStatement st = c.prepareStatement(query);
        for (int i = 0; i < parameters.size(); i++) {
            Object o = parameters.get(i);
            if (o instanceof Date) {
                Date d = (Date)o;
                st.setTimestamp(i + 1, new java.sql.Timestamp(d.getTime()));
            } else {
                st.setObject(i + 1, o);
            }
        }

        return st;

    }

    public QueryWithParameters getQueryWithParameters() {
        boolean useSqlSort = useSqlSort();

        List parameters = new ArrayList();

        StringBuilder query = new StringBuilder();

        String driver = dbConnectionFactory.getDriverName();

        List<String> joinTables = new ArrayList<String>();
        joinTables.add("content");
        joinTables.add("contentversion");
        joinTables.add("associations");
        if (topics != null && topics.size() > 0 && !intersectingTopics) {
            joinTables.add("ct2topic");
        }

        if (maxRecords != -1 && useSqlSort && driver.contains("jtds") && joinTables.size() == 0) {
            // Only limit if not using join
            query.append("select top ").append(maxRecords + offset);
        } else {
            query.append("select");
        }

        query.append(" content.*, contentversion.*, associations.* from ");

        for (int i = 0; i < joinTables.size(); i++) {
            String table = joinTables.get(i);
            if (i > 0) {
                query.append(",");
            }
            query.append(table);
        }


        query.append(" where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) ");
        query.append("and contentversion.Status IN (");
        query.append(ContentStatus.PUBLISHED.getTypeAsInt());
        if(onHearingFor != null) {
            query.append(",").append(ContentStatus.HEARING.getTypeAsInt());
        }
        if (includeDrafts) {
            query.append(",").append(ContentStatus.DRAFT.getTypeAsInt());
        }
        if(includeWaitingForApproval){
            query.append(",").append(ContentStatus.WAITING_FOR_APPROVAL.getTypeAsInt());
        }

        query.append(") and content.ContentId = associations.ContentId");

        if (associatedId != null) {
            if (associatedId.getAssociationId() != -1) {
                query.append(" and associations.ParentAssociationId = ?");
                parameters.add(associatedId.getAssociationId());
            }
            if (associatedId.getSiteId() != -1) {
                query.append(" and associations.SiteId = ?");
                parameters.add(associatedId.getSiteId());
            }

            if (associationCategory != null && associationCategory.getId() != -1) {
                // Fetch only those in given category/column
                query.append(" and associations.Category = ?");
                parameters.add(associationCategory.getId());
            }
        } else if (contentList != null && contentList.length > 0) {
            query.append(" and associations.UniqueId in (");
            for (int i=0; i < contentList.length; i++){
                if (contentList[i] != null) {
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append("?");
                    parameters.add(contentList[i].getAssociationId());
                }
            }
            query.append(")");
            if (excludedAssociationTypes == null) {
                excludedAssociationTypes = new int[]{ AssociationType.SHORTCUT };
            }
        } else if (pathElementIds != null) {
            for (int i = 0; i < pathElementIds.length; i++) {
                if (i == 0) {
                    query.append(" and (associations.Path like ?");
                } else {
                    query.append(" or associations.Path like ?");
                }
                parameters.add("%/" + pathElementIds[i].getAssociationId() + "/%");
                if (i == (pathElementIds.length-1)) {
                    query.append(")");
                }
            }
            if (associationCategory != null && associationCategory.getId() != -1) {
                // Fetch only those in given category/column
                query.append(" and associations.Category = ?");
                parameters.add(associationCategory.getId());
            }
        } else if (excludedPathElementIds != null) {
            for (int i=0; i<excludedPathElementIds.length; i++){
                if (excludedPathElementIds[i]!=null) {
                    // exclude all published under given id
                    query.append(" and associations.Path not like ? and associations.AssociationId <> ? ");
                    parameters.add("/%" + excludedPathElementIds[i].getAssociationId() + "/%");
                    parameters.add(excludedPathElementIds[i].getAssociationId());
                }
            }
        } else if (sql != null) {
            String s = sql.trim();
            if (!s.startsWith("and") && !s.startsWith("or")) {
                query.append(" and ");
            }
            query.append(sql);
        } else if (attributes == null && excludedAssociationTypes == null) {
            excludedAssociationTypes = new int[]{AssociationType.CROSS_POSTING, AssociationType.SHORTCUT};
        }

        addExclusionOfAssociationTypes(query);

        if (siteId != -1) {
            query.append(" and associations.SiteId = ?");
            parameters.add(siteId);
        }

        if (language != -1) {
            query.append(" and contentversion.Language = ?");
            parameters.add(language);
        }

        if (contentType != null) {
            query.append(" and content.ContentType = ?");
            parameters.add(contentType.getTypeAsInt());
        }

        if (documentType != null && documentType.length > 0) {
            query.append(" and content.DocumentTypeId in (");
            for (int i = 0; i < documentType.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
                parameters.add(documentType[i]);
            }
            query.append(") ");
        }

        if (excludedDocumentTypes != null && excludedDocumentTypes.length > 0) {
            query.append(" and content.DocumentTypeId not in (");
            for (int i = 0; i < excludedDocumentTypes.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
                parameters.add(excludedDocumentTypes[i]);
            }
            query.append(") ");
        }


        if (contentTemplate != null && contentTemplate.length > 0) {
            query.append(" and content.ContentTemplateId in (");
            for (int i = 0; i < contentTemplate.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
                parameters.add(contentTemplate[i]);
            }
            query.append(") ");
        }

        if (displayTemplate != null && displayTemplate.length > 0) {
            query.append(" and content.DisplayTemplateId in (");
            for (int i = 0; i < displayTemplate.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
                parameters.add(displayTemplate[i]);
            }
            query.append(") ");
        }

        // Fetch pages with given publishdate / expiredate
        if (publishDateFrom != null) {
            query.append(" and content.PublishDate >= ?");
            parameters.add(publishDateFrom);
        }
        if (publishDateTo != null) {
            query.append(" and content.PublishDate <= ?");
            parameters.add(publishDateTo);
        }
        if (expireDateFrom != null) {
            query.append(" and content.ExpireDate >= ?");
            parameters.add(expireDateFrom);
            showExpired = true;
        }
        if (expireDateTo != null) {
            query.append(" and content.ExpireDate <= ?");
            parameters.add(expireDateTo);
            showExpired = true;
        }

        // Fetch archived pages if specified
        if (showExpired) {
            query.append(" and content.VisibilityStatus in (" + ContentVisibilityStatus.ACTIVE + ", " + ContentVisibilityStatus.ARCHIVED + ", " + ContentVisibilityStatus.EXPIRED + ")");
        } else if (showArchived) {
            query.append(" and content.VisibilityStatus in (" + ContentVisibilityStatus.ACTIVE + ", " + ContentVisibilityStatus.ARCHIVED + ")");
        } else {
            query.append(" and content.VisibilityStatus in (" + ContentVisibilityStatus.ACTIVE + ")");
        }

        if (owner != null) {
            query.append(" and content.Owner in (");
            String[] owners  = owner.split(",");
            for (int i = 0; i < owners.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                String o = owners[i];
                query.append("?");
                parameters.add(o);
            }
            query.append(") ");

        }
        if (ownerPerson != null) {
            query.append(" and content.OwnerPerson in (");
            String[] owners  = ownerPerson.split(",");
            for (int i = 0; i < owners.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                String o = owners[i];
                query.append("?");
                parameters.add(o);
            }
            query.append(") ");
        }

        if (keyword != null) {
            // Keyword given, search title and alternative title
            if (!keyword.contains("%")) {
                keyword = "%" + keyword + "%";
            }
            if ((driver.contains("oracle")) || (driver.contains("postgresql"))) {
                // Oracle and PostgreSQL is case sensitive
                keyword = keyword.toLowerCase();
                query.append(" and (lower(contentversion.Title) like ? or lower(contentversion.AltTitle) like ?)");
            } else {
                query.append(" and (contentversion.Title like ? or contentversion.AltTitle like ?)");
            }
            parameters.add(keyword);
            parameters.add(keyword);

        }
        if (modifiedDate != null) {
            query.append(" and contentversion.LastModified >= ?");
            parameters.add(modifiedDate);
        }
        if (revisionDateFrom != null) {
            query.append(" and content.RevisionDate is not null and content.RevisionDate >= ?");
            parameters.add(revisionDateFrom);
        }
        if (revisionDateTo != null) {
            query.append(" and content.RevisionDate is not null and content.RevisionDate <= ?");
            parameters.add(revisionDateTo);
        }

        if(topics != null && topics.size() > 0) {
            if (intersectingTopics) {
                // Cannot be done via join, potensially slow method
                if (driver.contains("mysql")) {
                    log.info( "Using query with intersectingTopics is slow on MySQL");
                }

                query.append(" and content.contentId in (select ct2topic.contentId from ct2topic where ");
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = (Topic)topics.get(i);
                    if (i > 0){
                        query.append(" and ct2topic.contentId in (select ct2topic.contentId from ct2topic where ");
                    }
                    query.append(" (TopicMapId = ? and TopicId = ?)");
                    parameters.add(topic.getTopicMapId());
                    parameters.add(topic.getId());
                    if (i > 0){
                        query.append(")");
                    }
                }
                query.append(") ");
            } else {
                // Done via join - quickest way
                query.append(" and content.ContentId = ct2topic.ContentId and (");
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = (Topic)topics.get(i);
                    if (i > 0) {
                        query.append(" or ");
                    }
                    query.append(" (TopicMapId = ? and TopicId = ?)");
                    parameters.add(topic.getTopicMapId());
                    parameters.add(topic.getId());
                }
                query.append(") ");
            }
        }

        if (topicMapId != -1) {
            query.append(" and content.contentId in (select ct2topic.contentId from ct2topic where TopicMapId = ?");
            parameters.add(topicMapId);
        }

        if (topicType != null) {
            query.append(" and content.contentId in (select contentId from ct2topic inner join tmtopic on ct2topic.topicId = tmtopic.topicId and ct2topic.topicMapId = tmtopic.topicMapId where ct2topic.topicMapId=? and tmtopic.InstanceOf=?)");
            parameters.add(topicType.getTopicMapId());
            parameters.add(topicType.getId());
        }

        if (onHearingFor != null) {
            query.append(" and content.contentid in (select contentversion.contentId from hearing, contentversion, hearinginvitee " +
                    " where  hearing.ContentversionId = contentversion.Contentversionid " +
                    " and hearinginvitee.HearingId = hearing.Hearingid " +
                    " and hearing.DeadLine > ?");
            parameters.add(new Date());

            if (!onHearingFor.equals("everyone")) {
                query.append(" and ((hearinginvitee.InviteeType = "+ HearingInvitee.TYPE_PERSON + " and hearinginvitee.InviteeRef = ?)");
                parameters.add(onHearingFor);

                Map managers = RootContext.getInstance().getBeansOfType(OrganizationManager.class);
                if (managers.size() > 0) {
                    OrganizationManager manager = (OrganizationManager) managers.values().iterator().next();
                    List orgUnits = manager.getOrgUnitsAboveUser(onHearingFor);
                    if(orgUnits.size() > 0) {
                        query.append(" or (hearinginvitee.InviteeType = " + HearingInvitee.TYPE_ORGUNIT +
                                "     and hearinginvitee.InviteeRef in (");

                        for (int i = 0; i < orgUnits.size(); i++) {
                            OrgUnit unit = (OrgUnit) orgUnits.get(i);
                            query.append("?");
                            parameters.add(unit.getExternalId());
                            if(i < orgUnits.size() -1) {
                                query.append(",");
                            }
                        }
                        query.append("))");
                    }
                }
                query.append(")");
            }
            query.append(")");
        }

        if (attributes != null) {
            try {
                // Må gjøres tungvint siden MySQL 4.0 ikke støtter subqueryes
                try (Connection c = dbConnectionFactory.getConnection()){
                    PreparedStatement st = c.prepareStatement("select cv.ContentId from contentversion cv, contentattributes ca where cv.IsActive = 1 and cv.ContentVersionId = ca.ContentVersionId and ca.name = ? and ca.value like ?");
                    for (Attribute a : attributes) {
                        st.setString(1, a.getName());
                        st.setString(2, a.getValue());

                        int noFound = 0;
                        ResultSet rs = st.executeQuery();
                        query.append(" and content.ContentId in (");
                        while (rs.next()) {
                            if (noFound > 0) {
                                query.append(",");
                            }
                            int id = rs.getInt("ContentId");
                            query.append("?");
                            parameters.add(id);
                            noFound++;
                        }
                        query.append(")");
                        if (noFound == 0) {
                            return null;
                        }
                    }
                    st.close();
                }
            } catch (SQLException e) {
                log.error("Error when getting contentid", e);
            }
        }

        if (useSqlSort) {
            query.append(sortOrder.getSqlSort());
        } else {
            query.append(" order by ContentVersionId ");
        }

        if (maxRecords != -1 && useSqlSort && driver.contains("mysql") && joinTables.size() == 0) {
            // Only limit if not using join
            query.append(" limit ").append(maxRecords + offset);
        }

        return new QueryWithParameters(query.toString(), parameters);
    }

    private void addExclusionOfAssociationTypes(StringBuilder query) {
        if(excludedAssociationTypes != null && excludedAssociationTypes.length > 0){
            query.append(" and associations.Type not in (");
            for (int i = 0, excludedAssociationTypesLength = excludedAssociationTypes.length; i < excludedAssociationTypesLength; i++) {
                Integer associationType = excludedAssociationTypes[i];
                query.append(associationType);
                if(i < excludedAssociationTypes.length - 1){
                    query.append(",");
                }
            }
            query.append(")");
        }
    }


    //  Setter methods only
    public void setAssociatedId(ContentIdentifier cid) {
        this.associatedId = cid;
    }


    public void setContentList(String contentList) {
        String cids[] = contentList.split(",");
        this.contentList = new ContentIdentifier[cids.length];
        for (int i = 0; i < cids.length; i++) {
            String tmp = cids[i].trim();
            int id = -1;
            try {
                id = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {

            }
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(id);
            this.contentList[i] = cid;
        }
    }

    public void setContentList(ContentIdentifier[] contentList) {
        this.contentList = contentList;
    }

    public ContentIdentifier[] getContentList() {
        return contentList;
    }

    public void setAssociationCategory(AssociationCategory associationCategory) {
        this.associationCategory = associationCategory;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setPublishDateFrom(Date publishDateFrom) {
        this.publishDateFrom = publishDateFrom;
    }

    public void setPublishDateTo(Date publishDateTo) {
        this.publishDateTo = publishDateTo;
    }

    public void setRevisionDateFrom(Date revisionDateFrom) {
        this.revisionDateFrom = revisionDateFrom;
    }

    public void setRevisionDateTo(Date revisionDateTo) {
        this.revisionDateTo = revisionDateTo;
    }

    public void setContentTemplate(int contentTemplate) {
        this.contentTemplate = new int[1];
        this.contentTemplate[0] = contentTemplate;
    }

    public void setContentTemplate(String contentTemplate) throws SystemException {
        String templates[] = contentTemplate.split("\\|");
        this.contentTemplate = new int[templates.length];
        for (int i = 0; i < templates.length; i++) {
            String template = templates[i].trim();
            int id = -1;
            try {
                id = Integer.parseInt(template);
            } catch (NumberFormatException e) {
                ContentTemplate ct = ContentTemplateCache.getTemplateByPublicId(template);
                if (ct != null) {
                    id = ct.getId();
                } else {
                    log.info( "Reference to contenttemplate which does not exists:" + template);
                }
            }
            this.contentTemplate[i] = id;
        }
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public boolean useSqlSort() {
        return sortOrder != null && sortOrder.getSqlSort() != null;
    }

    public void setContentType(String contentType) throws SystemException {
        if (contentType.equalsIgnoreCase("page")) {
            this.contentType = ContentType.PAGE;
        } else if (contentType.equalsIgnoreCase("link")) {
            this.contentType = ContentType.LINK;
        } else if (contentType.equalsIgnoreCase("file")) {
            this.contentType = ContentType.FILE;
        } else if (contentType.equalsIgnoreCase("form")) {
            this.contentType = ContentType.FORM;
        }
    }

    public void setDocumentType(int documentType) {
        this.documentType = new int[1];
        this.documentType[0] = documentType;
    }

    public void setExcludedDocumentTypes(String documentType) throws SystemException {
        this.excludedDocumentTypes = getDocumentTypesFromString(documentType);
    }

    public void setDocumentType(String documentType) throws SystemException {
        this.documentType = getDocumentTypesFromString(documentType);
    }

    private int[] getDocumentTypesFromString(String documentType) {
        String doctypes[] = documentType.split("\\|");
        int docTypeIds[] = new int[doctypes.length];
        for (int i = 0; i < doctypes.length; i++) {
            String doctype = doctypes[i].trim();
            int id = -1;
            try {
                id = Integer.parseInt(doctype);
            } catch (NumberFormatException e) {
                DocumentType dt = DocumentTypeCache.getDocumentTypeByPublicId(doctype);
                if (dt != null) {
                    id = dt.getId();
                }
            }
            docTypeIds[i] = id;
        }
        return docTypeIds;
    }

    public void setDisplayTemplate(int displayTemplate) {
        this.displayTemplate = new int[1];
        this.displayTemplate[0] = displayTemplate;
    }

    public void setDisplayTemplate(String displayTemplate) throws SystemException {
        String templates[] = displayTemplate.split("\\|");
        this.displayTemplate = new int[templates.length];
        for (int i = 0; i < templates.length; i++) {
            String template = templates[i].trim();
            int id = -1;
            try {
                id = Integer.parseInt(template);
            } catch (NumberFormatException e) {
                DisplayTemplate dt = DisplayTemplateCache.getTemplateByPublicId(template);
                if (dt != null) {
                    id = dt.getId();
                } else {
                    log.info( "Reference to displaytemplate which does not exists:" + template);
                }
            }
            this.displayTemplate[i] = id;
        }
    }

    /**
     * @param keyword - to look for in title and alttitle.
     */
    public void setKeyword(String keyword) {
        if (keyword != null) {
            keyword = keyword.replaceAll("\\*", "%");
        }

        this.keyword = keyword;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void addAttribute(String name, String value) {
        Attribute a = new TextAttribute();
        a.setName(name);
        a.setValue(value);
        if (attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        attributes.add(a);
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public void setTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<Topic>();
        }
        topics.add(topic);
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void setShowArchived(boolean showArchived) {
        this.showArchived = showArchived;
    }

    public void setShowExpired(boolean showExpired) {
        this.showExpired = showExpired;
    }

    public void setPathElementId(ContentIdentifier pathElement) {
        if(this.pathElementIds == null) {
            this.pathElementIds = new ContentIdentifier[1];
        }
        this.pathElementIds[0] = pathElement;
    }

    public void setPathElementIds(ContentIdentifier[] pathElementIds) {
        this.pathElementIds = pathElementIds;
    }

    public void setExpireDateFrom(Date expireDateFrom) {
        this.expireDateFrom = expireDateFrom;
    }

    public void setExpireDateTo(Date expireDateTo) {
        this.expireDateTo = expireDateTo;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setTopicMapId(int topicMapId) {
        this.topicMapId = topicMapId;
    }

    public void setTopicType(Topic topicType) {
        this.topicType = topicType;
    }

    public void setOwnerPerson(String ownerPerson) {
        this.ownerPerson = ownerPerson;
    }

    public String getOnHearingFor() {
        return onHearingFor;
    }

    public void setOnHearingFor(String onHearingFor) {
        this.onHearingFor = onHearingFor;
    }

    public ContentIdentifier[] getExcludedPathElementIds() {
        return excludedPathElementIds;
    }

    public void setExcludedPathElementIds(ContentIdentifier[] excludedPathElementIds) {
        this.excludedPathElementIds = excludedPathElementIds;
    }

    public void setIntersectingTopics(boolean intersectingTopics) {
        this.intersectingTopics = intersectingTopics;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isIncludeDrafts() {
        return includeDrafts;
    }

    public void setIncludeDrafts(boolean includeDrafts) {
        this.includeDrafts = includeDrafts;
    }

    public boolean isIncludeWaitingForApproval() {
        return includeWaitingForApproval;
    }

    public void setIncludeWaitingForApproval(boolean includeWaitingForApproval) {
        this.includeWaitingForApproval = includeWaitingForApproval;
    }

    public void setExcludedAssociationTypes(int[] excludedAssociationTypes){
        this.excludedAssociationTypes = excludedAssociationTypes;
    }

    /**
     * Class representing an instance of a query, that is the query string and the parameters to be set.
     */
    public class QueryWithParameters {
        private final String query;
        private final List<Object> params;

        QueryWithParameters(String query, List<Object> params) {
            this.query = query;
            this.params = params;
        }

        public String getQuery() {
            return query;
        }

        public List<Object> getParams() {
            return params;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QueryWithParameters that = (QueryWithParameters) o;

            if (!params.equals(that.params)) return false;
            if (!query.equals(that.query)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = query.hashCode();
            result = 31 * result + params.hashCode();
            return result;
        }
    }
}
