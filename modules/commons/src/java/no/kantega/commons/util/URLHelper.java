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

import javax.servlet.http.HttpServletRequest;

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

    /**
     * Get valid url from two fragments.
     * /a + b -> /a/b
     * /a/ + /b -> /a/b
     * /a/ + b -> /a/b
     */
    public static String getValidUrl(String urlFragment1, String urlFragment2){
        StringBuilder urlBuilder = new StringBuilder();
        if(!urlFragment1.endsWith("/")){
            urlBuilder.append(urlFragment1);
            urlBuilder.append('/');
        } else {
            urlBuilder.append(urlFragment1);
        }

        if(urlFragment2.startsWith("/")){
            urlBuilder.append(urlFragment2.substring(1, urlFragment2.length()));
        } else {
            urlBuilder.append(urlFragment2);
        }
        return urlBuilder.toString();
    }
}
