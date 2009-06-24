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

import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.spring.RootContext;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ContentQuery {
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
    private int[] displayTemplate = null;
    private int language = -1;
    private int siteId = -1;
    private String keyword = null;
    private String owner   = null;
    private String ownerPerson = null;
    private List topics = null;
    private int topicMapId = -1;
    private String sql = null;
    private boolean showArchived = false;
    private boolean showExpired = false;
    private String onHearingFor = null;
    private SortOrder sortOrder = null;

    private int maxRecords = -1;

    private List attributes  = null;

    private boolean intersectingTopics = false;

    public ContentQuery() {
    }

    public PreparedStatement getPreparedStatement(Connection c) throws SQLException {
        boolean useSqlSort = useSqlSort();

        List parameters = new ArrayList();

        StringBuffer query = new StringBuffer();

        String driver = dbConnectionFactory.getDriverName();

        List joinTables = new ArrayList();
        joinTables.add("content");
        joinTables.add("contentversion");
        joinTables.add("associations");
        if (topics != null && topics.size() > 0 && !intersectingTopics) {
            joinTables.add("ct2topic");
        }

        if (maxRecords != -1 && useSqlSort && driver.indexOf("jtds") != -1 && joinTables.size() == 0) {
            // Only limit if not using join
            query.append("select top ").append(maxRecords);
        } else {
            query.append("select");
        }

        query.append(" content.*, contentversion.*, associations.* from ");

        for (int i = 0; i < joinTables.size(); i++) {
            String table = (String) joinTables.get(i);
            if (i > 0) {
                query.append(",");
            }
            query.append(table);
        }


        query.append(" where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) ");
        query.append("and contentversion.Status IN (");
        if(onHearingFor != null) {
            query.append(ContentStatus.PUBLISHED + "," + ContentStatus.HEARING);
        } else {
            query.append(ContentStatus.PUBLISHED);
        }

        query.append(") and content.ContentId = associations.ContentId");

        if (associatedId != null) {
            if (associatedId.getAssociationId() != -1) {
                query.append(" and associations.ParentAssociationId = ?");
                parameters.add(new Integer(associatedId.getAssociationId()));
            }
            if (associatedId.getSiteId() != -1) {
                query.append(" and associations.SiteId = ?");
                parameters.add(new Integer(associatedId.getSiteId()));
            }

            if (associationCategory != null && associationCategory.getId() != -1) {
                // Fetch only those in given category/column
                query.append(" and associations.Category = ?");
                parameters.add(new Integer(associationCategory.getId()));
            }
        } else if (contentList != null && contentList.length > 0) {
            query.append(" and associations.UniqueId in (");
            for (int i=0; i < contentList.length; i++){
                if (contentList[i] != null) {
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append("?");
                    parameters.add(new Integer(contentList[i].getAssociationId()));
                }
            }
            query.append(") and associations.Type <> " + AssociationType.SHORTCUT);
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
                parameters.add(new Integer(associationCategory.getId()));
            }
        } else if (excludedPathElementIds != null) {
            for (int i=0; i<excludedPathElementIds.length; i++){
                if (excludedPathElementIds[i]!=null) {
                    // exclude all published under given id
                    query.append(" and associations.Path not like ? and associations.AssociationId <> ? ");
                    parameters.add("/%" + excludedPathElementIds[i].getAssociationId() + "/%");
                    parameters.add(new Integer(excludedPathElementIds[i].getAssociationId()));
                }
            }
        } else if (sql != null) {
            query.append(sql);
        } else if (attributes == null) {
            query.append(" and associations.Type = ?");
            parameters.add(new Integer(AssociationType.DEFAULT_POSTING_FOR_SITE));
        }

        if (siteId != -1) {
            query.append(" and associations.SiteId = ?");
            parameters.add(new Integer(siteId));
        }

        if (language != -1) {
            query.append(" and contentversion.Language = ?");
            parameters.add(new Integer(language));
        }

        if (contentType != null) {
            query.append(" and content.Type = ?");
            parameters.add(new Integer(contentType.getTypeAsInt()));
        }

        if (documentType != null && documentType.length > 0) {
            query.append(" and content.DocumentTypeId in (");
            for (int i = 0; i < documentType.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
                parameters.add(new Integer(documentType[i]));
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
                parameters.add(new Integer(contentTemplate[i]));
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
                parameters.add(new Integer(displayTemplate[i]));
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
        }
        if (expireDateTo != null) {
            query.append(" and content.ExpireDate <= ?");
            parameters.add(expireDateTo);
        }

        if (expireDateFrom == null && expireDateTo == null) {
            // Fetch archived pages if specified
            if (showExpired) {
                query.append(" and content.VisibilityStatus in (" + ContentVisibilityStatus.ACTIVE + ", " + ContentVisibilityStatus.ARCHIVED + ", " + ContentVisibilityStatus.EXPIRED + ")");
            } else if (showArchived) {
                query.append(" and content.VisibilityStatus in (" + ContentVisibilityStatus.ACTIVE + ", " + ContentVisibilityStatus.ARCHIVED + ")");
            } else {
                query.append(" and content.VisibilityStatus in (" + ContentVisibilityStatus.ACTIVE + ")");
            }
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
            if (keyword.indexOf("%") == -1) {
                keyword = "%" + keyword + "%";
            }
            if ((driver.indexOf("oracle") != -1) || (driver.indexOf("postgresql") !=-1)) {
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
                if (driver.indexOf("mysql") != -1) {
                    Log.info(SOURCE, "Using query with intersectingTopics is slow on MySQL", null, null);
                }

                query.append(" and content.contentId in (select ct2topic.contentId from ct2topic where ");
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = (Topic)topics.get(i);
                    if (i > 0){
                        query.append(" and ct2topic.contentId in (select ct2topic.contentId from ct2topic where ");
                    }
                    query.append(" (TopicMapId = ? and TopicId = ?)");
                    parameters.add(new Integer(topic.getTopicMapId()));
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
                    parameters.add(new Integer(topic.getTopicMapId()));
                    parameters.add(topic.getId());
                }
                query.append(") ");
            }
        }

        if (topicMapId != -1) {
            query.append(" and content.contentId in (select ct2topic.contentId from ct2topic where TopicMapId = ?");
            parameters.add(new Integer(topicMapId));
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
                PreparedStatement st = c.prepareStatement("select cv.ContentId from contentversion cv, contentattributes ca where cv.IsActive = 1 and cv.ContentVersionId = ca.ContentVersionId and ca.name = ? and ca.value like ?");
                for (int i = 0; i < attributes.size(); i++) {
                    Attribute a = (Attribute)attributes.get(i);
                    st.setString(1, a.getName());
                    st.setString(2, a.getValue());

                    int noFound = 0;
                    ResultSet rs = st.executeQuery();
                    query.append(" and content.ContentId in (");
                    while(rs.next()) {
                        if (noFound > 0) {
                            query.append(",");
                        }
                        int id = rs.getInt("ContentId");
                        query.append("?");
                        parameters.add(new Integer(id));
                        noFound++;
                    }
                    query.append(")");
                    if (noFound == 0) {
                        return null;
                    }
                }
                st.close();
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

        if (useSqlSort) {
            query.append(sortOrder.getSqlSort());
        } else {
            query.append(" order by ContentVersionId ");
        }

        if (maxRecords != -1 && useSqlSort && driver.indexOf("mysql") != -1 && joinTables.size() == 0) {
            // Only limit if not using join
            query.append(" limit ").append(maxRecords);
        }

        //Log.debug(SOURCE, "Query:" + query, null, null);
        PreparedStatement st = c.prepareStatement(query.toString());
        for (int i = 0; i < parameters.size(); i++) {
            Object o = parameters.get(i);
            //Log.debug(SOURCE, "Parameter:" + (i+1) + ":" + o, null, null);
            if (o instanceof Date) {
                Date d = (Date)o;
                st.setTimestamp(i + 1, new java.sql.Timestamp(d.getTime()));
            } else {
                st.setObject(i + 1, o);
            }
        }

        return st;

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
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(id);
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
                ContentTemplate ct = ContentTemplateCache.getTemplateByName(template);
                if (ct != null) {
                    id = ct.getId();
                } else {
                    Log.info(SOURCE, "Reference to contenttemplate which does not exists:" + template, null, null);
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
        if (sortOrder != null && sortOrder.getSqlSort() != null) {
            return true;
        }
        return false;
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

    public void setDocumentType(String documentType) throws SystemException {
        String doctypes[] = documentType.split("\\|");
        this.documentType = new int[doctypes.length];
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
            this.documentType[i] = id;
        }
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
                    Log.info(SOURCE, "Reference to displaytemplate which does not exists:" + template, null, null);
                }
            }
            this.displayTemplate[i] = id;
        }
    }

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
            attributes = new ArrayList();
        }
        attributes.add(a);
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public void setTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList();
        }
        topics.add(topic);
    }

    public void setTopics(List topics) {
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
}
