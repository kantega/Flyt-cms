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

import com.google.common.base.Function;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;

public class ContentQuery {
    private static final Logger log = LoggerFactory.getLogger(ContentQuery.class);

    private ContentIdentifier associatedId = null;
    private AssociationCategory associationCategory = null;
    private List<ContentIdentifier> contentList = null;
    private Date modifiedDate = null;
    private Date publishDateFrom = null;
    private Date publishDateTo = null;
    private Date expireDateFrom = null;
    private Date expireDateTo = null;
    private Date revisionDateFrom = null;
    private Date revisionDateTo = null;

    private List<ContentIdentifier> pathElementIds = null; // include only pages under given id in path
    private List<ContentIdentifier> excludedPathElementIds = null; // exclude pages under given id in path

    private List<Integer> contentTemplate = null;
    private ContentType contentType = null;
    private List<Integer> documentType = null;
    private List<Integer> excludedDocumentTypes = null;
    private List<Integer> displayTemplate = null;
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
    private List<Integer> excludedAssociationTypes = null;

    private int maxRecords = -1;
    private int offset = 0;

    private List<Attribute> attributes  = null;

    private boolean intersectingTopics = false;

    public ContentQuery() {
    }


    public QueryWithParameters getQueryWithParameters() {
        boolean useSqlSort = useSqlSort();

        Map<String, Object> parameters = new HashMap<>();

        StringBuilder query = new StringBuilder();

        String driver = dbConnectionFactory.getDriverName();

        List<String> joinTables = new ArrayList<>(4);
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
        query.append(StringUtils.join(joinTables, ','));


        query.append(" where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0) ");
        query.append("and contentversion.Status IN (:status)");
        List<Integer> statuses = new ArrayList<>(4);
        statuses.add(ContentStatus.PUBLISHED.getTypeAsInt());
        if(onHearingFor != null) {
            statuses.add(ContentStatus.HEARING.getTypeAsInt());
        }
        if (includeDrafts) {
            statuses.add(ContentStatus.DRAFT.getTypeAsInt());
        }
        if(includeWaitingForApproval){
            statuses.add(ContentStatus.WAITING_FOR_APPROVAL.getTypeAsInt());
        }
        parameters.put("status", statuses);
        query.append(" and content.ContentId = associations.ContentId");

        if (associatedId != null) {
            if (associatedId.getAssociationId() != -1) {
                query.append(" and associations.ParentAssociationId = :ParentAssociationId");
                parameters.put("ParentAssociationId", associatedId.getAssociationId());
            }
            if (associatedId.getSiteId() != -1) {
                query.append(" and associations.SiteId = :siteid");
                parameters.put("siteid", associatedId.getSiteId());
            }

            if (associationCategory != null && associationCategory.getId() != -1) {
                // Fetch only those in given category/column
                query.append(" and associations.Category = :category");
                parameters.put("category", associationCategory.getId());
            }
        } else if (contentList != null && !contentList.isEmpty()) {
            query.append(" and associations.UniqueId in (:contentlist)");
            parameters.put("contentlist", transform(contentList, cidToAssociationIdTransformer));

            if (excludedAssociationTypes == null) {
                excludedAssociationTypes = asList(AssociationType.SHORTCUT);
            }
        } else if (pathElementIds != null) {
            for (int i = 0; i < pathElementIds.size(); i++) {
                String key = "pathelement".concat(String.valueOf(i));
                if (i == 0) {
                    query.append(" and (associations.Path like :").append(key);
                } else {
                    query.append(" or associations.Path like :").append(key);
                }
                parameters.put(key, "%/" + pathElementIds.get(i).getAssociationId() + "/%");
                if (i == (pathElementIds.size()-1)) {
                    query.append(")");
                }
            }
            if (associationCategory != null && associationCategory.getId() != -1) {
                // Fetch only those in given category/column
                query.append(" and associations.Category = :category");
                parameters.put("category", associationCategory.getId());
            }
        } else if (excludedPathElementIds != null) {
            for (ContentIdentifier excludedPathElementId : excludedPathElementIds) {
                if (excludedPathElementId != null) {
                    // exclude all published under given id
                    query.append(" and associations.Path not like :associationpath and associations.AssociationId <> :excludepathid ");
                    parameters.put("associationpath", "/%" + excludedPathElementId.getAssociationId() + "/%");
                    parameters.put("excludepathid", excludedPathElementId.getAssociationId());
                }
            }
        } else if (sql != null) {
            String s = sql.trim();
            if (!s.startsWith("and") && !s.startsWith("or")) {
                query.append(" and ");
            }
            query.append(sql);
        } else if (attributes == null && excludedAssociationTypes == null) {
            excludedAssociationTypes = asList(AssociationType.CROSS_POSTING, AssociationType.SHORTCUT);
        }

        if(excludedAssociationTypes != null && excludedAssociationTypes.size() > 0){
            query.append(" and associations.Type not in (:excludedAssociationTypes)");
            parameters.put("excludedAssociationTypes", excludedAssociationTypes);
        }

        if (siteId != -1) {
            query.append(" and associations.SiteId = :siteId");
            parameters.put("siteId", siteId);
        }

        if (language != -1) {
            query.append(" and contentversion.Language = :language");
            parameters.put("language", language);
        }

        if (contentType != null) {
            query.append(" and content.ContentType = :contentType");
            parameters.put("contentType", contentType.getTypeAsInt());
        }

        if (documentType != null && documentType.size() > 0) {
            query.append(" and content.DocumentTypeId in (:documenttype)");
            parameters.put("documenttype", documentType);
        }

        if (excludedDocumentTypes != null && excludedDocumentTypes.size()> 0) {
            query.append(" and content.DocumentTypeId not in (:excludedDocumentTypes)");
            parameters.put("excludedDocumentTypes", excludedDocumentTypes);
        }


        if (contentTemplate != null && contentTemplate.size() > 0) {
            query.append(" and content.ContentTemplateId in (:contentTemplates)");
            parameters.put("contentTemplates", contentTemplate);
        }

        if (displayTemplate != null && displayTemplate.size() > 0) {
            query.append(" and content.DisplayTemplateId in (:displayTemplates)");
            parameters.put("displayTemplates", displayTemplate);
        }

        // Fetch pages with given publishdate / expiredate
        if (publishDateFrom != null) {
            query.append(" and content.PublishDate >= :PublishDateFrom");
            parameters.put("PublishDateFrom", publishDateFrom);
        }
        if (publishDateTo != null) {
            query.append(" and content.PublishDate <= :PublishDateTo");
            parameters.put("PublishDateTo", publishDateTo);
        }
        if (expireDateFrom != null) {
            query.append(" and content.ExpireDate >= :expireDateFrom");
            parameters.put("expireDateFrom", expireDateFrom);
            showExpired = true;
        }
        if (expireDateTo != null) {
            query.append(" and content.ExpireDate <= :expireDateTo");
            parameters.put("expireDateTo", expireDateTo);
            showExpired = true;
        }

        // Fetch archived pages if specified
        query.append(" and content.VisibilityStatus in (:VisibilityStatus)");
        if (showExpired) {
            parameters.put("VisibilityStatus", asList(ContentVisibilityStatus.ACTIVE, ContentVisibilityStatus.ARCHIVED, ContentVisibilityStatus.EXPIRED));
        } else if (showArchived) {
            parameters.put("VisibilityStatus", asList(ContentVisibilityStatus.ACTIVE, ContentVisibilityStatus.ARCHIVED));
        } else {
            parameters.put("VisibilityStatus", ContentVisibilityStatus.ACTIVE);
        }

        if (owner != null) {
            query.append(" and content.Owner in (:ownerunit)");
            parameters.put("ownerunit", asList(owner.split(",")));

        }
        if (ownerPerson != null) {
            query.append(" and content.OwnerPerson in (:ownerpersons");
            parameters.put("ownerpersons", asList(ownerPerson.split(",")));

        }

        if (keyword != null) {
            // Keyword given, search title and alternative title
            if (!keyword.contains("%")) {
                keyword = "%" + keyword + "%";
            }
            if ((driver.contains("oracle")) || (driver.contains("postgresql"))) {
                // Oracle and PostgreSQL is case sensitive
                keyword = keyword.toLowerCase();
                query.append(" and (lower(contentversion.Title) like :keyword or lower(contentversion.AltTitle) like :keyword)");
            } else {
                query.append(" and (contentversion.Title like :keyword or contentversion.AltTitle like :keyword)");
            }
            parameters.put("keyword", keyword);

        }
        if (modifiedDate != null) {
            query.append(" and contentversion.LastModified >= :modifiedDate");
            parameters.put("modifiedDate", modifiedDate);
        }
        if (revisionDateFrom != null) {
            query.append(" and content.RevisionDate is not null and content.RevisionDate >= :revisionDateFrom");
            parameters.put("revisionDateFrom", revisionDateFrom);
        }
        if (revisionDateTo != null) {
            query.append(" and content.RevisionDate is not null and content.RevisionDate <= :revisionDateTo");
            parameters.put("revisionDateTo", revisionDateTo);
        }

        if(topics != null && topics.size() > 0) {
            if (intersectingTopics) {
                // Cannot be done via join, potensially slow method
                if (driver.contains("mysql")) {
                    log.info( "Using query with intersectingTopics is slow on MySQL");
                }

                query.append(" and content.contentId in (select ct2topic.contentId from ct2topic where ");
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = topics.get(i);
                    if (i > 0){
                        query.append(" and ct2topic.contentId in (select ct2topic.contentId from ct2topic where ");
                    }
                    query.append(" (TopicMapId = :TopicMapId and TopicId = :TopicId)");
                    parameters.put("TopicMapId", topic.getTopicMapId());
                    parameters.put("TopicId", topic.getId());
                    if (i > 0){
                        query.append(")");
                    }
                }
                query.append(") ");
            } else {
                // Done via join - quickest way
                query.append(" and content.ContentId = ct2topic.ContentId and (");
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = topics.get(i);
                    if (i > 0) {
                        query.append(" or ");
                    }
                    query.append(" (TopicMapId = :TopicMapId and TopicId = :TopicId)");
                    parameters.put("TopicMapId", topic.getTopicMapId());
                    parameters.put("TopicId", topic.getId());
                }
                query.append(") ");
            }
        }

        if (topicMapId != -1) {
            query.append(" and content.contentId in (select ct2topic.contentId from ct2topic where TopicMapId = :topicMapId");
            parameters.put("topicMapId", topicMapId);
        }

        if (topicType != null) {
            query.append(" and content.contentId in (select contentId from ct2topic inner join tmtopic on ct2topic.topicId = tmtopic.topicId and ct2topic.topicMapId = tmtopic.topicMapId where ct2topic.topicMapId=:topictypetopicMapId and tmtopic.InstanceOf=:tmtopicinstanceof)");
            parameters.put("topictypetopicMapId", topicType.getTopicMapId());
            parameters.put("tmtopicinstanceof", topicType.getId());
        }

        if (onHearingFor != null) {
            query.append(" and content.contentid in (select contentversion.contentId from hearing, contentversion, hearinginvitee " +
                    " where  hearing.ContentversionId = contentversion.Contentversionid " +
                    " and hearinginvitee.HearingId = hearing.Hearingid " +
                    " and hearing.DeadLine > :hearingdeadline");
            parameters.put("hearingdeadline", new Date());

            if (!onHearingFor.equals("everyone")) {
                query.append(" and ((hearinginvitee.InviteeType = "+ HearingInvitee.TYPE_PERSON + " and hearinginvitee.InviteeRef = :InviteeRef)");
                parameters.put("InviteeRef", onHearingFor);

                Map managers = RootContext.getInstance().getBeansOfType(OrganizationManager.class);
                if (managers.size() > 0) {
                    OrganizationManager manager = (OrganizationManager) managers.values().iterator().next();
                    List<OrgUnit> orgUnits = manager.getOrgUnitsAboveUser(onHearingFor);
                    if(orgUnits.size() > 0) {
                        query.append(" or (hearinginvitee.InviteeType = " + HearingInvitee.TYPE_ORGUNIT +
                                "     and hearinginvitee.InviteeRef in (:unitids))");

                        List<String> orgunitIds = transform(orgUnits, new Function<OrgUnit, String>() {
                            @Override
                            public String apply(OrgUnit input) {
                                return input.getExternalId();
                            }
                        });
                        parameters.put("unitids", orgunitIds);
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
                        query.append(" and content.ContentId in (:attributecontentids)");
                        List<Integer> attributecontentids = new ArrayList<>();
                        while (rs.next()) {
                            if (noFound > 0) {
                                query.append(",");
                            }
                            int id = rs.getInt("ContentId");
                            attributecontentids.add(id);
                            noFound++;
                        }
                        if (noFound == 0) {
                            return null;
                        }
                        parameters.put("attributecontentids", attributecontentids);
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

    //  Setter methods only
    public void setAssociatedId(ContentIdentifier cid) {
        this.associatedId = cid;
    }


    public void setContentList(String contentList) {
        String cids[] = contentList.split(",");
        this.contentList = new ArrayList<>(cids.length);
        for (String cid : cids) {
            String tmp = cid.trim();
            int id = -1;
            try {
                id = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                log.error("Could not format contentid " + tmp, e);
            }
            this.contentList.add(ContentIdentifier.fromAssociationId(id));
        }
    }

    public void setContentList(ContentIdentifier[] contentList) {
        this.contentList = asList(contentList);
    }

    public List<ContentIdentifier> getContentList() {
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
        this.contentTemplate = asList(contentTemplate);
    }

    public void setContentTemplate(String contentTemplate) throws SystemException {
        String templates[] = contentTemplate.split("\\|");
        this.contentTemplate = new ArrayList<>(templates.length);
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
            this.contentTemplate.add(id);
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
        this.documentType = asList(documentType);
    }

    public void setExcludedDocumentTypes(String documentType) throws SystemException {
        this.excludedDocumentTypes = getDocumentTypesFromString(documentType);
    }

    public void setDocumentType(String documentType) throws SystemException {
        this.documentType = getDocumentTypesFromString(documentType);
    }

    private List<Integer> getDocumentTypesFromString(String documentType) {
        String doctypes[] = documentType.split("\\|");
        List<Integer> docTypeIds = new ArrayList<>(doctypes.length);
        for (String doctype1 : doctypes) {
            String doctype = doctype1.trim();
            int id = -1;
            try {
                id = Integer.parseInt(doctype);
            } catch (NumberFormatException e) {
                DocumentType dt = DocumentTypeCache.getDocumentTypeByPublicId(doctype);
                if (dt != null) {
                    id = dt.getId();
                }
            }
            docTypeIds.add(id);

        }
        return docTypeIds;
    }

    public void setDisplayTemplate(int displayTemplate) {
        this.displayTemplate = asList(displayTemplate);
    }

    public void setDisplayTemplate(String displayTemplate) throws SystemException {
        String templates[] = displayTemplate.split("\\|");
        this.displayTemplate = new ArrayList<>(templates.length);
        for (String template1 : templates) {
            String template = template1.trim();
            int id = -1;
            try {
                id = Integer.parseInt(template);
            } catch (NumberFormatException e) {
                DisplayTemplate dt = DisplayTemplateCache.getTemplateByPublicId(template);
                if (dt != null) {
                    id = dt.getId();
                } else {
                    log.info("Reference to displaytemplate which does not exists:" + template);
                }
            }
            this.displayTemplate.add(id);
            ;
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
            attributes = new ArrayList<>();
        }
        attributes.add(a);
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public void setTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
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
            this.pathElementIds = new ArrayList<>();
        }
        this.pathElementIds.add(pathElement);
    }

    public void setPathElementIds(List<ContentIdentifier> pathElementIds) {
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

    public List<ContentIdentifier> getExcludedPathElementIds() {
        return excludedPathElementIds;
    }

    public void setExcludedPathElementIds(List<ContentIdentifier> excludedPathElementIds) {
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

    public void setExcludedAssociationTypes(List<Integer> excludedAssociationTypes){
        this.excludedAssociationTypes = excludedAssociationTypes;
    }

    private final Function<ContentIdentifier,Integer> cidToAssociationIdTransformer = new Function<ContentIdentifier, Integer>() {
        @Override
        public Integer apply(ContentIdentifier input) {
            return input.getAssociationId();
        }
    };


    /**
     * Class representing an instance of a query, that is the query string and the parameters to be set.
     */
    public static class QueryWithParameters {
        private final String query;
        private final Map<String, Object> params;

        QueryWithParameters(String query, Map<String, Object> params) {
            this.query = query;
            this.params = params;
        }

        public String getQuery() {
            return query;
        }

        public Map<String, Object> getParams() {
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
