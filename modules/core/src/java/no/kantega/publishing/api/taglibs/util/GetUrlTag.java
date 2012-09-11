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

import no.kantega.commons.log.Log;
import no.kantega.commons.urlplaceholder.UrlPlaceholderResolver;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class GetUrlTag extends TagSupport {
    private static String SOURCE = "aksess.GetUrlTag";

    String url = null;
    String queryParams = null;
    boolean addcontextpath = true;
    boolean escapeurl = true;

    private UrlPlaceholderResolver urlPlaceholderResolver;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setQueryparams(String queryParams) {
        this.queryParams = queryParams;
    }

    public void setAddcontextpath(boolean addcontextpath){
       this.addcontextpath = addcontextpath;
    }

    public void setEscapeurl(boolean escapeurl) {
        this.escapeurl = escapeurl;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            initUrlPlaceholderResolverIfNull();

            url = urlPlaceholderResolver.replaceMacros(url, pageContext);

            String absoluteurl = addcontextpath ? Aksess.getContextPath(): "";

            if (url != null && url.length() > 0) {
                if (url.contains("http:") || url.contains("https:")) {
                    absoluteurl = url;
                } else {
                    if (url.charAt(0) == '/') {
                        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

                        // Hvis adminmodus, legg til siteid på link
                        if (HttpHelper.isAdminMode(request)) {
                            try {
                                ContentIdentifier cid = new ContentIdentifier(request, url);
                                if (!url.contains("?")) {
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

            if (queryParams != null) {
                if ((!queryParams.startsWith("&")) && (!queryParams.startsWith("?")) && (!queryParams.startsWith("#"))) {
                    if (!absoluteurl.contains("?")) {
                        queryParams = "?" + queryParams;
                    } else {
                        queryParams = "&" + queryParams;
                    }
                }
                queryParams = queryParams.replaceAll("&", "&amp;");
                absoluteurl = absoluteurl + queryParams;
            }

            if (!escapeurl) {
                absoluteurl = absoluteurl.replaceAll("&amp;", "&");
            }

            out.write(absoluteurl);

        } catch (Exception e){
            Log.error(SOURCE, e);
            throw new JspException("ERROR: GetUrlTag", e);
        }

        resetVars();

        return SKIP_BODY;
    }

    private void initUrlPlaceholderResolverIfNull() {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        urlPlaceholderResolver = context.getBean(UrlPlaceholderResolver.class);
    }

    private void resetVars() {
        url = null;
        queryParams = null;
        escapeurl = true;
        addcontextpath = true;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }
}
