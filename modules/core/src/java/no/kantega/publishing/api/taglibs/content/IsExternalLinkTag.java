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

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.commons.log.Log;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IsExternalLinkTag extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.IsExternalLinkTag";

    private String contentId = null;
    private String collection = null;
    private boolean negate = false;

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    protected boolean condition() {
        try {
            Content content = AttributeTagHelper.getContent(pageContext, collection, contentId);
            if (content != null) {
                boolean isExternal = content.isExternalLink();
                if((isExternal && ! negate) || (!isExternal && negate)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
        }

        return false;
    }

    public int doEndTag() throws JspException {
        contentId  = null;
        collection = null;
        negate = false;

        return super.doEndTag();
    }
}

