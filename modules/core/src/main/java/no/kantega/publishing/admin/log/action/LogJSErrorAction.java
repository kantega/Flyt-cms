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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LogJSErrorAction {
    private static final Logger log = LoggerFactory.getLogger(LogJSErrorAction.class);

    public ResponseEntity handleRequest(HttpServletRequest request) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String message = param.getString("message");
        String url = param.getString("url");
        String line = param.getString("line");

        String userName = "";

        SecuritySession securitySession = SecuritySession.getInstance(request);
        if (securitySession != null && securitySession.getUser() != null) {
            User user = securitySession.getUser();
            userName = user.getName();
        }

        log.error("Message:" + message + "\n" + "Javascript URL:" + url + "\n" + "Line:" + line + "\n" + "User:" + userName);

        return new ResponseEntity(HttpStatus.OK);
    }
}


