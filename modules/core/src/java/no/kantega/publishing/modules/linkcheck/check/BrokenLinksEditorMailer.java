package no.kantega.publishing.modules.linkcheck.check;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;
import org.apache.log4j.Logger;

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
            log.error("finner ikke mailEditor eller mailFrom i config, kan ikke sende mail om brukne lenker");
        }
	}

	private boolean exists(String property) {
		return property != null;
	}

}
