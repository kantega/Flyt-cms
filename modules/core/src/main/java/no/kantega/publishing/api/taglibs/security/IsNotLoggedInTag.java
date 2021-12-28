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

package no.kantega.publishing.api.taglibs.security;

import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class IsNotLoggedInTag extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(IsNotLoggedInTag.class);

    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {

        try {
            SecuritySession session = SecuritySession.getInstance((HttpServletRequest) pageContext.getRequest());

            if (!session.isLoggedIn()) {
                bodyContent.writeOut(getPreviousOut());
            }
        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(e);
        } finally {
            bodyContent.clearBody();
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }
}
