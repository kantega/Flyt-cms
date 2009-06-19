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
import java.io.File;
import java.io.IOException;

/**
 * @deprecated No replacement - Use CSS styles for mimetypes
 */
@Deprecated
public class MimeIconTag extends TagSupport {
    private static String SOURCE = "no.kantega.publishing.api.taglibs.util.MimeIconTag";
    private String mimetype = null;
    private String cssclass = null;
    private String alt = "";

    public final static String DEFAULT_MIME_DIR = "/aksess/bitmaps/mimetype/";

    
    public int doStartTag() throws JspException {
        String dir = DEFAULT_MIME_DIR;

        if(mimetype == null || mimetype.equals("")) {
            return SKIP_BODY;
        }

        String link;

        File f = new File(pageContext.getServletContext().getRealPath(DEFAULT_MIME_DIR + mimetype + ".gif"));
        if (!f.exists()) {
           link  = dir + "default.gif";
        } else {
            link = dir + mimetype +".gif";
        }

        JspWriter out = pageContext.getOut();

        try {
            out.write("<img src=\"" +Aksess.getContextPath() +link +"\" alt=\"" + alt + "\"");
            if(cssclass != null) {
                out.write(" class=\"" + cssclass + "\"");
            }
            out.write(">");
        } catch (IOException e) {
            throw new JspException("ERROR: MimeIconTag", e);
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getCssclass() {
        return cssclass;
    }

    public void setCssclass(String cssclass) {
        this.cssclass = cssclass;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
