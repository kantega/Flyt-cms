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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.urlplaceholder.UrlPlaceholderResolver;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.requestlisteners.ContentRequestListener;
import no.kantega.publishing.client.DefaultDispatchContext;
import org.kantega.jexmec.PluginManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class IncludeTag  extends TagSupport {
    String url = null;
    private PluginManager<OpenAksessPlugin> pluginManager;
    private UrlPlaceholderResolver urlPlaceholderResolver;

    public void setUrl(String url) {
        this.url = url;
    }

    public int doStartTag() throws JspException {
        if (url != null) {

            setPluginmanagerAndUrlPlaceholderResolverIfNull();

            try {
                url = urlPlaceholderResolver.replaceMacros(url, pageContext);
                String absoluteUrl = url;

                if(!url.startsWith("/")) {
                    String include_url = (String) pageContext.getRequest().getAttribute("javax.servlet.include.servlet_path");
                    if(include_url != null) {
                        absoluteUrl = include_url.substring(0, include_url.lastIndexOf("/") ) + "/" + url;
                    } else {
                        String servletPath = ((HttpServletRequest) pageContext.getRequest()).getServletPath();
                        if (servletPath.contains("/")) {
                            servletPath = servletPath.substring(0, servletPath.lastIndexOf('/'));
                            absoluteUrl = servletPath + "/" +url;
                        }
                    }
                }
                for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
                    for(ContentRequestListener listener : plugin.getContentRequestListeners()) {
                        listener.beforeIncludeTemplateDispatch(new DefaultDispatchContext((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), absoluteUrl));
                    }
                }
                pageContext.include(url);
            } catch (ServletException e) {
                throw new JspException(e);
            } catch (IOException e) {
                throw new JspException(e);
            } catch (SystemException e) {
                throw new JspException(e);
            }

        }

        return SKIP_BODY;
    }

    private void setPluginmanagerAndUrlPlaceholderResolverIfNull() {
        if (pluginManager == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            pluginManager = (PluginManager<OpenAksessPlugin>) context.getBean("pluginManager", PluginManager.class);
            urlPlaceholderResolver = context.getBean(UrlPlaceholderResolver.class);
        }
    }

    public int doEndTag() throws JspException {
        url = null;
        return EVAL_PAGE;
    }


}
