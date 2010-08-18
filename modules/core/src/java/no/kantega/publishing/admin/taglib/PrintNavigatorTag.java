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
import no.kantega.publishing.common.data.NavigationMapEntry;

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
 * Abstract base class to print navigator menu.
 *
 * Takes a NaviagtionMapEntry containing all items to show as input.
 * It also requires the id of the current page as input in order to set proper classes etc.
 */
public abstract class PrintNavigatorTag extends SimpleTagSupport {

    private static final String SOURCE = "no.kantega.publishing.admin.taglib.PrintNavigatorTag";

    private NavigationMapEntry site;
    private int currentId;
    private int startId = -1;

    private List<NavigationMapEntry> menuitems = null;
    private int prevDepth = -1;
    private int nrul = 0;
    private CollectionLoopTagStatus status = null;

    private String selectedClass = "selected";
    private String openClass ="open";

    /**
     * Prints the contents of each menu item, i.e. what's inside the li-elements
     *
     * @param item
     * @throws IOException
     */
    protected abstract void printBody(NavigationMapEntry item) throws IOException;

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public int getCurrentId() {
        return currentId;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public void setRoot(NavigationMapEntry site) {
        this.site = site;
    }

    private void addToSiteMap(NavigationMapEntry sitemap, int currentDepth, boolean doDisplay) {

        sitemap.setDepth(currentDepth);

        if (sitemap.getId() == currentId) {
            sitemap.setSelected(true);
        }

        if (sitemap.getId() == startId && !doDisplay) {
            doDisplay = true;
        }

        if (doDisplay) {
            menuitems.add(sitemap);
        }

        List<NavigationMapEntry> children = sitemap.getChildren();
        if (children != null) {
            for (NavigationMapEntry child : children) {
                sitemap.setOpen(true);
                addToSiteMap(child, currentDepth + 1, doDisplay);
            }
        }
    }


    @Override
    public void doTag() throws JspException, IOException {
        menuitems = new ArrayList<NavigationMapEntry>();

        try {
            if (site != null) {
                boolean doDisplay = startId == -1;
                addToSiteMap(site, 0, doDisplay);
            }
            status = new CollectionLoopTagStatus(menuitems);

            while (status.getIndex() < status.getCount()) {
                printListElement();
                status.incrementIndex();
            }

        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        currentId = -1;
        startId = -1;
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
        NavigationMapEntry currentItem = (NavigationMapEntry)status.getCurrent();

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
}