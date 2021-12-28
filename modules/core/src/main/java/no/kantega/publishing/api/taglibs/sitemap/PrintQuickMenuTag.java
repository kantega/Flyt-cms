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

import no.kantega.publishing.common.data.SiteMapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

public class PrintQuickMenuTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(PrintQuickMenuTag.class);

    private String name = "sitemap";
    private String hasChildrenCssClass = "";
    private String noChildrenCssClass = "";
    private String activeCssClass = "";
    private int activePageId = -1;
    private int currentPageId = -1;

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        SiteMapEntry sitemap = (SiteMapEntry) request.getAttribute(name);
        JspWriter out = pageContext.getOut();

        if(sitemap != null){
            currentPageId = sitemap.getContentId();
            try {
                printQuickMenu(sitemap, 0, out);
            } catch (IOException e) {
                log.error("", e);
            }
        }
        return SKIP_BODY;
    }


    private void printQuickMenu(SiteMapEntry sitemap, int level, JspWriter out) throws IOException {
        if (sitemap != null) {
			List children = sitemap.getChildren();
            if(level > 0){
                String title = sitemap.getTitle();
                String url = sitemap.getUrl();
                if (children!=null && !"".equals(hasChildrenCssClass)){
                    out.write("<li class=\""+hasChildrenCssClass+"\">");
                }
                else{
                    if ("".equals(noChildrenCssClass)){
                        out.write("<li>");
                    }
                    else{
                        out.write("<li class=\""+noChildrenCssClass+"\">");   
                    }
                }
                if(sitemap.isAncestorFor(activePageId) || sitemap.getContentId()==activePageId){
                    out.write("<a href=\""+url+"\"class=\""+activeCssClass+"\">" + title + "</a>\n");
                }else{
                    out.write("<a href=\""+url+"\">" + title + "</a>\n");    
                }
            }

            if (children != null) {
                if (level > 0)
                    out.write("<ul>\n");
                for (int i = 0; i < children.size(); i++) {
                    printQuickMenu((SiteMapEntry)children.get(i), level+1, out);
                }
                if (level > 0)
                    out.write("</ul>\n");

            }
            out.write("</li>\n");
        }
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setHaschildrencssclass(String cssClass){
        hasChildrenCssClass = cssClass;
    }

    public void setNochildrencssclass(String cssClass){
        noChildrenCssClass = cssClass;
    }

    public void setActivepagecssclass(String activeCssClass) {
        this.activeCssClass = activeCssClass;
    }

    public void setActivepageid(int activePageId) {
        this.activePageId = activePageId;
    }
}
