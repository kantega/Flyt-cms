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

package no.kantega.publishing.jobs.alerts;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentEmailAlertListener implements ContentAlertListener {
    private static final Logger log = LoggerFactory.getLogger(ContentEmailAlertListener.class);

    private String mailSubject = "Aksess Publisering";
    private String mailTemplate = "expirecontent.vm";
    private String mailFrom = "noreply";

    public void sendContentAlert(User user, List content) {

        String recipient = user.getEmail();
        if (recipient == null || !recipient.contains("@")) {
            log.info( "Kunne ikke sende epost til (mangler epostadresse): " + user.getId());
            return;
        }


        try {
            Map<String, Object> param = new HashMap<>();
            param.put("contentlist", content);
            param.put("editor", mailFrom);

            param.put("baseurl", Aksess.getBaseUrl());
            param.put("applicationurl", Aksess.getApplicationUrl());

            log.debug( "Sender varsling til:" + recipient);

            MailSender.send(mailFrom, recipient, mailSubject, mailTemplate, param);
        } catch (SystemException | ConfigurationException e) {
            log.error("", e);
        }
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }
}
