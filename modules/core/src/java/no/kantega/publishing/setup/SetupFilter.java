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

package no.kantega.publishing.setup;

import no.kantega.publishing.spring.OpenAksessContextLoaderListener;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class SetupFilter implements Filter {
    public static final String FILTER_ATTR = SetupFilter.class.getName() +".this";
    private OpenAksessContextLoaderListener contextLoader;

    private static String[] excludedStaticResources = {".png", ".jpg", ".gif", ".jjs", ".js", ".css"};

    public void init(FilterConfig filterConfig) throws ServletException {
        filterConfig.getServletContext().setAttribute(FILTER_ATTR, this);
        contextLoader = (OpenAksessContextLoaderListener) filterConfig.getServletContext().getAttribute(OpenAksessContextLoaderListener.LISTENER_ATTR);

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(contextLoader.isSetupNeeded() && !isStaticResource(req)) {
            if(isSetupRequest(req)) {
                if(!isLocalhost(req)) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Inital no.kantega.publishing.setup is only allowed from localhost");
                } else {
                    chain.doFilter(req, res);
                }
            } else {
                request.getRequestDispatcher("/Setup.initialAction").forward(req, res);
            }
        } else {
            chain.doFilter(request, response);
        }

    }

    private boolean isLocalhost(HttpServletRequest request) {
        if (request.getRemoteAddr().equals("127.0.0.1") || request.getRemoteAddr().equals("0:0:0:0:0:0:0:1")) {
            return true;
        }

        return false;
    }

    private boolean isStaticResource(HttpServletRequest request) {
        String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        for (String fileext : excludedStaticResources) {
            if (path.endsWith(fileext)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSetupRequest(HttpServletRequest req) {
        return req.getServletPath().equals("/Setup.initialAction");
    }

    public void destroy() {

    }


}
