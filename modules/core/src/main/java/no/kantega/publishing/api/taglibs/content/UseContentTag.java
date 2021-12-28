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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import static no.kantega.publishing.api.ContentUtil.tryGetFromPageContext;

/**
 *  Set specified content as current page
 */
public class UseContentTag  extends ConditionalTagSupport {
    private static final Logger log = LoggerFactory.getLogger(UseContentTag.class);
    private String contentId = null;
    private String collection = null;
    private Content contentObject = null;
    private Content originalContent = null;

    public void setCollection(String collection) {
        if (collection != null && collection.length() == 0) {
            collection = null;
        }
        this.collection = collection;
    }

    public void setContentid(String contentId) {
        if (contentId != null && contentId.length() == 0) {
            contentId = null;
        }
        this.contentId = contentId;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }

    protected boolean condition() throws JspTagException {
        originalContent = tryGetFromPageContext(pageContext);

        if (contentObject == null && (collection != null || contentId != null)) {
            try {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId);
            } catch (NotAuthorizedException e) {
                log.error("", e);
                throw new JspTagException(getClass().getName() + ":" + e.getMessage());

            }
        }
        if (contentObject != null) {
            RequestHelper.setRequestAttributes((HttpServletRequest)pageContext.getRequest(), contentObject);
            return true;
        } else {
            return false;
        }
    }

    public int doEndTag() throws JspException {
        if (originalContent != null) {
            RequestHelper.setRequestAttributes((HttpServletRequest)pageContext.getRequest(), originalContent);
        }

        contentId = null;
        collection = null;
        contentObject = null;
        originalContent = null;

        return super.doEndTag();
    }





}
