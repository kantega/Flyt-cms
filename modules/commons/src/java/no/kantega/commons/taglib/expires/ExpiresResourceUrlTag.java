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

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 *
 */
public class ExpiresResourceUrlTag extends TagSupport {


    private ResourceKeyProvider defaultExpiresSettings = new DefaultExpiresResourceKeyProvider();

    private boolean includeContextPath = true;

    private String url;

    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

        ResourceKeyProvider provider = defaultExpiresSettings;

        {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            Map<String, ResourceKeyProvider> beans = context.getBeansOfType(ResourceKeyProvider.class);
            if (beans.size() > 0) {
                provider = beans.values().iterator().next();
            }

        }
        try {
            String expireUrl = "/expires/" + provider.getUniqueKey(request, response, url) + url;
            if (includeContextPath) {
                expireUrl = request.getContextPath() + expireUrl;
            }
            pageContext.getOut().write(expireUrl);
        } catch (IOException e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * Simple key provider that just returns the time of last restart.
     */
    class DefaultExpiresResourceKeyProvider implements ResourceKeyProvider {

        public String getUniqueKey(HttpServletRequest request, HttpServletResponse response, String url) {
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
