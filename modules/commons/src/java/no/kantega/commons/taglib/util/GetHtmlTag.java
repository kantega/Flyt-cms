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

package no.kantega.commons.taglib.util;

import no.kantega.commons.log.Log;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * User: Espen Høe / Kantega AS
 * Date: 03.sep.2007
 * Time: 13:20:17
 */
public class GetHtmlTag extends TagSupport {

    private String url;
    private static String SOURCE = "aksess.GetHtmlTag";


    public int doStartTag() throws JspException {
        HttpClient client = new HttpClient();
        try {
            GetMethod m = new GetMethod(url);
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            m.setRequestHeader("User-Agent", request.getHeader("User-Agent"));

            client.executeMethod(m);
            String html = m.getResponseBodyAsString();
            pageContext.getOut().write(html);
        } catch (IOException e) {
            Log.error(SOURCE, e, null, null);
        } catch (IllegalArgumentException e) {
            // failure to parse url, shouldn't take down the page
            Log.error(SOURCE, e, null, null);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }



    public void setUrl(String url) {
        this.url = url;
    }
}
