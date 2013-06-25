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

package no.kantega.publishing.controls.userprofile;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 22, 2007
 * Time: 2:25:16 PM
 */
public class ViewProfileController implements AksessController {
    private static final Logger log = LoggerFactory.getLogger(ViewProfileController.class);

    private static String SOURCE = "aksess.ViewProfileController";

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = null;

        RequestParameters param = new RequestParameters(request);

        Map model = new HashMap();
        SecuritySession session = SecuritySession.getInstance(request);

        String userId = param.getString("userId");
        if (userId == null || userId.length() == 0) {
            if (session.isLoggedIn()) {
                user = session.getUser();
                model.put("isEditable", Boolean.TRUE);
            }
        } else {
            user = session.getRealm().lookupUser(userId);
        }

        if (user != null) {
            Properties attributes = user.getAttributes();
            if (attributes != null) {
                Enumeration propertyNames = attributes.propertyNames();
                while (propertyNames.hasMoreElements()) {
                    String propertyName = (String)propertyNames.nextElement();
                    model.put("attribute_" + propertyName, attributes.getProperty(propertyName));
                    log.debug( "property:" + propertyName);
                }
            }
        }

        model.put("profile", user);

        return model;
    }

    public String getDescription() {
        return "Hent brukerprofil - Henter brukerprofil for angitt eller innlogget bruker og viser denne";
    }
}
