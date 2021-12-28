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

package no.kantega.publishing.api.taglibs.sitemap;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class GetSiteMapTag  extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetSiteMapTag.class);
    private static ContentIdHelper contentIdHelper;

    private String name = "sitemap";
    private int siteId = -1;
    private int rootId = -1;
    private int depth  = -1;
    private int language = -1;
    private int currentId = -1;
    protected boolean alwaysIncludeCurrentId = false;
    protected boolean ignoreLanguage = false;

    String associationCategory = null;

    private SiteCache siteCache;

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public void setSiteid(String siteId) {
        this.siteId = Integer.parseInt(siteId);
    }

    public void setRootid(String rootId) {
        if (rootId != null && rootId.length() > 0) {
            try {
                this.rootId = Integer.parseInt(rootId);
            } catch (NumberFormatException e) {
                try {
                    if(contentIdHelper == null){
                        contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                    }
                    ContentIdentifier cid = contentIdHelper.fromUrl(rootId);
                    this.rootId = cid.getAssociationId();
                } catch (ContentNotFoundException | SystemException e1) {
                    log.error("", e);
                }
            }
        }
    }


    public void setStartid(String startId) {
        setRootid(startId);
    }


    public void setDepth(int depth) {
        this.depth = depth;
    }


    public void setLanguage(int language) {
        this.language = language;
    }


    public void setAssociation(String associationCategory) {
        this.associationCategory = associationCategory;
    }


    public void setAssociationcategory(String associationCategory) {
        this.associationCategory = associationCategory;
    }


    public void setAlwaysincludecurrentid(boolean alwaysIncludeCurrentId) {
        this.alwaysIncludeCurrentId = alwaysIncludeCurrentId;
    }

    public void setIgnorelanguage(boolean ignoreLanguage) {
        this.ignoreLanguage = ignoreLanguage;
    }


    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);
            Content content = AttributeTagHelper.getContent(pageContext, null, null);


            if (language == -1 && !ignoreLanguage) {
                String lang = request.getParameter("language");
                if (lang != null) {
                    language = Integer.parseInt(lang);
                }

                if (language == -1) {
                    if (content != null) {
                        language = content.getLanguage();
                    }
                }
            }

            if(ignoreLanguage) {
                language = -1;
            }

            if (siteId == -1) {
                if (content != null) {
                    siteId = content.getAssociation().getSiteId();
                }
            }

            if (content != null) {
                currentId = content.getAssociation().getId();
            }

            if (siteId == -1) {
                setSiteCacheIfNull();
                Site site = siteCache.getSiteByHostname(pageContext.getRequest().getServerName());
                siteId = (site != null)? site.getId() : 1;
            }

            SiteMapEntry sitemap;
            if (alwaysIncludeCurrentId) {
                sitemap = cms.getSiteMap(siteId, depth, language, associationCategory, rootId, currentId);
            } else {
                sitemap = cms.getSiteMap(siteId, depth, language, associationCategory, rootId, -1);
            }
            request.setAttribute(name, sitemap);

        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        name = "sitemap";
        siteId = -1;
        rootId = -1;
        depth = -1;
        language = -1;
        currentId = -1;
        associationCategory = null;
        alwaysIncludeCurrentId = false;
        ignoreLanguage = false;

        return EVAL_PAGE;
    }

    private void setSiteCacheIfNull() {
        if(siteCache == null){
            siteCache = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(SiteCache.class);
        }
    }
}

