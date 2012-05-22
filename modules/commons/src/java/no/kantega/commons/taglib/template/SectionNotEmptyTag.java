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
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: May 5, 2007
 * Time: 1:15:35 PM
 */
public class SectionNotEmptyTag extends BodyTagSupport {

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
        String sectionBody = (String) pageContext.getAttribute("kantega_template_" + id, PageContext.REQUEST_SCOPE);
        boolean hasSection = sectionBody != null;
        boolean negate = this.negate != null && this.negate.equals("true");
        boolean hasContent = hasSection && !sectionBody.trim().equals("");

        try {

            if((hasSection && hasContent && !negate) || (!hasSection && negate) || (!hasContent && negate)) {
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
