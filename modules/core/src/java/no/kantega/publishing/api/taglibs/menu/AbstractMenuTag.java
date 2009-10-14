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
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.api.taglibs.util.CollectionLoopTagStatus;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kristian Lier Selnæs, Kantega
 * Date: 21.des.2006
 * Time: 10:27:30
 */
public abstract class AbstractMenuTag extends BodyTagSupport {
    private static final String SOURCE = "aksess.AbstractMenuTag";

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
    protected boolean ignoreLanguage = false;
    protected boolean checkAuthorization = false;
    String associationCategory = null;
    protected boolean expandAll = false;

    private List menuitems = null;

    protected String var = "entry";
    protected String varStatus = "status";

    protected CollectionLoopTagStatus status = null;

    protected abstract void printBody() throws IOException;
    protected abstract void reset();

    public void setName(String name) {
        this.name = name;
    }

    public void setSiteid(String siteId) {
        if (siteId != null && siteId.length() > 0) {

            try {
                this.siteId = Integer.parseInt(siteId);
            } catch (NumberFormatException e) {
                try {
                    Site site = SiteCache.getSiteByPublicIdOrAlias(siteId);
                    if (site != null) {
                        this.siteId = site.getId();
                    }
                } catch (SystemException e1) {
                    Log.error(SOURCE, e1, null, null);
                }
            }
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
                    Log.error(SOURCE, e, null, null);
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
                    Log.error(SOURCE, e, null, null);
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
                    Log.error(SOURCE, e, null, null);
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

    private void addToSiteMap(SecuritySession securitySession, SiteMapEntry sitemap, int currentDepth) {
        sitemap.setDepth(currentDepth);

        if (sitemap.getId() == currentId) {
            sitemap.setSelected(true);
        }

        if (currentDepth == 0 && includeRoot) {
            currentDepth++;
        }

        if (currentDepth > 0 || includeRoot) {
            if(sitemap.getDepth() >= startDepth){
                menuitems.add(sitemap);
            }
        }

        List children = sitemap.getChildren();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                SiteMapEntry child = (SiteMapEntry)children.get(i);
                boolean isOpen = false;
                if(child.getParentId() == 0 || child.getParentId() == currentId || currentPath.indexOf("/" + child.getParentId() + "/") != -1 || child.getParentId() == defaultOpenId || defaultOpenPath.indexOf("/" + child.getParentId() + "/") != -1){
                    isOpen = true;
                }
                if (expandAll || isOpen) {
                    if(isOpen){
                        sitemap.setOpen(true);
                    }
                    // We get one more level than we need, don't display all
                    int maxDepth = depth - Math.max(startDepth, 0);
                    if (depth == -1 || currentDepth < maxDepth) {
                        boolean isAuthorized = false;
                        if (checkAuthorization) {
                            try {
                                isAuthorized = securitySession.isAuthorized(child, Privilege.VIEW_CONTENT);
                            } catch (SystemException e) {
                                Log.error(SOURCE, e, null, null);
                            }
                        }
                        if (isAuthorized || !checkAuthorization) {
                            addToSiteMap(securitySession, child, currentDepth + 1);
                        }
                    }
                }
            }
        }
    }

    public int doStartTag() throws JspException {
        menuitems = new ArrayList();

        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);
            SecuritySession securitySession = SecuritySession.getInstance(request);

            Content content = AttributeTagHelper.getContent(pageContext, null, null);
            if (content == null && defaultId != -1) {
                content = AttributeTagHelper.getContent(pageContext, null, "" + defaultId);
            } else if (content == null && siteId != -1) {
                try {
                    ContentIdentifier cid = new ContentIdentifier(siteId, "/");
                    content = cms.getContent(cid);
                }  catch (ContentNotFoundException e) {
                    //
                }
            }

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

            if (content != null) {
                currentId = content.getAssociation().getAssociationId();
                currentPath = content.getAssociation().getPath();
            }

            if (siteId == -1) {
                if (content != null) {
                    siteId = content.getAssociation().getSiteId();
                }
            }

            if (siteId == -1) {
                siteId = 1;
            }

            // To determine if elements have children we have to get more level than we should display
            int getDepth = depth;
            if (depth != -1) {
                getDepth = depth + 1;
            }

            SiteMapEntry sitemap;
            if (alwaysIncludeCurrentId) {
                sitemap = cms.getSiteMap(siteId, getDepth, language, associationCategory, rootId, currentId);
            } else {
                sitemap = cms.getSiteMap(siteId, getDepth, language, associationCategory, rootId, -1);
            }


            if (sitemap != null) {
                if (currentId == sitemap.getId() && defaultOpenId != -1) {
                    Content openContent = AttributeTagHelper.getContent(pageContext, null, "" + defaultOpenId);
                    if (openContent != null) {
                        defaultOpenPath = openContent.getAssociation().getPath();
                    }
                }

                addToSiteMap(securitySession, sitemap, 0);
            }
            status = new CollectionLoopTagStatus(menuitems);

        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        return doIter();
    }

    private int doIter() {

        if (status.getIndex() < status.getCount()) {
            pageContext.setAttribute("aksess_menu_" + name, status.getCurrent());
            if(var != null) {
                pageContext.setAttribute(var, status.getCurrent());
            }
            pageContext.setAttribute("aksess_menu_offset" + name, new Integer(status.getIndex()));

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
            throw new JspTagException("GetCollectionTag: " + e.getMessage());
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
        ignoreLanguage = false;
        startDepth = -1;
        checkAuthorization = false;
        expandAll = false;
        menuitems = null;

        reset();

        return EVAL_PAGE;
    }
}
