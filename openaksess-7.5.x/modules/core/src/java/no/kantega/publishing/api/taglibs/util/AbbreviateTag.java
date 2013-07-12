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

import no.kantega.commons.util.StringHelper;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * Author: Kristian Lier Selnæs, Kantega
 * Date: 05.jan.2007
 * Time: 12:15:21
 *
 * <br><br>
 * Tar strengen som ligger i taggens body og forkorter den til det antall tegn som er
 * spesifisert i maxsize. Legger på ... etter den forkortede strengen.
 * <br><br>
 * Eksempler: Tekst "abcdefghijklmno"
 * <br><br>
 * leftedge er -1 og maxsize er 10 = "abcdefg..."<br>
 * leftedge er 0 og maxsize er 10 = "abcdefg..."<br>
 * leftedge er 1 og maxsize er 10 = "abcdefg..."<br>
 * leftedge er 4 og maxsize er 10 = "abcdefg..."<br>
 * leftedge er 5 og maxsize er 10 = "...fghi..."<br>
 * leftedge er 6 og maxsize er 10 = "...ghij..."<br>
 * leftedge er 8 og maxsize er 10 = "...ijklmno"<br>
 * leftedge er 10 og maxsize er 10 = "...ijklmno"<br>
 * leftedge er 12 og maxsize er 10 = "...ijklmno"<br>
 * leftedge er 0 og mazSize er 3 = ""<br>
 * leftedge er 5 og mazSize er 6 = ""<br>
 */
public class AbbreviateTag extends BodyTagSupport {
    private static final String SOURCE = "aksess.AbbreviateTag";
    private String text = "";
    private int leftedge = 0;
    private int maxsize = 4;


    public void setLeftedge(int leftedge) {
        this.leftedge = leftedge;
    }

    public void setMaxsize(int maxsize) {
        this.maxsize = maxsize;
    }

    /**
     * Endrer innholde i body
     * @return
     * @throws JspException
     */
    public int doAfterBody() throws JspException {

        text = bodyContent.getString();
        if (text == null) {
            text = "";
        }
        text = StringHelper.stripHtml(text);
        if(text.length() > maxsize) {
            try {
                text = StringUtils.abbreviate(text, leftedge, maxsize);
            } catch (IllegalArgumentException iae) {
                text = "";
            }
        }

        leftedge = 0;
        maxsize = 0;

        return SKIP_BODY;

    }




    /**
     * Rendrer endret body.
     *
     * @return
     * @throws JspException
     */
    public int doEndTag() throws JspException {

        try {
            JspWriter out = pageContext.getOut();
            out.print(text);
        } catch (IOException ioe) {
            throw new JspException(ioe.toString());
        }
        return EVAL_PAGE;

    }


}

