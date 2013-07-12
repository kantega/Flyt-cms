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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IsLoggedInTag extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.IsLoggedInTag";

    protected boolean condition()  {
        HttpServletRequest  request  = (HttpServletRequest)pageContext.getRequest();
        SecuritySession session = null;
        try {
            session = SecuritySession.getInstance(request);
            if (session.isLoggedIn()) {
                return true;
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }

        return false;
    }

    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
}
