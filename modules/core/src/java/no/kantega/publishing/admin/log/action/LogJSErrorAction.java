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

package no.kantega.publishing.admin.log.action;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.log.Log;
import no.kantega.commons.client.util.RequestParameters;

/**
 * User: Anders Skar, Kantega AS
 * Date: Nov 3, 2008
 * Time: 3:16:39 PM
 */
public class LogJSErrorAction implements Controller {
    private static String SOURCE = "aksess.LogJSErrorAction";

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String message = param.getString("message");
        String url = param.getString("url");
        String line = param.getString("line");

        String userId = "";
        String userName = "";

        SecuritySession securitySession = SecuritySession.getInstance(request);
        if (securitySession != null && securitySession.getUser() != null) {
            User user = securitySession.getUser();
            userId = user.getId();
            userName = user.getName();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("Message:");
        sb.append(message);
        sb.append("\n");
        sb.append("Javascript URL:");
        sb.append(url);
        sb.append("\n");
        sb.append("Line:");
        sb.append(line);
        sb.append("\n");
        sb.append("User:");
        sb.append(userName);


        Log.error(SOURCE, sb.toString(), null, userId);


        return new ModelAndView(new RedirectView(Aksess.getContextPath() + "/admin/bitmaps/blank.gif"));
    }
}


