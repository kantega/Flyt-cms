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

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.commons.log.Log;
import no.kantega.commons.client.util.RequestParameters;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class GetRandomTag  extends BodyTagSupport {
    private static final String SOURCE = "aksess.GetRandomTag";

    private String name = null;
    private String associationCategory = null;
    private String contentList = null;
    private String contentTemplate = null;
    private String contentType = null;
    private String documentType = null;
    private String displayTemplate = null;
    private ContentIdentifier associatedId = null;
    private int max = 1;
    private List collection = null;
    private int offset = 0;

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
                    associatedId = new ContentIdentifier(request, id);
                } catch (Exception e) {
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

        List tmpcollection = null;
        collection = new ArrayList();

        try {
            ContentManagementService cs = new ContentManagementService(request);

            boolean useAssociatedId = true;
            if (associatedId != null) {
                query.setAssociatedId(associatedId);
                useAssociatedId = false;
            } else if (contentList != null) {
                Content content = (Content)request.getAttribute("aksess_this");
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
                    Content content = (Content)request.getAttribute("aksess_this");
                    if (content != null) {
                        associatedId = content.getContentIdentifier();
                    } else {
                        associatedId = new ContentIdentifier(request);
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

            tmpcollection = cs.getContentList(query, -1, new SortOrder(orderBy, true));

        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
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
            pageContext.setAttribute("aksess_collection_" + name, (Content)collection.get(offset));
            pageContext.setAttribute("aksess_collection_offset" + name, new Integer(offset));
            offset++;
            return EVAL_BODY_TAG;
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

