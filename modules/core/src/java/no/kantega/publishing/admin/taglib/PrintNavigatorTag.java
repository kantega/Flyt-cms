/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.taglib;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.taglibs.util.CollectionLoopTagStatus;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.admin.util.NavigatorUtil;
import no.kantega.publishing.admin.AdminRequestParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints the content navigator menu.
 *
 * Takes a SiteMapEntry containing all items to show as input.
 * It also requires the id of the current page as input in order to set proper classes etc.
 */
public class PrintNavigatorTag extends SimpleTagSupport {

    private static final String SOURCE = "no.kantega.publishing.admin.taglib.PrintNavigatorTag";

    private SiteMapEntry site;
    private int currentId;

    private List<SiteMapEntry> menuitems = null;
    private int prevDepth = -1;
    private int nrul = 0;
    private CollectionLoopTagStatus status = null;

    private String selectedClass = "selected";
    private String openClass ="open";


    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public void setSite(SiteMapEntry site) {
        this.site = site;
    }


    private void addToSiteMap(SiteMapEntry sitemap, int currentDepth) {

        sitemap.setDepth(currentDepth);

        if (sitemap.getId() == currentId) {
            sitemap.setSelected(true);
        }

        menuitems.add(sitemap);


        List<SiteMapEntry> children = sitemap.getChildren();
        if (children != null) {
            for (SiteMapEntry child : children) {
                sitemap.setOpen(true);
                addToSiteMap(child, currentDepth + 1);
            }
        }
    }


    @Override
    public void doTag() throws JspException, IOException {
        menuitems = new ArrayList<SiteMapEntry>();

        try {
            HttpServletRequest request = (HttpServletRequest)((PageContext)getJspContext()).getRequest();

            if (site != null) {
                addToSiteMap(site, 0);
            }
            status = new CollectionLoopTagStatus(menuitems);

            while (status.getIndex() < status.getCount()) {
                printListElement();
                status.incrementIndex();
            }

        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        currentId = -1;
        site = null;
        menuitems = null;
        prevDepth = -1;
        nrul = 0;
     }

    /**
     * Prints the menu "plumbing", i.e. the list elements (ul/li) needed to mark up the menu as a tree.
     * @throws IOException
     */
    private void printListElement() throws IOException {
        SiteMapEntry currentItem = (SiteMapEntry)status.getCurrent();

        String ulStartElem = "<ul class=\"navigator\">";
        String ulEndElem = "</ul>\n";

        StringBuffer clz = new StringBuffer();

        if (selectedClass != null && currentItem.isSelected()) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(selectedClass);
        }
        if (openClass != null && currentItem.isOpen()) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(openClass);
        }

        JspWriter out = getJspContext().getOut();

        if (prevDepth == -1) {
            prevDepth = currentItem.getDepth();
            out.write(ulStartElem);
        }
        if (currentItem.getDepth() > prevDepth) {
            out.write(ulStartElem);
            nrul++;
        } else if (currentItem.getDepth() < prevDepth) {
            for (int i = prevDepth; i > currentItem.getDepth(); i--) {
                out.write("</li>\n");
                out.write(ulEndElem);
                nrul--;
            }
        } else if (currentItem.getDepth() == prevDepth && status.getIndex() > 0){
            out.write("</li>\n");
        }

        out.write("\t<li");
        if (clz.length() > 0) {
            out.write(" class=\"" + clz.toString() + "\"");
        }
        out.write(">\n");

        printBody(currentItem);

        prevDepth = currentItem.getDepth();

        if (status.isLast()) {
            for (int i = 0; i < nrul; i++) {
                out.write("</li>\n");
                out.write(ulEndElem);
            }
            out.write("</li>\n");
            out.write(ulEndElem);
        }
    }

    /**
     * Prints the contents of each menu item, i.e. what's inside the li-elements
     *
     * @param currentItem
     * @throws IOException
     */
    private void printBody(SiteMapEntry currentItem) throws IOException {
        JspWriter out = getJspContext().getOut();

        StringBuilder href = new StringBuilder();
        href.append("?");
        href.append(AdminRequestParameters.THIS_ID).append("=").append(currentItem.getId()).append("&amp;");
        href.append(AdminRequestParameters.CONTENT_ID).append("=").append(currentItem.getContentId());

        String openState = currentItem.isOpen()? "open": "closed";
        out.write("<span class=\"openState\"><a href=\"" + href + "\" class=\"" + openState + "\"></a></span>");

        ContentType type = currentItem.getType();
        String title = NavigatorUtil.getNavigatorTitle(type, currentItem.getTitle());

        String iconText = "";
        String iconClass = "";
        int visibilityStatus = currentItem.getVisibilityStatus();
        if (currentItem.getParentId() == 0) {
            iconClass = "root";
            iconText = title;
        } else {
            iconClass = NavigatorUtil.getIcon(type, visibilityStatus, currentItem.getStatus());
            iconText = NavigatorUtil.getIconText(type, visibilityStatus, currentItem.getStatus());
        }
        out.write("<span class=\"icon\"><a href=\"" + href + "\" class=\""+iconClass+"\" title=\""+iconText+"\"></a></span>");


        boolean isSelected = false;
        // Mark object (shortcuts will not be marked since you go directly to the object)
        if (currentItem.getId() == currentId && type != ContentType.SHORTCUT) {
            isSelected = true;
        }

        String titleClass = NavigatorUtil.getContextMenuType(type, visibilityStatus, currentItem.getStatus());
        if (isSelected) {
            titleClass += " selected";
        }
        out.write("<span class=\"title\"><a href=\""+ href +"\" class=\""+ titleClass +"\" title=\"" + title + "\">" + title +"</a></span>");

        
    }
}
