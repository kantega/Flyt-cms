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

import no.kantega.publishing.common.data.SiteMapEntry;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class PrintListMenuTag extends AbstractMenuTag {

    private String liClass = null;
    private String ulClass = null;
    private String selectedClass = null;
    private String openClass = null;
    private String depthClass = null;
    private String firstChildClass = null;
    private String lastChildClass = null;

    private int prevDepth = -1;
    private int nrul = 0;


    public void setLiclass(String liClass) {
        this.liClass = liClass;
    }

    public void setUlclass(String ulClass) {
        this.ulClass = ulClass;
    }

    public void setSelectedclass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public void setOpenclass(String openClass) {
        this.openClass = openClass;
    }

    public void setDepthclass(String depthClass) {
        this.depthClass = depthClass;
    }

    public void setFirstchildclass(String firstClass) {
        this.firstChildClass = firstClass;
    }

    public void setLastchildclass(String lastClass) {
        this.lastChildClass = lastClass;
    }

    protected void printBody() throws IOException {
        SiteMapEntry currentItem = (SiteMapEntry)status.getCurrent();

        String ulStartElem = "<ul";
        if (ulClass != null) {
            ulStartElem += " class=\"" + ulClass + "\"";
        }
        ulStartElem += ">\n";
        String ulEndElem = "</ul>\n";

        StringBuffer clz = new StringBuffer();
        if (liClass != null) {
            clz.append(liClass);
        }
        if (depthClass != null) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(depthClass).append(currentItem.getDepth());
        }
        if (selectedClass != null && currentItem.isSelected()) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(selectedClass);
        }
        if (openClass != null && currentItem.isOpen()) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(openClass);
        }

        String body = bodyContent.getString();
        JspWriter out = bodyContent.getEnclosingWriter();

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

        if (firstChildClass != null && currentItem.isFirstChild()) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(firstChildClass);
        }

        if (lastChildClass != null && currentItem.isLastChild()) {
            if (clz.length()  > 0) clz.append(" ");
            clz.append(lastChildClass);
        }


        out.write("\t<li");
        if (clz.length() > 0) {
            out.write(" class=\"" + clz.toString() + "\"");
        }
        out.write(">\n");
        if (body != null){
            out.write(body);
        }

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


    public void reset(){
        prevDepth = -1;
        nrul = 0;

        liClass = null;
        ulClass = null;
        selectedClass = null;
        openClass = null;
        depthClass = null;
    }
}
