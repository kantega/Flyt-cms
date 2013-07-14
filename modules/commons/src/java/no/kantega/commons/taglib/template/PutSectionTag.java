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

package no.kantega.commons.taglib.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Benyttes for å definere et område (section) som skal hentes ut senere
 */
public class PutSectionTag  extends BodyTagSupport {
    private String id = null;

    private static final Logger log = LoggerFactory.getLogger(PutSectionTag.class);

    public void setId(String id) {
        this.id = id;
    }

    public int doAfterBody() throws JspException {
        if (id != null) {
            String body = bodyContent.getString();
            if(body != null && body.length() > 1000000) {
                log.warn("Warning: body content in tag is larger than 2MB (" +body.length() +"), URL is: " + ((HttpServletRequest)pageContext.getRequest()).getRequestURI().toString());
            }
            pageContext.setAttribute("kantega_template_" + id, body, PageContext.REQUEST_SCOPE);
            bodyContent.clearBody();
        }
        return SKIP_BODY;
    }
}
