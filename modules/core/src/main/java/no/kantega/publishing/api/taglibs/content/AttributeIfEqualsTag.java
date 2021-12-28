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

import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class AttributeIfEqualsTag  extends ConditionalTagSupport {
    private static final Logger log = LoggerFactory.getLogger(AttributeIfEqualsTag.class);

    private String name = null;
    private String value = null;
    private String contentId = null;
    private String collection = null;
    private boolean negate = false;

    private boolean inheritFromAncestors = false;

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public void setInheritfromancestors(boolean inheritFromAncestors) {
        this.inheritFromAncestors = inheritFromAncestors;
    }

    protected boolean condition()  {
        try {
            Content content = AttributeTagHelper.getContent(pageContext, collection, contentId);
            GetAttributeCommand cmd = new GetAttributeCommand();
            cmd.setName(name);
            cmd.setProperty(AttributeProperty.VALUE);

            SecuritySession session = SecuritySession.getInstance((HttpServletRequest) pageContext.getRequest());

            String result = AttributeTagHelper.getAttribute(session, content, cmd, inheritFromAncestors);
            if (result != null && result.equalsIgnoreCase(value)) {
                return (!negate);
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return negate;
    }

    public int doEndTag() throws JspException  {
        name = null;
        value = null;
        contentId = null;
        collection = null;
        negate = false;
        inheritFromAncestors = false;

        return super.doEndTag();
    }
}

