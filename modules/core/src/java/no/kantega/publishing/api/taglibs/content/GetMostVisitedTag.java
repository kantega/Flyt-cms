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

package no.kantega.publishing.api.taglibs.content;

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.TrafficStatisticsService;
import no.kantega.commons.log.Log;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;

public class GetMostVisitedTag  extends BodyTagSupport {
    private static final String SOURCE = "aksess.GetMostVisitedTag";

    private String name = null;
    private String documentType = null;
    private int minDepth = 0;
    private int max = 10;

    private int siteId = -1;

    private List collection = null;
    private int offset = 0;

    public void setName(String name) {
        this.name = name;
    }

    public void setDocumenttype(String documentType) {
        this.documentType = documentType;
    }

    public void setSite(String site) {
        this.siteId = Integer.parseInt(site);
    }

    public void setMindepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        ContentQuery query = new ContentQuery();


        try {
            ContentManagementService cs = new ContentManagementService(request);

            collection = cs.getContentSummaryList(query, max, new SortOrder(ContentProperty.NUMBER_OF_VIEWS, true));

            pageContext.setAttribute("aksess_collection_size" + name, new Integer(collection.size()));
        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }
        return doIter();
    }

    private int doIter() {
        int size = collection.size();
        if (offset < size) {
            pageContext.setAttribute("aksess_collection_" + name, (Content)collection.get(offset));
            pageContext.setAttribute("aksess_collection_offset" + name, new Integer(offset));
            offset++;
            return EVAL_BODY_TAG;
        } else {
            pageContext.removeAttribute("aksess_collection_" + name);
            pageContext.removeAttribute("aksess_collection_size" + name);
            pageContext.removeAttribute("aksess_collection_offset" + name);
            name = null;
            documentType = null;

            collection = null;
            offset = 0;

            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        try {
           bodyContent.writeOut(getPreviousOut());
        } catch (IOException e) {
           throw new JspTagException("GetCollectionTag: " + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }
        return doIter();
    }
}
