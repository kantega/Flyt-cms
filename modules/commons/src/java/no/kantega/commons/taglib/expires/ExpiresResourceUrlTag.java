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

package no.kantega.commons.taglib.expires;

import no.kantega.commons.urlplaceholder.UrlPlaceholderResolver;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Generates an unique url with ResourceKeyProvider.
 */
public class ExpiresResourceUrlTag extends TagSupport {

    private UrlPlaceholderResolver urlPlaceholderResolver;

    private ResourceKeyProvider provider;

    private boolean includeContextPath = true;

    private String url;

    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        initIfNecessary(pageContext);
        String resolvedUrl = urlPlaceholderResolver.replaceMacros(url, pageContext);
        try {
            StringBuilder expireUrl = new StringBuilder();
            addContextPathIfSpecified(request, expireUrl);
            expireUrl.append("/expires/");
            expireUrl.append(provider.getUniqueKey(request, resolvedUrl));
            expireUrl.append(resolvedUrl);

            pageContext.getOut().write(expireUrl.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    private void addContextPathIfSpecified(HttpServletRequest request, StringBuilder expireUrl) {
        if (includeContextPath) {
            expireUrl.append(request.getContextPath());
        }
    }

    private void initIfNecessary(PageContext pageContext) {
        if (provider == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            try {
                provider = context.getBean(ResourceKeyProvider.class);
            } catch (BeansException e) {
                provider = new DefaultExpiresResourceKeyProvider();
            }
            urlPlaceholderResolver = context.getBean(UrlPlaceholderResolver.class);
        }


    }

    public int doEndTag() throws JspException {
        url = null;
        return EVAL_PAGE;
    }

    /**
     * Simple key provider that just returns the time of last restart.
     */
    class DefaultExpiresResourceKeyProvider implements ResourceKeyProvider {

        public String getUniqueKey(HttpServletRequest request, String url) {
            return Long.toString(ManagementFactory.getRuntimeMXBean().getStartTime());
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIncludecontextpath(boolean includeContextPath) {
        this.includeContextPath = includeContextPath;
    }
}
