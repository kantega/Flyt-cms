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

package no.kantega.commons.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides access to virtual, possibly changing URLs.
 * Matching resources are given a far future expires header.
 */
public class FarFutureExpiresDirectoryFilter implements Filter {

    private Pattern resourcePattern = Pattern.compile("/expires/([a-f0-9]*)(/.*)");

    private static final long YEAR = (long) 365*24*60*60*1000;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI().substring(req.getContextPath().length());

        Matcher m = resourcePattern.matcher(uri);

        if(m.find()) {
            final String realUri = m.group(2);
            res.setDateHeader("Expires", System.currentTimeMillis() + YEAR);
            req.getRequestDispatcher(realUri).forward(request, response);
            return;
        } 

        chain.doFilter(req, res);

    }

    public void destroy() {
    }
}
