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

package no.kantega.publishing.api.taglibs.mini;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.api.content.ContentTemplateAO;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class CreateTag extends AbstractSimpleEditTag {
    private static final Logger log = LoggerFactory.getLogger(CreateTag.class);
    private static WebApplicationContext webApplicationContext;

    private int displayTemplateId = -1;
    private int contentTemplateId = -1;
    private String parentId = null;
    private String associationcategory = null;
    private String queryParams;
    private String action;

    public int doAfterBody() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            JspWriter out = bodyContent.getEnclosingWriter();
            String body = bodyContent.getString();

            StringBuilder link = new StringBuilder();

            if (content == null) {
                content = AttributeTagHelper.getContent(pageContext, collection, parentId);
            }

            SecuritySession session = SecuritySession.getInstance(request);
            if (content != null && session.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
                // Is authorized to edit page
                link.append("<a");
                if (cssclass != null) {
                    link.append(" class=\"").append(cssclass).append("\"");
                }
                if (linkId != null) {
                    link.append(" id=\"").append(linkId).append("\"");
                }
                link.append(" href=\"");
                link.append(URLHelper.getRootURL(request));
                if (action != null) {
                    link.append(action);
                    if (!action.endsWith("?")) {
                        link.append("?");
                    }
                } else {
                    link.append("admin/publish/SimpleEditContent.action?");
                }
                if (displayTemplateId > 0) {
                    link.append("templateId=");
                    link.append(displayTemplateId);
                } else if (contentTemplateId > 0) {
                    link.append("contentTemplateId=");
                    link.append(contentTemplateId);
                }
                link.append("&amp;parentId=");
                link.append(content.getAssociation().getId());
                link.append("&amp;categoryName=");
                link.append(associationcategory);
                if (redirectUrl != null) {
                    link.append("&amp;redirectUrl=");
                    link.append(redirectUrl);
                }
                if (cancelUrl != null) {
                    link.append("&amp;redirectUrl=");
                    link.append(cancelUrl);
                }
                if (queryParams != null && queryParams.trim().length() > 0) {
                    if (queryParams.startsWith("?")) {
                        queryParams = queryParams.replaceFirst("\\?", "");
                    }
                    if (!queryParams.startsWith("&")) {
                        link.append("&amp;");
                    }
                    link.append(queryParams);
                }
                link.append("\">");
                link.append(body);
                link.append("</a>");

                out.print(link.toString());
            }

        } catch (SystemException e) {
                throw new JspException(e);
        } catch (NotAuthorizedException e) {
                //
        } catch (IOException e) {
            log.error("", e);
            throw new JspTagException(e);
        } finally {
            bodyContent.clearBody();
        }


        displayTemplateId = -1;
        contentTemplateId = -1;
        parentId = null;
        associationcategory = null;

        resetVars();

        return SKIP_BODY;
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
    }

    public void setDisplaytemplateid(int displaytemplateid) {
        this.displayTemplateId = displaytemplateid;
    }

    public void setDisplaytemplatename(String displaytemplatename) {
        DisplayTemplate template = DisplayTemplateCache.getTemplateByPublicId(displaytemplatename);
        this.displayTemplateId = template.getId();
    }

    public void setContenttemplateid(int contenttemplateid) {
        this.contentTemplateId = contenttemplateid;
    }

    public void setContenttemplatename(String contenttemplatename) {
        ContentTemplate template = webApplicationContext.getBean(ContentTemplateAO.class).getTemplateByPublicId(contenttemplatename);
        this.contentTemplateId = template.getId();
    }

    public void setParentid(String parentid) {
        this.parentId = parentid;
    }

    public void setAssociationcategory(String associationcategory) {
        this.associationcategory = associationcategory;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
