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

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * Benyttes for å hente ut en section som er lagret med put tidligere
 */
public class GetSectionTag  extends TagSupport {
    private String id = null;

    public void setId(String id) {
        this.id = id;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();
            if (id != null) {
                String body = (String)pageContext.getAttribute("kantega_template_" + id, PageContext.REQUEST_SCOPE);
                if (body != null) {
                    out.print(body);
                }
            }
        } catch (IOException e) {
            throw new JspException("ERROR: GetSectionTag", e);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }
}
