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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.data.ContentIdentifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 *
 */
public class GetUrlTag extends TagSupport {
    private static String SOURCE = "aksess.GetUrlTag";

    String url = null;

    public void setUrl(String url) {
        this.url = url;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            url = AttributeTagHelper.replaceMacros(url, pageContext);

            String absoluteurl = Aksess.getContextPath();
            if (url != null && url.length() > 0) {
                if (url.indexOf("http:") != -1 || url.indexOf("https:") != -1 ) {
                    absoluteurl = url;
                } else {
                    if (url.charAt(0) == '/') {
                        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

                        // Hvis adminmodus, legg til siteid på link
                        if (HttpHelper.isAdminMode(request)) {
                            try {
                                ContentIdentifier cid = new ContentIdentifier(request, url);
                                if (url.indexOf("?") == -1) {
                                    url = url + "?siteId=" + cid.getSiteId();
                                } else {
                                    url = url + "&amp;siteId=" + cid.getSiteId();
                                }

                            } catch (ContentNotFoundException e) {

                            }
                        }

                        absoluteurl += url;
                    } else {
                        absoluteurl += "/" + url;
                    }
                }

            }

            out.write(absoluteurl);

        } catch (IOException e) {
            throw new JspException("ERROR: GetUrlTag", e);
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
            throw new JspException("ERROR: GetUrlTag", e);
        } catch (NotAuthorizedException e) {
            // Do nothing
        }

        url = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

}
