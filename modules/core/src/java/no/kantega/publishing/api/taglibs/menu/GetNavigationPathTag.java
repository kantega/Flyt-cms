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
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class GetNavigationPathTag  extends BodyTagSupport {
    private static final String SOURCE = "aksess.GetNavigationPathTag";

    private String name = "menu";
    private int defaultId = -1;
    private boolean includeRoot = false;
    private boolean includeCurrent = true;

    private List pathitems = null;
    private int offset = 0;
    private String var = "entry";

    private int startId = -1;
    private int endId = -1;

    public int getStartId() {
        return startId;
    }

    public void setStartid(String startId) {
        if(startId != null && startId.length() > 0 ){
            try{
                this.startId = Integer.parseInt(startId);
            } catch(NumberFormatException e){
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    public void setStartid(int startId) {
        this.startId = startId;
    }


    public void setEndid(int endId) {
        this.endId = endId;
    }



    public int getEndId() {
        return endId;
    }

    public void setEndid(String endId) {
        if(endId != null && endId.length() > 0 ){
            try{
                this.endId = Integer.parseInt(endId);
            } catch(NumberFormatException e){
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public void setDefaultid(String defaultId) {
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

    public void setIncluderoot(boolean includeRoot) {
        this.includeRoot = includeRoot;
    }

    public void setIncludecurrent(boolean includeCurrent) {
        this.includeCurrent = includeCurrent;
    }

    public void setVar(String var) {
        this.var = var;
    }


    public int doStartTag() throws JspException {
        pathitems = new ArrayList();

        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);

            Content content = AttributeTagHelper.getContent(pageContext, null, null);
            if (content == null && defaultId != -1) {
                content = AttributeTagHelper.getContent(pageContext, null, "" + defaultId);
            }

            if (content != null) {
                List tmp = cms.getPathByAssociation(content.getAssociation());
                pathitems = new ArrayList();
                for (int i = 0; i < tmp.size(); i++) {
                    PathEntry p = (PathEntry)tmp.get(i);
                    if (i > 0 || includeRoot) {
                        if (pathitems.size() > 0 || startId == p.getId() || startId == -1){
                            SiteMapEntry s = new SiteMapEntry(p.getId(), p.getId(), -1, ContentType.PAGE, ContentStatus.PUBLISHED, ContentVisibilityStatus.ACTIVE, p.getTitle(), 0);
                            pathitems.add(s);
                        }
                    }

                    if (p.getId() == endId) break;
                }

                if (includeCurrent && content != null) {
                    // Vis dersom root skal vises
                    if (includeRoot || content.getAssociation().getParentAssociationId() > 0) {
                        SiteMapEntry s = new SiteMapEntry(content.getAssociation().getId(), content.getAssociation().getAssociationId(), -1, content.getType(), ContentStatus.PUBLISHED, ContentVisibilityStatus.ACTIVE, content.getTitle(), content.getNumberOfNotes());
                        s.setSelected(true);
                        pathitems.add(s);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        return doIter();
    }
                                                    
    private int doIter() {
        int size = pathitems.size();
        if (offset < size) {
            pageContext.setAttribute("aksess_menu_" + name, (SiteMapEntry)pathitems.get(offset));
            if(var != null) {
                pageContext.setAttribute(var, (SiteMapEntry)pathitems.get(offset));
            }
            pageContext.setAttribute("aksess_menu_" + name, new Integer(offset));
            offset++;
            return EVAL_BODY_TAG;
        } else {
            name = "menu";
            var="entry";
            defaultId = -1;
            includeRoot = false;
            includeCurrent = true;

            pathitems = null;
            offset = 0;
            startId = -1;
            endId = -1;

            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        try {
            bodyContent.writeOut(getPreviousOut());
        } catch (IOException e) {
            throw new JspTagException("GetNavigationPathTag: " + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }
        return doIter();
    }
}


