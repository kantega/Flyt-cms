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

import no.kantega.commons.log.Log;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Benyttes for å hente ut en section som er lagret med put tidligere
 */
public class HasSectionTag  extends BodyTagSupport {
    private String id = null;
    private String negate = null;

    public void setId(String id) {
        this.id = id;
    }

    public void setNegate(String negate) {
        this.negate = negate;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_TAG;
    }


    public int doAfterBody() throws JspException {
        boolean hasSection = pageContext.getAttribute("kantega_template_" + id, PageContext.REQUEST_SCOPE) != null;
        boolean negate = this.negate != null && this.negate.equals("true");

        try {
            if((hasSection && ! negate) || (!hasSection && negate)) {
                bodyContent.writeOut(getPreviousOut());
            }
        } catch (Exception e) {
            System.err.println(e);
            Log.error("ERROR", e, null, null);
            throw new JspTagException("ERROR" + ":" + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }



        return SKIP_BODY;
    }




}
