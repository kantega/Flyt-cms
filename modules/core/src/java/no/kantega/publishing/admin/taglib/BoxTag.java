/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.admin.taglib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class BoxTag extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(BoxTag.class);
    public int doStartTag()  throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        try {
            String body = bodyContent.getString();
            JspWriter out = bodyContent.getEnclosingWriter();

            out.print("<div class=\"roundCorners\"><div class=\"top\"><div class=\"corner\"></div></div><div class=\"body\"><div class=\"left\"><div class=\"right\">");
            if(body != null) {
               out.print(body);
            }
            out.print("</div></div></div><div class=\"bottom\"><div class=\"corner\"></div></div></div>");

        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(this.getClass().getName() + ":" + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }


        return SKIP_BODY;
     }
}
