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

package no.kantega.publishing.admin.taglib;

import no.kantega.commons.util.LocaleLabels;
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
public class FormatTimeTag extends TagSupport {
    Date date = null;

    public void setDate(Date date) {
        this.date = date;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            String dateFormat = Aksess.getDefaultTimeFormat();

            if (date == null) {
                String defaultDate = dateFormat;
                defaultDate = defaultDate.replaceAll("H", LocaleLabels.getLabel("aksess.dateformat.character.hour", Aksess.getDefaultAdminLocale()));
                defaultDate = defaultDate.replaceAll("m", LocaleLabels.getLabel("aksess.dateformat.character.minute", Aksess.getDefaultAdminLocale()));
                out.write(defaultDate);
            } else {
                DateFormat df = new SimpleDateFormat(dateFormat);
                out.write(df.format(date));
            }

        } catch (IOException e) {
            throw new JspException("ERROR: FormatTimeTag", e);
        }

        date = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

}

