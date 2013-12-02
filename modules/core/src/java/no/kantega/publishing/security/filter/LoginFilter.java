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

package no.kantega.publishing.security.filter;

import no.kantega.publishing.security.SecuritySession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class LoginFilter implements Filter {
    private Pattern excludedFilesPattern = null;
    private Pattern exclucedHostsPattern = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        String filesPattern = filterConfig.getInitParameter("exclude");
        if(filesPattern != null) {
            excludedFilesPattern = Pattern.compile(filesPattern);
        }

        String hostsPattern = filterConfig.getInitParameter("exclude-hosts");
        if(hostsPattern != null) {
            exclucedHostsPattern = Pattern.compile(hostsPattern);
        }

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;


        try {
            SecuritySession securitySession = SecuritySession.getInstance(request);

            String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");

            // Sjekk om host er med i liste med unntak
            boolean excludeHost = false;
            if (exclucedHostsPattern != null) {
                Matcher m = exclucedHostsPattern.matcher(request.getServerName());
                excludeHost = m.matches();
            }

            if (!excludeHost) {
                // Sjekk om fila er med i liste med unntak
                boolean excludeFile = false;
                if (excludedFilesPattern != null) {
                    Matcher m = excludedFilesPattern.matcher(path);
                    excludeFile = m.matches();
                }

                if(!excludeFile) {
                    // Sjekk at bruker er logget inn
                    if (!securitySession.isLoggedIn()) {
                        securitySession.initiateLogin(request, response);
                        return;
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void destroy() {

    }

}
