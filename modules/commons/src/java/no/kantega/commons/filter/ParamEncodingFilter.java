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

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 */
public class ParamEncodingFilter implements Filter {
    public static final String POST_PARAM_ENCODING = "encoding";
    public static final String GET_PARAM_ENCODING = "getencoding";

    private String encoding;
    private String GETencoding;

    public ParamEncodingFilter() {}

    public void init(FilterConfig filterConfig) throws ServletException {
        String encoding = filterConfig.getInitParameter(POST_PARAM_ENCODING);
        this.encoding = StringUtils.defaultIfBlank(encoding, "utf-8");

        String GETencoding = filterConfig.getInitParameter(GET_PARAM_ENCODING);
        this.GETencoding = StringUtils.defaultIfBlank(GETencoding, "iso-8859-1");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(((HttpServletRequest)servletRequest).getMethod().equals("POST")) {
            servletRequest.setCharacterEncoding(encoding);
        } else {
            servletRequest.setCharacterEncoding(GETencoding);
        }
        Iterator it = servletRequest.getParameterMap().keySet().iterator();
        if (it.hasNext()) {
            String key = (String) it.next();
            servletRequest.getParameter(key);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }

}
