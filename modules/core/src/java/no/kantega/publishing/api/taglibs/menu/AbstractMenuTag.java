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

package no.kantega.publishing.api.taglibs.menu;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.api.taglibs.util.CollectionLoopTagStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.NavigationMapEntry;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
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


public abstract class AbstractMenuTag extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractMenuTag.class);
    private static ContentIdHelper contentIdHelper;

    protected String name = "menu";
    protected int siteId = -1;
    protected int rootId = -1;
    protected int depth  = -1;
    protected int startDepth = -1;
    protected int language = -1;
    protected int defaultId = -1;
    protected int currentId = -1;
    protected String currentPath = "";
    protected int defaultOpenId = -1;
    protected String defaultOpenPath = "";
    protected boolean includeRoot = false;
    protected boolean alwaysIncludeCurrentId = false;
    protected boolean alwaysIncludeCurrentPath = false;
    protected boolean ignoreLanguage = false;
    protected boolean checkAuthorization = false;
    String associationCategory = null;
    protected boolean expandAll = false;

    private List<NavigationMapEntry> menuitems =  new ArrayList<NavigationMapEntry>();

    protected String var = "entry";
    protected String varStatus = "status";

    protected CollectionLoopTagStatus status = null;

    protected abstract void printBody() throws IOException;
    protected abstract void reset();

    private SiteCache siteCache;

    public void setName(String name) {
        this.name = name;
    }

    public void setSiteid(String siteId) {
        if (siteId != null && siteId.length() > 0) {

            try {
                this.siteId = Integer.parseInt(siteId);
            } catch (NumberFormatException e) {
                try {
                    setSiteCacheIfNull();
                    Site site = siteCache.getSiteByPublicIdOrAlias(siteId);
                    if (site != null) {
                        this.siteId = site.getId();
                    }
                } catch (SystemException e1) {
                    log.error("Could not set siteid " + siteId, e1);
                }
            }
        }
    }

    private void setSiteCacheIfNull() {
        if(siteCache == null){
            siteCache = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(SiteCache.class);
        }
    }

    public void setRootid(String rootId) {
        if (rootId != null && rootId.length() > 0) {

            try {
                this.rootId = Integer.parseInt(rootId);
            } catch (NumberFormatException e) {
                try {
                    Content c = AttributeTagHelper.getContent(pageContext, null, rootId);
                    if (c != null) {
                        this.rootId = c.getAssociation().getAssociationId();
                    }
                } catch (SystemException e1) {
                    log.error("", e);
                } catch (NotAuthorizedException e1) {
                    // User not authorized, do nothing
                }
            }
        }
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setStartdepth(int startDepth) {
        this.startDepth = startDepth;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    /**
     * @deprecated Use associationcategory 
     */
    @Deprecated
    public void setAssociation(String associationCategory) {
        this.associationCategory = associationCategory;
    }

    public void setAssociationcategory(String associationCategory) {
        this.associationCategory = associationCategory;
    }

    public void setDefaultid(String defaultId)  throws JspException {
        if (defaultId != null && defaultId.length() > 0) {
            try {
                this.defaultId = Integer.parseInt(defaultId);
            } catch (NumberFormatException e) {
                try {
                    Content c = AttributeTagHelper.getContent(pageContext, null, defaultId);
                    if (c != null) {
                        this.defaultId = c.getAssociation().getAssociationId();
                    }
                } catch (SystemException e1) {
                    log.error("", e);
                } catch (NotAuthorizedException e1) {
                    // Do nothing
                }
            }
        }
    }


    public void setDefaultopenid(String defaultOpenId) throws JspException {
        if (defaultOpenId != null && defaultOpenId.length() > 0) {
            try {
                this.defaultOpenId = Integer.parseInt(defaultOpenId);
            } catch (NumberFormatException e) {
                try {
                    Content c = AttributeTagHelper.getContent(pageContext, null, defaultOpenId);
                    if (c != null) {
                        this.defaultOpenId = c.getAssociation().getAssociationId();
                    }
                } catch (SystemException e1) {
                    log.error("", e);
                } catch (NotAuthorizedException e1) {
                    // Do nothing
                }
            }
        }
    }


    public void setIncluderoot(boolean includeRoot) {
        this.includeRoot = includeRoot;
    }


    public void setAlwaysincludecurrentid(boolean alwaysIncludeCurrentId) {
        this.alwaysIncludeCurrentId = alwaysIncludeCurrentId;
    }

    public void setAlwaysincludecurrentpath(boolean alwaysIncludeCurrentPath) {
        this.alwaysIncludeCurrentPath = alwaysIncludeCurrentPath;
    }

    public void setIgnorelanguage(boolean ignoreLanguage) {
        this.ignoreLanguage = ignoreLanguage;
    }

    public void setVar(String var) {
        this.var = var;
    }
    public void setVarStatus(String varStatus){
        this.varStatus = varStatus;
    }

    public void setCheckauthorization(boolean checkAuthorization) {
        this.checkAuthorization = checkAuthorization;
    }

    public void setExpandall(boolean expandAll) {
        this.expandAll = expandAll;
    }


    private void addToSiteMap(SecuritySession securitySession, NavigationMapEntry sitemap, int currentDepth) {
        sitemap.setDepth(currentDepth);

        sitemap.setSelected(sitemap.getId() == currentId);

        sitemap.setOpen(sitemap.getId() == currentId || currentPath.contains("/" + sitemap.getId() + "/"));

        if (currentDepth == 0 && includeRoot) {
            currentDepth++;
        }

        if (currentDepth > 0 || includeRoot) {
            if(sitemap.getDepth() >= startDepth){
                menuitems.add(sitemap);
            }
        }

        addChildrenToSiteMap(securitySession, sitemap, currentDepth);
    }

    private void addChildrenToSiteMap(SecuritySession securitySession, NavigationMapEntry sitemap, int currentDepth) {
        List<NavigationMapEntry> children = sitemap.getChildren();
        for (int i = 0; i < children.size(); i++) {
            NavigationMapEntry child = children.get(i);

            child.setFirstChild(i == 0);
            child.setLastChild(i == children.size() - 1);

            boolean isOpen = determineWhetherIsOpen(child);
            if (expandAll || isOpen) {
                sitemap.setOpen(isOpen);

                // We get one more level than we need, don't display all
                int maxDepth = depth - Math.max(startDepth, 0);
                if (depth == -1 || currentDepth < maxDepth) {
                    boolean isAuthorized = false;
                    if (checkAuthorization) {
                        try {
                            isAuthorized = securitySession.isAuthorized(child, Privilege.VIEW_CONTENT);
                        } catch (SystemException e) {
                            log.error("", e);
                        }
                    }
                    if (isAuthorized || !checkAuthorization) {
                        addToSiteMap(securitySession, child, currentDepth + 1);
                    }
                }
            }
        }
    }

    private boolean determineWhetherIsOpen(NavigationMapEntry child) {
        boolean isOpen = false;
        if (child.getParentId() == 0 || child.getParentId() == currentId || currentPath.contains("/" + child.getParentId() + "/") || child.getParentId() == defaultOpenId || defaultOpenPath.contains("/" + child.getParentId() + "/")){
            isOpen = true;
        }
        return isOpen;
    }

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);
            SecuritySession securitySession = SecuritySession.getInstance(request);

            Content content = setContent(cms);
            setLanguage(request, content);

            setSiteId(content);
            int depth = setDepth();

            SiteMapEntry sitemap = setSiteMap(cms, depth);

            setOpenElement(securitySession, sitemap);
            status = new CollectionLoopTagStatus(menuitems);

        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(e);
        }

        return doIter();
    }

    private void setOpenElement(SecuritySession securitySession, SiteMapEntry sitemap) throws NotAuthorizedException {
        if (sitemap != null) {
            if (currentId == sitemap.getId() && defaultOpenId != -1) {
                Content openContent = AttributeTagHelper.getContent(pageContext, null, String.valueOf(defaultOpenId));
                if (openContent != null) {
                    defaultOpenPath = openContent.getAssociation().getPath();
                }
            }

            addToSiteMap(securitySession, sitemap, 0);
        }
    }

    private SiteMapEntry setSiteMap(ContentManagementService cms, int getDepth) {
        SiteMapEntry sitemap;
        if (alwaysIncludeCurrentId) {
            sitemap = cms.getSiteMap(siteId, getDepth, language, associationCategory, rootId, currentId);
        } else if (alwaysIncludeCurrentPath && currentPath.length() > 0) {
            sitemap = cms.getSiteMap(siteId, getDepth, language, associationCategory, rootId, StringHelper.getInts(currentPath + "/" + currentId, "/"));
        } else {
            sitemap = cms.getSiteMap(siteId, getDepth, language, associationCategory, rootId, -1);
        }
        return sitemap;
    }

    private Content setContent(ContentManagementService cms) throws NotAuthorizedException {
        Content content = AttributeTagHelper.getContent(pageContext, null, null);
        if (content == null && defaultId != -1) {
            content = AttributeTagHelper.getContent(pageContext, null, "" + defaultId);
        } else if (content == null && siteId != -1) {
            try {
                if(contentIdHelper == null){
                    contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                }
                ContentIdentifier cid = contentIdHelper.fromSiteIdAndUrl(siteId, "/");
                content = cms.getContent(cid);
            }  catch (ContentNotFoundException e) {
                //
            }
        }


        if (content != null) {
            currentId = content.getAssociation().getAssociationId();
            currentPath = content.getAssociation().getPath();
        }
        return content;
    }

    private int setDepth() {
        // To determine if elements have children we have to get more level than we should display
        int getDepth = depth;
        if (depth != -1) {
            getDepth = depth + 1;
        }
        return getDepth;
    }

    private void setSiteId(Content content) {
        if (siteId == -1) {
            if (content != null) {
                siteId = content.getAssociation().getSiteId();
            }
        }

        if (siteId == -1) {
            siteId = 1;
        }
    }

    private void setLanguage(HttpServletRequest request, Content content) {
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
            if (language == -1) {
                language = Language.NORWEGIAN_BO;
            }
        }

        if(ignoreLanguage) {
            language = -1;
        }
    }

    private int doIter() {

        if (status.getIndex() < status.getCount()) {
            pageContext.setAttribute("aksess_menu_" + name, status.getCurrent());
            if(var != null) {
                pageContext.setAttribute(var, status.getCurrent());
            }
            pageContext.setAttribute("aksess_menu_offset" + name, status.getIndex());

            // Current status
            if (varStatus != null) {
                pageContext.setAttribute(varStatus, status);
            }

            // Current menu object
            if (var != null) {
                pageContext.setAttribute(var, status.getCurrent());
            }
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        try {
            printBody();
        } catch (IOException e) {
            throw new JspTagException(e);
        } finally {
            bodyContent.clearBody();
        }
        status.incrementIndex();
        return doIter();
    }

    public int doEndTag() {
        name = "menu";
        var="entry";
        siteId = -1;
        rootId = -1;
        depth  = -1;
        language = -1;
        defaultId = -1;
        currentId = -1;
        currentPath = "";
        defaultOpenId = -1;
        defaultOpenPath = "";
        includeRoot = false;
        associationCategory = null;
        alwaysIncludeCurrentId = false;
        alwaysIncludeCurrentPath = false;
        ignoreLanguage = false;
        startDepth = -1;
        checkAuthorization = false;
        expandAll = false;
        menuitems.clear();

        reset();

        return EVAL_PAGE;
    }
}
