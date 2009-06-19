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
        Map model = new HashMap();

        if (epost != null && epost.indexOf('@') != -1 && epost.indexOf(".") != -1) {
            // Remove spaces
            epost = StringHelper.replace(epost, " ", "");

            Enumeration parameters = request.getParameterNames();
            while(parameters.hasMoreElements()){
                String paramName = (String)parameters.nextElement();
                try {
                    int channelId = Integer.parseInt(paramName, 10);
                    String op = param.getString(paramName);
                    if ("av".equalsIgnoreCase(op)) {
                        MailSubscriptionService.removeMailSubscription(epost, channelId, -1);
                        model.put("meldtAv", Boolean.TRUE);
                    } else if ("pa".equalsIgnoreCase(op)) {
                        MailSubscriptionService.addMailSubscription(epost, channelId, -1, interval, Language.NORWEGIAN_BO);
                        model.put("meldtPa", Boolean.TRUE);
                    }
                } catch (NumberFormatException e) {
                     //
                }
            }
        } else {
            SecuritySession securitySession = SecuritySession.getInstance(request);
            if(securitySession != null && securitySession.isLoggedIn()){
                epost = securitySession.getUser().getEmail();
            }
        }

        model.put("epost", epost);
        return model;
    }

    public String getDescription() {
        return "Epostabonnement - Melder brukere av/på epostliste(r)";
    }
}
