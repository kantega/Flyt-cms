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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.publishing.common.Aksess;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class GetDateTag extends TagSupport {
    String format = null;

    public void setFormat(String format) {
        this.format = format;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();
            Date today = new Date();

            if (format == null) {
                format = Aksess.getDefaultDateFormat();
            }

            DateFormat df = new SimpleDateFormat(format);
            out.write(df.format(today));
        } catch (IOException e) {
            throw new JspException("ERROR: GetDateTag", e);
        }

        format = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

}
