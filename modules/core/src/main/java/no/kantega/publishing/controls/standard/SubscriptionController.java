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

import no.kantega.commons.util.RegExp;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.mailsubscription.MailSubscription;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionInterval;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionService;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Controller for adding subscriptions.
 * If the parameter «epost» is present it is assumed that we want to add or remove a subscription.
 * The controller assumes the associationId one wants to subscribe to is present
 * as a request parameter with the value «on». If the value is «off» we want to unsubscribe to the given associationId.
 */
public class SubscriptionController implements AksessController {

    @Autowired
    private MailSubscriptionService mailSubscriptionService;

    public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String epost = ServletRequestUtils.getStringParameter(request, "epost", "");

        int documentType = ServletRequestUtils.getIntParameter(request, "documenttype", -1);

        Map<String, Object> model = new HashMap<>();

        if (isNotBlank(epost)) {
            epost = epost.trim();

            if (validEmail(epost)) {
                Enumeration parameters = request.getParameterNames();
                while(parameters.hasMoreElements()){
                    try{
                        String paramName = (String)parameters.nextElement();
                        int channelId = Integer.parseInt(paramName, 10);
                        String op = ServletRequestUtils.getStringParameter(request, paramName);
                        if ("av".equalsIgnoreCase(op) || "off".equalsIgnoreCase(op)) {
                            mailSubscriptionService.removeMailSubscription(epost, channelId, documentType);
                            model.put("meldtAv", Boolean.TRUE);  // Backwards compability
                            model.put("unsubscribed", Boolean.TRUE);
                        } else if ("pa".equalsIgnoreCase(op) || "on".equalsIgnoreCase(op)) {
                            MailSubscription mailSubscription = new MailSubscription();
                            mailSubscription.setChannel(channelId);
                            mailSubscription.setDocumenttype(documentType);
                            mailSubscription.setEmail(epost);
                            mailSubscription.setLanguage(Language.NORWEGIAN_BO);
                            MailSubscriptionInterval interval = MailSubscriptionInterval.valueOf(ServletRequestUtils.getRequiredStringParameter(request, "interval"));
                            mailSubscription.setInterval(interval);
                            mailSubscriptionService.addMailSubscription(mailSubscription);
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
