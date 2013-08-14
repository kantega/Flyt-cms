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

import no.kantega.commons.util.HttpHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.JspTagException;


public class IsAdminModeTag extends ConditionalTagSupport {
    private boolean negate = false;

    protected boolean condition() throws JspTagException {
        boolean ret;
        if (negate) {
            ret = (!HttpHelper.isAdminMode((HttpServletRequest)pageContext.getRequest()));
        } else {
            ret = HttpHelper.isAdminMode((HttpServletRequest)pageContext.getRequest());
        }
        negate = false;
        return ret;
    }

    public void setNegate(String negate) {
        this.negate = Boolean.valueOf(negate);
    }


}