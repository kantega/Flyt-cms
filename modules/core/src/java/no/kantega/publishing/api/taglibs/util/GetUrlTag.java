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

import no.kantega.commons.urlplaceholder.UrlPlaceholderResolver;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.taglibs.standard.tag.common.core.ImportSupport.isAbsoluteUrl;

public class GetUrlTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetUrlTag.class);

    String url = null;
    String queryParams = null;
    boolean addcontextpath = true;
    boolean absoluteUrl = false;
    boolean escapeurl = true;

    private static UrlPlaceholderResolver urlPlaceholderResolver;
    private static ContentIdHelper contentIdHelper;

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            if(isNotBlank(url)){
                initUrlPlaceholderResolverIfNull();
                StringBuilder urlBuilder = new StringBuilder();
                if(isAbsoluteUrl(url)){
                    urlBuilder.append(url);
                }else {
                    if(absoluteUrl){
                        addSchemeServerAndContextPath(urlBuilder, request);
                    }
                    if(addcontextpath && !absoluteUrl){
                        urlBuilder.append(request.getContextPath());
                    }
                    if (url.charAt(0) == '/'  || url.charAt(0) == '$') {
                        urlBuilder.append(url);
                    } else {
                        urlBuilder.append('/');
                        urlBuilder.append(url);
                    }
                }

                addSiteIdIfInAdminMode(urlBuilder, request);

                if (queryParams != null) {
                    if ((!queryParams.startsWith("&")) && (!queryParams.startsWith("?")) && (!queryParams.startsWith("#"))) {
                        if (urlBuilder.indexOf("?") == -1) {
                            urlBuilder.append("?");
                        } else {
                            urlBuilder.append("&amp;");
                        }
                    }
                    urlBuilder.append(queryParams.replaceAll("&", "&amp;"));
                }

                String buildUrl = urlPlaceholderResolver.replaceMacros(urlBuilder.toString(), pageContext);

                if (!escapeurl) {
                    out.write(buildUrl.replaceAll("&amp;", "&"));
                } else {
                    out.write(buildUrl);
                }

            } else {
                out.write(request.getContextPath());
            }

        } catch (Exception e){
            log.error("", e);
            throw new JspException(e);
        }

        resetVars();

        return SKIP_BODY;
    }

    private void addSiteIdIfInAdminMode(StringBuilder urlBuilder, HttpServletRequest request) {
        if (HttpHelper.isAdminMode(request)) {
            try {
                if(contentIdHelper == null){
                    contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                }
                ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, getUrlWithLeadingSlash(url));
                if (!url.contains("?")) {
                    urlBuilder.append("?siteId=").append(cid.getSiteId());
                } else {
                    urlBuilder.append("&siteId=").append(cid.getSiteId());
                }

            } catch (ContentNotFoundException e) {
                log.debug(e.getMessage());
            }
        }
    }

    private String getUrlWithLeadingSlash(String url) {
        if (url.charAt(0) == '/') {
            return url;
        } else {
            return "/" + url;
        }
    }

    private void addSchemeServerAndContextPath(StringBuilder urlBuilder, HttpServletRequest request) {
        urlBuilder.append(request.getScheme());
        urlBuilder.append("://");
        urlBuilder.append(request.getServerName());
        int serverPort = request.getServerPort();
        if(serverPort != 80 && serverPort != 443){
            urlBuilder.append(":");
            urlBuilder.append(serverPort);
        }
        urlBuilder.append(request.getContextPath());
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
        absoluteUrl = false;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }


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

    public void setAbsoluteUrl(boolean absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
    }
}
