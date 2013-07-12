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

import no.kantega.commons.log.Log;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;

/**
 * Gets collection from database, shows body if collection not empty
 *
 * User: Anders Skar, Kantega AS
 * Date: Feb 21, 2007
 * Time: 10:02:04 AM
 */

public class IfCollectionNotEmptyTag extends AbstractGetCollectionTag {
    private static final String SOURCE = "aksess.IfCollectionNotEmptyTag";

    public int doStartTag() throws JspException {
        try {
            List collection = getCollection(pageContext);
            pageContext.setAttribute("aksess_collection_items" + name, collection);

            if (collection == null || collection.size() == 0) {
                return SKIP_BODY;
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE, e);
        }

        return EVAL_BODY_TAG;
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

        return SKIP_BODY;
    }


    /**
     * Cleanup after tag is finished
     * @return EVAL_PAGE
     */
    public int doEndTag() {
        int ret = super.doEndTag();
        if (name != null) {
            pageContext.removeAttribute(name);
        }

        return ret;
    }
}