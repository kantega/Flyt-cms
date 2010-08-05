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

import no.kantega.commons.log.Log;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 *
 */
public class ResponseHeaderFilter implements Filter {

    public static final String HEADER_PARAM_PREFIX = "H_";
    private Set<String> extensions;
    private Map<String, String> headers = new HashMap<String, String>();


    public void init(FilterConfig config) throws ServletException {
        // extensions-param
        String extensionsParam = config.getInitParameter("extensions");
        if (extensionsParam != null && !"".equals(extensionsParam)) {
            extensions = new HashSet<String>();
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
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, expiresInDaysInt);
                Date d = cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                headers.put("Expires", dateFormat.format(d));
            } catch (NumberFormatException e) {
                Log.error(getClass().getSimpleName(), e, null, null);
            }
        }
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
