package no.kantega.publishing.spring;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.modules.mailsender.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

public class MailSendingErrorHandler implements ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(MailSendingErrorHandler.class);

    private String mailFrom;
    private String mailTemplate;
    private String mailSubject;
    private String mailRecipients;

    @Override
    public void handleError(Throwable throwable) {
        log.error("MailSendingErrorHandler", throwable);

        Map mailParams = new HashMap();

        /*
        try {
            MailSender.send(mailFrom, mailRecipients, mailSubject, mailTemplate, mailParams);
        } catch (ConfigurationException e) {
            log.error("Error sending email", e);
        } */
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

    public void setMailRecipients(String mailRecipients) {
        this.mailRecipients = mailRecipients;
    }
}
