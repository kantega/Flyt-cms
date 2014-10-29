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

package no.kantega.commons.util;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class URLHelper {

    public static String getRootURL(HttpServletRequest request) {
        return getServerURL(request) +  request.getContextPath() + "/";
    }

    public static String getCurrentUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");
        if (originalUri != null) {
            // Call via 404
            url = originalUri;
        }        
        return url;
    }

    public static String getServerURL(HttpServletRequest request){
        int port = request.getServerPort();
        String portStr = "";
        if (port != 80 && port != 443) {
            portStr = ":" + port;
        }
        return request.getScheme() + "://" +  request.getServerName() + portStr;
    }

    public static String getUrlWithHttps(HttpServletRequest request){
        StringBuilder sb = new StringBuilder(getServerURL(request).replaceFirst("http:", "https:"));
        sb.append(request.getRequestURI());

        String queryString = request.getQueryString();
        if(isNotBlank(queryString)){
            sb.append("?").append(queryString);
        }
        return sb.toString();
    }

    /**
     * Returns the actual URL requested by the client
     * @param request
     * @return String URL requested by client
     */
    public static String getRequestedUrl(HttpServletRequest request){
        StringBuilder urlBuilder = new StringBuilder();

        // protocol
        urlBuilder.append(request.getScheme());
        urlBuilder.append("://");

        // server/domain
        urlBuilder.append(request.getServerName());
        int serverPort = request.getServerPort();

        // Port
        if(serverPort != 80 && serverPort != 443){
            urlBuilder.append(":");
            urlBuilder.append(serverPort);
        }

        // URI
        urlBuilder.append(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));

        // Query params
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()){
            urlBuilder.append("?").append(request.getQueryString());
        }

        return urlBuilder.toString();
    }
}
