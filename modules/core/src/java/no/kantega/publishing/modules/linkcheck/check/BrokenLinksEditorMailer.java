/*
 * Copyright 2011 Kantega AS
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
package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.modules.mailsender.MailSender;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BrokenLinksEditorMailer implements BrokenLinkEventListener {

    private Logger log = Logger.getLogger(getClass());


	public void process(List<LinkOccurrence> links) {
		Map params = new HashMap();
		params.put("linklist", links);
        params.put("applicationurl", Aksess.getApplicationUrl());
		
		try {
			mailLinksToEditor(params);
		} catch (ConfigurationException e) {
		   Log.error(this.getClass().getName(), e, null, null);
		} 
	}

	private void mailLinksToEditor(Map params) throws ConfigurationException {
		Properties properties = new Properties(Aksess.getConfiguration().getProperties());
		String mailFrom = properties.getProperty("mail.from");
		String mailEditor = properties.getProperty("mail.editor");
		if(exists(mailEditor) && exists(mailFrom)){
			MailSender.send(mailFrom, mailEditor, "Brukne lenker", "brokenlinks.vm", params);
		} else{
            String message = "finner ikke mailEditor eller mailFrom i config, kan ikke sende mail om brukne lenker";
            log.error(message);
            EventLog.log("System", null, Event.FAILED_EMAIL_SUBMISSION, message, null);
        }
	}

	private boolean exists(String property) {
		return property != null;
	}

}
