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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class ResponseHeaderFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ResponseHeaderFilter.class);

    public static final String HEADER_PARAM_PREFIX = "H_";
    private Set<String> extensions;
    private Map<String, String> headers = new HashMap<>();

    public ResponseHeaderFilter() {}

    public void init(FilterConfig config) throws ServletException {
        // extensions-param
        String extensionsParam = config.getInitParameter("extensions");
        if (extensionsParam != null && !"".equals(extensionsParam)) {
            extensions = new HashSet<>();
            String[] extensionsArr = extensionsParam.split(",");
            for (String ext : extensionsArr) {
                this.extensions.add(ext.trim().toLowerCase());
            }
        }

        // H_-params
        Enumeration enumer = config.getInitParameterNames();
        while (enumer.hasMoreElements()) {
            String paramName = enumer.nextElement().toString();
            if (paramName.startsWith(HEADER_PARAM_PREFIX)) {
                String headerName = paramName.replaceFirst(HEADER_PARAM_PREFIX, "");
                headers.put(headerName, config.getInitParameter(paramName));
            }
        }

        // expiresInDays-param
        String expiresInDaysParam = config.getInitParameter("expiresInDays");
        if (expiresInDaysParam != null && !"".equals(expiresInDaysParam)) {
            try {
                int expiresInDaysInt = Integer.parseInt(expiresInDaysParam);
                addExpiresInHeader(expiresInDaysInt);
            } catch (NumberFormatException e) {
                log.error("Could not format expire date", e);
            }
        }
    }

    private void addExpiresInHeader(int expiresInDaysInt) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, expiresInDaysInt);
        Date d = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        headers.put("Expires", dateFormat.format(d));
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        if (shouldBeProcessed(request.getRequestURI())) {
            setHeaders(response);
        }

        chain.doFilter(req, resp);
    }

    private boolean shouldBeProcessed(String uri) {
        boolean retVal = false;
        if (extensions == null) {
            retVal = true;
        } else {
            if (uri != null) {
                uri = uri.toLowerCase();
                for (String ext : extensions) {
                    if (uri.endsWith(ext)) {
                        retVal = true;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    private void setHeaders(HttpServletResponse response) {
        for (String key : headers.keySet()) {
            response.setHeader(key, headers.get(key));
        }
    }

}
