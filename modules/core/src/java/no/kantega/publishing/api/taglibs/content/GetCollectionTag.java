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

import no.kantega.publishing.api.taglibs.util.CollectionLoopTagStatus;
import no.kantega.publishing.common.data.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;

/**
 * Henter en liste med innholdselement (sider) og looper gjennom dem
 */
public class GetCollectionTag extends AbstractGetCollectionTag {
    private static final Logger log = LoggerFactory.getLogger(GetCollectionTag.class);

    protected CollectionLoopTagStatus status = null;

    protected String var = null;

    protected String varStatus = "status";


    public int doStartTag() throws JspException {
        try {
            List collection = (List)pageContext.getAttribute("aksess_collection_items" + name);
            if (collection == null) {
                collection = getCollection(pageContext);
            }

            if (collection == null) {
                return SKIP_BODY;
            }

            status = new CollectionLoopTagStatus(collection);

            pageContext.setAttribute("aksess_collection_size" + name, collection.size());

        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(e);
        }
        return doIter();
    }

    private int doIter() {
        if (status.getIndex() < status.getCount()) {
            pageContext.setAttribute("aksess_collection_" + name, (Content)status.getCurrent());
            pageContext.setAttribute("aksess_collection_offset" + name, status.getIndex());


            // Current status
            if (varStatus != null) {
                pageContext.setAttribute(varStatus, status);
            }

            // Current content object
            if (var != null) {
                pageContext.setAttribute(var, status.getCurrent());
            }

            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }


    /**
     * Writes content of tag, returns status to iterate
     * @return
     * @throws JspException
     */
    public int doAfterBody() throws JspException {
        try {
            bodyContent.writeOut(getPreviousOut());
        } catch (IOException e) {
            throw new JspTagException("GetCollectionTag: " + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }

        status.incrementIndex();
        return doIter();
    }


    /**
     * Cleanup after tag is finished
     * @return EVAL_PAGE
     */
    public int doEndTag() {
        int ret = super.doEndTag();
        if (varStatus != null) {
            pageContext.removeAttribute(varStatus);
        }

        if (var != null) {
            pageContext.removeAttribute(var);
        }

        return ret;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setVarStatus(String varStatus) {
        this.varStatus = varStatus;
    }

}
