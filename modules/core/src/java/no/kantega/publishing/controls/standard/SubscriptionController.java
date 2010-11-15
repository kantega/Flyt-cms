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

package no.kantega.publishing.controls.standard;

import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.RegExp;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.modules.mailsubscription.api.MailSubscriptionService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.client.util.RequestParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kristian Lier Selnæs, Kantega
 * Date: 20.des.2006
 * Time: 12:14:50
 */
public class SubscriptionController implements AksessController {

    private static final String SOURCE = "aksess.SubscriptionController";

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String epost = param.getString("epost");
        String interval = param.getString("interval");
        int documentType = -1;
        try{
            documentType = Integer.parseInt(param.getString("documenttype"));
        } catch(NumberFormatException e) {
           // default to -1
        }

        Map model = new HashMap();

        if (epost != null && epost.length() > 0) {
            // Remove spaces
            epost = epost.trim();

            if (validEmail(epost)) {
                Enumeration parameters = request.getParameterNames();
                while(parameters.hasMoreElements()){
                    String paramName = (String)parameters.nextElement();
                    try {
                        int channelId = Integer.parseInt(paramName, 10);
                        String op = param.getString(paramName);
                        if ("av".equalsIgnoreCase(op) || "off".equalsIgnoreCase(op)) {
                            MailSubscriptionService.removeMailSubscription(epost, channelId, documentType);
                            model.put("meldtAv", Boolean.TRUE);  // Backwards compability
                            model.put("unsubscribed", Boolean.TRUE);
                        } else if ("pa".equalsIgnoreCase(op) || "on".equalsIgnoreCase(op)) {
                            MailSubscriptionService.addMailSubscription(epost, channelId, documentType, interval, Language.NORWEGIAN_BO);
                            model.put("meldtPa", Boolean.TRUE);  // Backwards compability
                            model.put("subscribed", Boolean.TRUE);
                        }
                    } catch (NumberFormatException e) {
                         //
                    }
                }
            } else {
                model.put("invalidEmail", Boolean.TRUE);
                model.put("epost", epost);  // Backwards compability
                model.put("email", epost);
            }

        } else {
            SecuritySession securitySession = SecuritySession.getInstance(request);
            if(securitySession != null && securitySession.isLoggedIn()){
                epost = securitySession.getUser().getEmail();
            }
        }

        model.put("epost", epost);  // Backwards compability
        model.put("email", epost);
        return model;
    }

    private boolean validEmail(String email) {
        return RegExp.isEmail(email);
    }

    public String getDescription() {
        return "Epostabonnement - Melder brukere av/på epostliste(r)";
    }
}
