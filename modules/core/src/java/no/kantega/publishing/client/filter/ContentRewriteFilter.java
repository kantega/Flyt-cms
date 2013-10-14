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

package no.kantega.publishing.client.filter;

import no.kantega.publishing.common.util.CharResponseWrapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class ContentRewriteFilter implements Filter {
    private ServletContext servletContext;


    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
    }

    /**
     * Filter request, replace links with aliases
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse)response);

        chain.doFilter(request, wrapper);

        if (wrapper.isWrapped()) {
            String result  = rewrite((HttpServletRequest) request, wrapper.toString());
            PrintWriter out = response.getWriter();
            out.write(result);
            out.flush();
        }

    }

    private String rewrite(HttpServletRequest request, String content) {
        String out = content;
        final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        if(wac != null) {

            final Map<String, ContentRewriter> map = wac.getBeansOfType(ContentRewriter.class);

            for(ContentRewriter rewriter : map.values()) {
                out = rewriter.rewriteContent(request, out);
            }


        }
        return out;
    }

    public void destroy() {
        // Do nothing
    }
}
