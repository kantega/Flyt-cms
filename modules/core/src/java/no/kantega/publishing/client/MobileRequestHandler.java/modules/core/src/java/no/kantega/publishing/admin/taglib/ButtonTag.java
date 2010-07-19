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

import no.kantega.publishing.common.Aksess;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Locale;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 1, 2008
 * Time: 9:53:35 AM
 */
public class ButtonTag  extends TagSupport {
    String href = null;
    String button = null;
    String title = null;
    String tabindex = null;

    public void setHref(String href) {
        this.href = href;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            Locale locale = Aksess.getDefaultAdminLocale();
            String language = locale.getLanguage().toLowerCase();

            out = pageContext.getOut();

            out.write("<a href=\"" + href + "\"");
            if (tabindex != null) {
                out.write(" tabindex=\"" + tabindex + "\"");
            }
            out.write(">");
            out.write("<img src=\"" + Aksess.getContextPath() + "/admin/buttons/default/" + language + "/" + button + ".gif\" alt=\"" + title + "\" title=\"" + title + "\" border=\"0\">");
            out.write("</a>");
        } catch (IOException e) {
            throw new JspException("ERROR: ButtonTag", e);
        }

        href = null;
        button = null;
        title = null;
        tabindex = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

}
