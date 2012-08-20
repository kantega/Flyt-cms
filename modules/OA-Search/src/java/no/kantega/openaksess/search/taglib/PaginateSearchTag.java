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

package no.kantega.openaksess.search.taglib;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.taglibs.content.paginate.PaginatePage;
import no.kantega.publishing.api.taglibs.content.paginate.Paginator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

public class PaginateSearchTag extends TagSupport{
    private static final String SOURCE = "aksess.PaginateSearchTag";

    private String searchPageUrlsVariableName = "searchPageUrls";

    private String prevlabel = "previous";
    private String nextlabel = "next";
    private String offsetparam = "offset";
    private int beforeandafterlinks = 3;

    public int doStartTag() throws JspException {
        try{
            RequestParameters param = new RequestParameters((HttpServletRequest)pageContext.getRequest());
            int offset = param.getInt(offsetparam);
            offset = offset < 0 ? offset = 0 : offset / 10; // Searchtag use number of hits as offset. page 35 with 10pr page is offset 350.
            List searchPageUrls = (List) pageContext.getRequest().getAttribute(searchPageUrlsVariableName);
            if (searchPageUrls != null) {
                paginate(offset, searchPageUrls);
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        return Tag.SKIP_BODY;
    }

    private void paginate(int offset, List<String> pagesList) throws IOException {
        Paginator paginator = new Paginator();
        int numberOfPages = pagesList.size();
        List<PaginatePage> pages = paginator.getPaginatedList(numberOfPages, offset, beforeandafterlinks);
        printPagination(pages, pagesList, offset);
    }

    public void printPagination(List<PaginatePage> paginatePages, List<String> urls, int offset) throws IOException {
        JspWriter out = pageContext.getOut();
        out.write("<ul class=\"paginate\">");
        if (offset > 0) {
            out.write("<li class=\"prev\">");
            out.write("<a href=\"" + urls.get(offset - 1) + "\">" + prevlabel + "</a>");
            out.write("</li>");
        }

        for (PaginatePage page : paginatePages) {
            if (page.isGap()) {
                out.write("<li class=\"gap\">...</li>");
            } else {
                if (page.isCurrentPage()) {
                    out.write("<li class=\"current\">");
                } else {
                    out.write("<li>");
                }
                out.write("<a href=\"" + urls.get(page.getPageNumber() - 1) + "\">" + page.getPageNumber() + "</a>");
                out.write("</li>");
            }
        }

        if (offset + 1 < paginatePages.size()) {
            out.write("<li class=\"next\">");
            out.write("<a href=\"" + urls.get(offset + 1) + "\">" + nextlabel + "</a>");
            out.write("</li>");
        }

        out.write("</ul>");
    }

    public int doEndTag() throws JspException {
        searchPageUrlsVariableName = "searchPageUrls";

        prevlabel = "previous";
        nextlabel = "next";
        offsetparam = "offset";
        beforeandafterlinks = 3;

        return Tag.EVAL_PAGE;
    }

    public void setSearchPageUrlsVariableName(String searchPageUrlsVariableName) {
        this.searchPageUrlsVariableName = searchPageUrlsVariableName;
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
}
