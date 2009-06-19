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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Calendar;

public class HttpHelper {
    public static boolean isInClientCache(HttpServletRequest request, HttpServletResponse response, String etag, Date lastModified) {
        // IE bryr seg ikke om å sjekke om ting er modifisert, så vi må la være å bruke dette i adminmodus
        if (isAdminMode(request)) {
            return false;
        }

        response.setHeader("ETag", etag);
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastModified);
        cal.set(Calendar.MILLISECOND, 0);
        lastModified = cal.getTime();
        response.setDateHeader("Last-Modified", lastModified.getTime());

        boolean isCached = false;
        String ifNoneMatch = request.getHeader("If-None-Match");
        String ifModifiedSince = request.getHeader("If-Modified-Since");

        if (ifNoneMatch != null && ifModifiedSince != null && ifNoneMatch.equals(etag)) {
            if (request.getDateHeader("If-Modified-Since") == lastModified.getTime()) {
                isCached = true;
            }
        }

        return isCached;
    }

    public static boolean isAdminMode(HttpServletRequest request) {
        if (request == null) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("adminMode") != null) {
                return true;
            }
        }

        return false;
    }
}
