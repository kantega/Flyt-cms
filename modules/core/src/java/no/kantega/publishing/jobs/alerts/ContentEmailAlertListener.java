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

import no.kantega.publishing.security.data.User;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ContentEmailAlertListener implements ContentAlertListener {
    private static String SOURCE = "ContentEmailAlertListener";

    private String mailSubject = "Aksess Publisering";
    private String mailTemplate = "expirecontent.vm";

    public void sendContentAlert(User user, List content) {

        String editor = "";

        try {
            Configuration config = Aksess.getConfiguration();
            editor = config.getString("mail.editor");

            if (editor != null && editor.indexOf("@") == -1) {
                throw new ConfigurationException("mail.editor", SOURCE);
            }

        } catch (ConfigurationException e) {
            Log.error(SOURCE, e, null, null);
            return;
        }

        String recipient = user.getEmail();
        if (recipient == null || recipient.indexOf("@") == -1) {
            Log.info(SOURCE, "Kunne ikke sende epost til (mangler epostadresse): " + user.getId(), null, null);
            return;
        }


        try {
            Map param = new HashMap();
            param.put("contentlist", content);
            param.put("editor", editor);

            param.put("baseurl", Aksess.getBaseUrl());
            param.put("applicationurl", Aksess.getApplicationUrl());

            Log.debug(SOURCE, "Sender varsling til:" + recipient, null, null);

            MailSender.send(editor, recipient, mailSubject, mailTemplate, param);
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (ConfigurationException e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }
}
