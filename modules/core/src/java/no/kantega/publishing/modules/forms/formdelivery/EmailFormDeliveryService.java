package no.kantega.publishing.modules.forms.formdelivery;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.FormValue;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.springframework.web.util.HtmlUtils;


/**
 *  Send form submission via email
 */
public class EmailFormDeliveryService implements FormDeliveryService {
    private String mailTemplate = "formsubmission.vm";

    public void deliverForm(FormSubmission formSubmission) {
        if (formSubmission.getForm().getEmail() == null || formSubmission.getForm().getEmail().length() == 0) {
            Log.debug(getClass().getName(), "Email was blank, form not sent via email", null, null);
            return;
        }
        try {
            String from = formSubmission.getEmail();
            if (from == null || from.indexOf("@") == -1) {
                // Use default sender
                from = Aksess.getConfiguration().getString("mail.from");
            }
            String to = formSubmission.getForm().getEmail();
            String subject = formSubmission.getForm().getTitle();

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("form", formSubmission);

            List<FormValue> values = formSubmission.getValues();
            for (FormValue v : values) {
                String value = v.getValuesAsString();
                HtmlUtils.htmlEscape(value);
            }

            MailSender.send(from, to, subject, mailTemplate, param);
        } catch (SystemException e) {
            Log.error("", e, null, null);
        } catch (ConfigurationException e) {
            Log.error("", e, null, null);
        }

    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }
}
