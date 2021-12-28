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

import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.SiteMapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PrintSiteMapTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(PrintSiteMapTag.class);

    private DateFormat dateFormat;

    private String name = "sitemap";
    private boolean crawlerSiteMap = false;


    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        SiteMapEntry sitemap = (SiteMapEntry) request.getAttribute(name);
        JspWriter out = pageContext.getOut();

        if(sitemap != null){
            try {
                if (crawlerSiteMap) {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    printCrawlerSiteMap(sitemap, 0, out);
                } else {
                    printSiteMap(sitemap, 0, out);
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }

        // Reset vars
        crawlerSiteMap = false;

        return SKIP_BODY;
    }


    private void printSiteMap(SiteMapEntry sitemap, int level, JspWriter out) throws IOException {
        if (sitemap != null) {
            if(level > 0){
                String url = sitemap.getUrl();
                String title = sitemap.getTitle();

                out.write("<li><a class=sidekart" + level + " href=\"" + url + "\">" + title + "</a>\n");
            }
            List children = sitemap.getChildren();
            if (children != null) {
                out.write("<ul>\n");
                for (int i = 0; i < children.size(); i++) {
                    printSiteMap((SiteMapEntry)children.get(i), level+1, out);
                }
                out.write("</ul>\n");

            }
            if (level > 0) {
                out.write("</li>\n");
            }
        }
    }


    private void printCrawlerSiteMap(SiteMapEntry sitemap, int level, JspWriter out) throws IOException {
        if (sitemap != null) {
            if (level == 0) {
                out.write("\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n");
                out.write("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
                out.write("    xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9\n");
                out.write("    http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n\n");
            } else if (level > 0) {
                String absUrl;
                if (sitemap.getAlias() != null) {
                    absUrl = URLHelper.getServerURL((HttpServletRequest)pageContext.getRequest()) + Aksess.getContextPath() + sitemap.getAlias();
                } else {
                    absUrl = URLHelper.getServerURL((HttpServletRequest)pageContext.getRequest()) + sitemap.getUrl();
                }
                Date lastModified = sitemap.getLastModified();
                String changefreq = "weekly";
                String priority = (1-level/10d) + "";

                out.write("  <url>\n");
                out.write("    <loc>" + absUrl + "</loc>\n");
                if (lastModified != null) {
                    out.write("    <lastmod>" + dateFormat.format(lastModified) + "</lastmod>\n");
                }
                out.write("    <changefreq>" + changefreq + "</changefreq>\n");
                out.write("    <priority>" + priority + "</priority>\n");
                out.write("  </url>\n");
            }

            List children = sitemap.getChildren();
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    SiteMapEntry entry = (SiteMapEntry)children.get(i);
                    if (entry.isSearchable()) {
                        printCrawlerSiteMap(entry, level+1, out);
                    }                    
                }
            }
            if (level == 0) {
                out.write("</urlset>\n");
            }
        }
    }


    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setCrawlerSiteMap(boolean crawlerSiteMap) {
        this.crawlerSiteMap = crawlerSiteMap;
    }

}
