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

package no.kantega.publishing.api.taglibs.content.paginate;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

public class PaginateCollectionTag extends TagSupport {
    private static final String SOURCE = "aksess.PaginateCollectionTag";

    private String collection = null;
    private String prevlabel = "previous";
    private String nextlabel = "next";

    private String offsetparam = "offset";
    private int beforeandafterlinks = 3;
    private int resultsperpage = 25;
    private String ulclass = "paginate";
    private String prevclass = "prev";
    private String nextclass = "next";
    private String currentclass = "current";
    private String gapclass = "gap";

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setPrevlabel(String prevlabel) {
        this.prevlabel = prevlabel;
    }

    public void setNextlabel(String nextlabel) {
        this.nextlabel = nextlabel;
    }

    public void setOffsetparam(String offsetparam) {
        this.offsetparam = offsetparam;
    }

    public void setBeforeandafterlinks(int beforeandafterlinks) {
        this.beforeandafterlinks = beforeandafterlinks;
    }

    public void setResultsperpage(int resultsperpage) {
        this.resultsperpage = resultsperpage;
    }

    public void setGapclass(String gapclass) {
        this.gapclass = gapclass;
    }

    public void setUlclass(String ulclass) {
        this.ulclass = ulclass;
    }

    public void setPrevclass(String prevclass) {
        this.prevclass = prevclass;
    }

    public void setNextclass(String nextclass) {
        this.nextclass = nextclass;
    }

    public void setCurrentclass(String currentclass) {
        this.currentclass = currentclass;
    }

    public int doStartTag() throws JspException {
        try {
            RequestParameters param = new RequestParameters((HttpServletRequest)pageContext.getRequest());
            int offset = param.getInt(offsetparam);
            if (offset < 0) offset = 0;

            List contentPages = (List)pageContext.getAttribute("aksess_collection_items" + collection);
            if (contentPages != null) {
                Paginator paginator = new Paginator();
                int numberOfPages = (int)Math.ceil(((float)contentPages.size()) / ((float)resultsperpage));
                List<PaginatePage> pages = paginator.getPaginatedList(numberOfPages, offset, beforeandafterlinks);
                printPagination(pages, offset, numberOfPages);
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        return SKIP_BODY;
    }

    private void printPagination(List<PaginatePage> pages, int offset, int numberOfPages) throws IOException {
        JspWriter out = pageContext.getOut();
        out.write("<ul class=\"" + ulclass + "\">");
        if (offset > 0) {
            out.write("<li class=\"" + prevclass + "\">");
            out.write("<a href=\"?" + offsetparam + "=" + (offset - 1) + "\">" + prevlabel + "</a>");
            out.write("</li>");
        }

        for (PaginatePage page : pages) {
            if (page.isGap()) {
                out.write("<li class=\"" + gapclass + "\">...</li>");
            } else {
                if (page.isCurrentPage()) {
                    out.write("<li class=\"" + currentclass + "\">");
                } else {
                    out.write("<li>");
                }
                out.write("<a href=\"?" + offsetparam + "=" + (page.getPageNumber() - 1) + "\">" + page.getPageNumber() + "</a>");
                out.write("</li>");
            }
        }

        if (offset + 1 < numberOfPages) {
            out.write("<li class=\"" + nextclass + "\">");
            out.write("<a href=\"?" + offsetparam + "=" + (offset + 1) + "\">" + nextlabel + "</a>");
            out.write("</li>");
        }

        out.write("</ul>");
    }

    public int doEndTag() throws JspException {
        offsetparam = "offset";
        collection = null;
        prevlabel = "previous";
        nextlabel = "next";
        ulclass = "paginate";
        prevclass = "prev";
        nextclass = "next";
        currentclass = "current";
        gapclass = "gap";
        return EVAL_PAGE;
    }
}
