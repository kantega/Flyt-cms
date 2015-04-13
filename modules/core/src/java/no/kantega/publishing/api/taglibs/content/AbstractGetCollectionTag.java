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

package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.topicmaps.ao.TopicMapDao;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

public class AbstractGetCollectionTag extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractGetCollectionTag.class);

    protected String name = null;
    protected String orderBy = null;
    protected String association = null;
    protected String contentList = null;
    protected String contentTemplate = null;
    protected String contentType = null;
    protected String documentType = null;
    protected String excludedDocumentTypes = null;
    protected String displayTemplate = null;
    protected String keyword = null;
    protected String relevance = null;
    protected ContentIdentifier associatedId = null;
    protected Date modifiedDate = null;
    protected Date publishedFromDate = null;
    protected Date publishedToDate = null;
    protected Date expireFromDate = null;
    protected Date expireToDate = null;
    protected Date revisionDateTo = null;
    protected Date revisionDateFrom = null;
    protected List<Topic> topics = null;
    protected Topic topic = null;
    protected String topicId = null;
    protected int topicMapId = -1;
    protected String owner = null;
    protected String ownerPerson = null;
    protected String onHearingFor;
    protected ContentIdentifier[] pathElementIds = null;

    protected ContentQuery contentQuery = null;

    protected int siteId = -1;

    protected int offset = 0;
    protected int max = -1;
    protected boolean descending = false;
    protected boolean findAll = false;
    protected boolean skipAttributes = false;
    protected boolean skipTopics = true;
    protected boolean showArchived = false;
    protected boolean showExpired = false;
    protected boolean shuffle = false;
    protected int shuffleMax = -1;

    private static SiteCache siteCache;
    private static ContentIdHelper contentIdHelper;
    private static TopicMapDao topicMapDao;

    /**
     * Cleanup after tag is finished
     * @return EVAL_PAGE
     */
    public int doEndTag() {
        pageContext.removeAttribute("aksess_collection_" + name);
        pageContext.removeAttribute("aksess_collection_offset" + name);
        name = null;
        orderBy = null;
        association = null;
        contentList = null;
        contentQuery = null;
        contentTemplate = null;
        contentType = null;
        descending = false;
        documentType = null;
        excludedDocumentTypes = null;
        displayTemplate = null;
        keyword = null;
        associatedId = null;
        modifiedDate = null;
        publishedFromDate = null;
        publishedToDate = null;
        expireFromDate = null;
        expireToDate = null;
        showArchived = false;
        showExpired = false;
        topic = null;
        topics = null;
        topicId = null;
        topicMapId = -1;
        pathElementIds = null;
        siteId = -1;

        findAll = false;
        skipAttributes = false;
        skipTopics = true;
        showArchived = false;
        showExpired = false;
        shuffle = false;
        shuffleMax = -1;
        offset = 0;
        max = -1;

        return EVAL_PAGE;
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        if (contentIdHelper == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            contentIdHelper = context.getBean(ContentIdHelper.class);
            topicMapDao = context.getBean(TopicMapDao.class);
            siteCache = context.getBean(SiteCache.class);
        }
    }

    /**
     * Gets collection of pages using ContentManagementService
     * @param pageContext for access to HttpServletRequest
     * @return {@code List<Content>} matching either the parameters, or ContentQuery set.
     * @throws SystemException
     * @throws JspException
     */
    protected List<Content> getCollection(PageContext pageContext) throws SystemException, JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        ContentManagementService cs = new ContentManagementService(request);

        if (orderBy == null) {
            if (contentList == null) {
                orderBy = ContentProperty.TITLE;
            } else {
                orderBy = ContentProperty.PRIORITY;
            }
        }

        ContentQuery query = new ContentQuery();
        query.setShowArchived(showArchived);
        query.setShowExpired(showExpired);

        if (contentQuery == null) {
            boolean useAssociatedId = true;
            if (modifiedDate != null) {
                query.setModifiedDate(modifiedDate);
                useAssociatedId = false;
            }
            if (revisionDateFrom != null) {
                query.setRevisionDateFrom(revisionDateFrom);
                useAssociatedId = false;
            }
            if (revisionDateTo != null) {
                query.setRevisionDateTo(revisionDateTo);
                useAssociatedId = false;
            }
            if (associatedId != null) {
                query.setAssociatedId(associatedId);
                useAssociatedId = false;
            }
            if(topic != null) {
                query.setTopic(topic);
                useAssociatedId = false;
            } else if(topics != null) {
                query.setTopics(topics);
                useAssociatedId = false;
            } else if (topicId != null && topicId.trim().length() > 0 && topicMapId != -1) {
                query.setTopic(new Topic(topicId, topicMapId));
                useAssociatedId = false;
            }

            if (contentList != null) {
                String ids;
                if (contentList.length() == 0) {
                    return null;
                }

                if (contentList.charAt(0) >= '0' && contentList.charAt(0) <= '9') {
                    // Interpret as a list with ids (numbers)
                    ids = contentList;
                } else {
                    // Interpret as attribute navn that contains list with ids
                    Content content = tryGetFromRequest(request);
                    if (content == null) {
                        return null;
                    }
                    ids = content.getAttributeValue(contentList);
                }

                if (ids.length() > 0) {
                    query.setContentList(ids);
                } else {
                    return null;
                }
                useAssociatedId = false;
            }
            if (pathElementIds != null && pathElementIds.length > 0) {
                query.setPathElementIds(asList(pathElementIds));
                useAssociatedId = false;
            }

            if (relevance != null) {
                if (relevance.equalsIgnoreCase("user")) {
                    // Find pages marked with topics in current users profile
                    if (cs.getSecuritySession() != null){
                        User user = cs.getSecuritySession().getUser();
                        if (user != null && user.getTopics() != null) {
                            query.setTopics(user.getTopics());
                        }
                    }

                }
            }

            if (siteId != -1) {
                query.setSiteId(siteId);
            }

            if (contentTemplate != null) {
                query.setContentTemplate(contentTemplate);
            }

            if (contentType != null) {
                query.setContentType(contentType);
            }

            if (documentType != null) {
                query.setDocumentType(documentType);
            }

            if (excludedDocumentTypes != null) {
                query.setExcludedDocumentTypes(excludedDocumentTypes);
            }

            if (displayTemplate != null) {
                query.setDisplayTemplate(displayTemplate);
            }

            if (publishedFromDate != null) {
                query.setPublishDateFrom(publishedFromDate);
            }

            if (publishedToDate != null) {
                query.setPublishDateTo(publishedToDate);
            }
            if (expireFromDate != null) {
                query.setExpireDateFrom(expireFromDate);
            }

            if (expireToDate != null) {
                query.setExpireDateTo(expireToDate);
            }

            if (keyword != null && keyword.length() > 0) {
                query.setKeyword(keyword);
            }

            if(owner != null && owner.length() > 0) {
                query.setOwner(owner);
            }

            if(ownerPerson != null && ownerPerson.length() > 0) {
                query.setOwnerPerson(ownerPerson);
            }

            if(onHearingFor != null && onHearingFor.length() > 0) {
                query.setOnHearingFor(onHearingFor);
            }

            AssociationCategory associationCategory = null;
            if (association != null && association.length() > 0) {
                associationCategory = cs.getAssociationCategoryByPublicId(association);
                if (associationCategory == null) {
                    // If the category / column was not found, nothing should be returned
                    associationCategory = new AssociationCategory();
                    associationCategory.setId(9999);
                    associationCategory.setName("dummy");
                }
                query.setAssociationCategory(associationCategory);
            }


            if (useAssociatedId && !findAll) {
                // Standard: Find content connected to this page
                try {
                    Content content = tryGetFromRequest(request);
                    if (content != null) {
                        associatedId = content.getContentIdentifier();
                    } else {
                        associatedId = contentIdHelper.fromRequest(request);
                    }
                } catch (Exception e) {
                    // No content found
                }

                if (associatedId != null && associatedId.getAssociationId() != -1) {
                    query.setAssociatedId(associatedId);
                } else {
                    query = null;
                }
            }
        } else {
            query = contentQuery;
        }

        List<Content> collection = new ArrayList<>();

        if (query != null) {
            query.setOffset(offset);
            collection = cs.getContentList(query, max, new SortOrder(orderBy, descending), !skipAttributes, !skipTopics);
            if(shuffle) {
                Collections.shuffle(collection);
                if(shuffleMax != -1 && collection.size() > shuffleMax) {
                    collection = collection.subList(0, shuffleMax);
                }
            }
        }

        return collection;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void setTopicid(String topicId) {
        this.topicId = topicId;
    }

    /**
     * @deprecated use topicMap
     */
    @Deprecated
    public void setTopicmapid(int topicMapId) {
        this.topicMapId = topicMapId;
    }

    public void setTopicmap(String topicmap) {
        TopicMap tm = topicMapDao.getTopicMapByName(topicmap);
        if (tm != null) {
            this.topicMapId = tm.getId();
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwnerperson(String ownerPerson) {
        this.ownerPerson = ownerPerson;
    }

    public void setOnhearingfor(String onHearingFor) {
        this.onHearingFor = onHearingFor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrderby(String orderBy) {
        this.orderBy = orderBy.toLowerCase();
    }

    public void setContentlist(String contentList) {
        this.contentList = contentList;
    }

    public void setContenttemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public void setContenttype(String contentType) {
        this.contentType = contentType;
    }

    public void setDocumenttype(String documentType) {
        this.documentType = documentType;
    }

    public void setExcludeddocumenttypes(String excludedDocumentTypes) {
        this.excludedDocumentTypes = excludedDocumentTypes;
    }

    public void setDisplaytemplate(String displayTemplate) {
        try {
            int displayTemplateId = Integer.parseInt(displayTemplate);
            ContentManagementService cms = new ContentManagementService((HttpServletRequest)pageContext.getRequest());
            DisplayTemplate dt = cms.getDisplayTemplate(displayTemplateId);
            if(dt != null) {
                displayTemplate = dt.getName();
            }
        } catch (NumberFormatException e) {
            // Do nothing, name of displaytemplate was supplied instead of id
        } catch (SystemException e) {
            log.error("Error setting displaytemplate " + displayTemplate, e);
        }

        this.displayTemplate = displayTemplate;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSite(String siteId) {
        if (siteId != null && siteId.length() > 0) {
            try {
                this.siteId = Integer.parseInt(siteId);
            } catch (NumberFormatException e) {
                try {
                    Site site = siteCache.getSiteByPublicIdOrAlias(siteId);
                    if (site != null) {
                        this.siteId = site.getId();
                    }
                } catch (SystemException e1) {
                    log.error("Could not set site " + siteId, e1);
                }
            }
        }
    }

    public void setFindall(boolean findAll) {
        this.findAll = findAll;
    }

    public void setShowarchived(boolean showArchived) {
        this.showArchived = showArchived;
    }

    public void setShowexpired(boolean showExpired) {
        this.showExpired = showExpired;
    }

    public void setSkipattributes(boolean skipAttributes) {
        this.skipAttributes = skipAttributes;
    }

    public void setSkiptopics(boolean skipTopics) {
        this.skipTopics = skipTopics;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    public void setAssociatedid(String id) throws JspException {
        if (id != null && id.length() > 0) {
            try {
                Content content = AttributeTagHelper.getContent(pageContext, null, id);
                if (content != null) {
                    associatedId = content.getContentIdentifier();
                }
            } catch (NotAuthorizedException e) {

            }
        }
    }

    /**
     * @deprecated Use associationcategory
     */
    @Deprecated
    public void setAssociation(String association) {
        this.association = association;
    }

    public void setAssociationcategory(String association) {
        this.association = association;
    }

    public void setPublishedfromdate(Object publishedFromDate) {
        this.publishedFromDate = getDate(publishedFromDate);
    }

    public void setPublishedtodate(Object publishedToDate) {
        this.publishedToDate = getDate(publishedToDate);
    }

    public void setExpiretodate(Object expireToDate) {
        this.expireToDate = getDate(expireToDate);
    }

    public void setExpirefromdate(Object expireFromDate) {
        this.expireFromDate = getDate(expireFromDate);
    }

    public void setModifieddate(Object modifiedDate) {
        this.modifiedDate = getDate(modifiedDate);
    }

    public void setRevisiondatefrom(Object revisionDateFrom) {
        this.revisionDateFrom = getDate(revisionDateFrom);
    }

    public void setRevisiondateto(Object revisionDateTo) {
        this.revisionDateTo = getDate(revisionDateTo);
    }

    private Date getDate(Object dateObj) {
        Date date = null;

        if (dateObj != null) {
            if (dateObj instanceof Date) {
                date = (Date)dateObj;
            } else if (dateObj instanceof String && !"".equals(dateObj)) {
                DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
                try {
                    date = df.parse((String)dateObj);
                } catch (ParseException e) {
                    log.error("Could not parse " + dateObj, e);
                }
            }
        }
        return date;
    }

    public void setPathelementid(String id) throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        if (id != null && id.length() > 0) {
            if (!Character.isDigit(id.charAt(0))) {
                //Alias
                try {
                    ContentIdentifier contentIdentifier = contentIdHelper.fromRequestAndUrl(request, id);
                    id = String.valueOf(contentIdentifier.getAssociationId());
                } catch (Exception e) {
                    log.error("Could not set pathElementid " + id, e);
                }
            }

            StringTokenizer st = new StringTokenizer(id, ",");
            int tokenCount = st.countTokens();
            pathElementIds = new ContentIdentifier[tokenCount];
            for (int i = 0; i < tokenCount; i++) {
                String elementId = st.nextToken();
                if (elementId != null && elementId.trim().length() > 0 && Character.isDigit(elementId.trim().charAt(0))) {
                    try {
                        ContentIdentifier pathElementId =  ContentIdentifier.fromAssociationId(Integer.parseInt(elementId.trim()));
                        RequestParameters param = new RequestParameters(request);
                        int language = param.getInt("language");
                        if (language != -1) {
                            pathElementId.setLanguage(language);
                        }
                        pathElementIds[i] = pathElementId;
                    } catch (NumberFormatException e) {
                        log.error("Could not parse " + elementId, e);
                    }
                }
            }
        }
    }

    public void setDescending(boolean desc) {
        this.descending = desc;

    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setContentquery(ContentQuery contentQuery) {
        this.contentQuery = contentQuery;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setShuffleMax(int shuffleMax) {
        this.shuffleMax = shuffleMax;
    }
}
