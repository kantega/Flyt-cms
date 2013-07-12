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

package no.kantega.commons.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class AksessRequestFilter implements Filter {
    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AksessRequestFilter.request.set((HttpServletRequest) request);
        AksessRequestFilter.response.set((HttpServletResponse) response);
        try {
            chain.doFilter(request, response);
        } finally {
            AksessRequestFilter.request.remove();
            AksessRequestFilter.response.remove();
        }
    }


    public void destroy() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public static HttpServletRequest getRequest() {
        return request.get();
    }

    public static HttpServletResponse getResponse() {
        return response.get();
    }
}
