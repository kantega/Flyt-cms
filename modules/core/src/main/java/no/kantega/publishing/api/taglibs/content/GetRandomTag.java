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
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

public class GetRandomTag  extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetRandomTag.class);

    private String name = null;
    private String associationCategory = null;
    private String contentList = null;
    private String contentTemplate = null;
    private String contentType = null;
    private String documentType = null;
    private String displayTemplate = null;
    private ContentIdentifier associatedId = null;
    private int max = 1;
    private List<Content> collection = null;
    private int offset = 0;
    private static ContentIdHelper contentIdHelper;

    public void setAssociation(String association) {
        this.associationCategory = association;
    }

    public void setAssociationcategory(String association) {
        this.associationCategory = association;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDisplaytemplate(String displayTemplate) {
        this.displayTemplate = displayTemplate;
    }

    public void setAssociatedid(String id) {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        if (id != null && id.length() > 0) {
            char c = id.charAt(0);
            if (Character.isDigit(c)) {
                int tmp = Integer.parseInt(id);
                RequestParameters param = new RequestParameters(request);
                int language = param.getInt("language");
                associatedId = new ContentIdentifier();
                associatedId.setAssociationId(tmp);
                if (language != -1) {
                    associatedId.setLanguage(language);
                }
            } else {
                // Alias
                try {
                    if(contentIdHelper == null){
                        contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                    }
                    associatedId = contentIdHelper.fromRequestAndUrl(request, id);
                } catch (Exception e) {
                    log.error("Could not set associated id " + id, e);
                }
            }
        }
    }

    public void setMax(int max) {
        this.max = max;
    }


    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        String orderBy = ContentProperty.TITLE;

        ContentQuery query = new ContentQuery();

        List<Content> tmpcollection = null;
        collection = new ArrayList<>();

        try {
            ContentManagementService cs = new ContentManagementService(request);

            boolean useAssociatedId = true;
            if (associatedId != null) {
                query.setAssociatedId(associatedId);
                useAssociatedId = false;
            } else if (contentList != null) {
                Content content = tryGetFromRequest(request);
                if (content == null) {
                    return SKIP_BODY;
                }
                String ids = content.getAttributeValue(contentList);
                if (ids.length() > 0) {
                    query.setContentList(ids);
                } else {
                    return SKIP_BODY;
                }
                useAssociatedId = false;
            }

            if (contentTemplate != null) {
                query.setContentTemplate(contentTemplate);
                useAssociatedId = false;
            }

            if (contentType != null) {
                query.setContentType(contentType);
            }

            if (documentType != null) {
                query.setDocumentType(documentType);
                useAssociatedId = false;
            }

            if (displayTemplate != null) {
                query.setDisplayTemplate(displayTemplate);
            }

            if (useAssociatedId) {
                // Standard: Finner innhold knyttet til denne siden
                try {
                    Content content = tryGetFromRequest(request);
                    if (content != null) {
                        associatedId = content.getContentIdentifier();
                    } else {
                        if(contentIdHelper == null){
                            contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                        }
                        associatedId = contentIdHelper.fromRequest(request);
                    }
                } catch (Exception e) {
                    // Finner ikke noe innhold
                    return SKIP_BODY;
                }
                query.setAssociatedId(associatedId);
            }

            AssociationCategory category = null;
            if (associationCategory != null && associationCategory.length() > 0) {
                category = cs.getAssociationCategoryByPublicId(associationCategory);
                if (category == null) {
                    // Dersom det er angitt en spalte som ikke finnes, ikke vis noe
                    category = new AssociationCategory();
                    category.setId(9999);
                    category.setName("dummy");
                }
                query.setAssociationCategory(category);
            }
            query.setSortOrder(new SortOrder(orderBy, true));
            tmpcollection = cs.getContentList(query);

        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(e);
        }
        if (tmpcollection != null && tmpcollection.size() > 0) {
            max = Math.min(max, tmpcollection.size());
            for (int i = 0; i < max ; i++) {
                int random = (int)Math.floor((tmpcollection.size()*Math.random()));
                collection.add(tmpcollection.get(random));
                tmpcollection.remove(random);
            }
        }

        return doIter();
    }

    private int doIter() {
        int size = collection.size();
        if (offset < size) {
            pageContext.setAttribute("aksess_collection_" + name, collection.get(offset));
            pageContext.setAttribute("aksess_collection_offset" + name, offset);
            offset++;
            return EVAL_BODY_BUFFERED;
        } else {
            pageContext.removeAttribute("aksess_collection_" + name);
            pageContext.removeAttribute("aksess_collection_offset" + name);
            name = null;
            associationCategory = null;
            contentList = null;
            contentTemplate = null;
            contentType = null;
            documentType = null;
            displayTemplate = null;
            associatedId = null;
            collection = null;
            offset = 0;

            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        try {
            bodyContent.writeOut(getPreviousOut());
        } catch (IOException e) {
            throw new JspTagException("GetRandomTag: " + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }
        return doIter();
    }

}

