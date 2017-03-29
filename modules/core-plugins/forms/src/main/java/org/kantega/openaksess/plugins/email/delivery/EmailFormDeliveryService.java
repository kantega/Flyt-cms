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

package org.kantega.openaksess.plugins.email.delivery;

import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.forms.delivery.FormDeliveryService;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.modules.mailsender.MailSender;
import org.kantega.openaksess.plugins.pdf.PDFGenerator;
import org.kantega.openaksess.plugins.xml.XMLFormsubmissionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.util.*;


/**
 *  Send form submission via email
 */
public class EmailFormDeliveryService implements FormDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(EmailFormDeliveryService.class);
    private String mailTemplate = null;
    private String xslFoDocumentPath = null;
    private String pdfFilename = "kvittering.pdf";

    private PDFGenerator pdfGenerator;
    private XMLFormsubmissionConverter xmlFormsubmissionConverter;
    @Autowired
    private EventLog eventLog;

    @Autowired
    private SystemConfiguration configuration;

    public String getId() {
        return "aksessEmail";
    }

    public void deliverForm(FormSubmission formSubmission) {
        Form form = formSubmission.getForm();
        if (form.getEmail() == null || form.getEmail().length() == 0) {
            log.debug( "Email was blank, form not sent via email");
            return;
        }
        try {
            String from = formSubmission.getSubmittedByEmail();
            boolean notEmailAddress = from == null || !from.contains("@");
            if (notEmailAddress || configuration.getBoolean("EmailFormDeliveryService.useConfiguredFromAddress", false)) {
                // Use default sender
                from = configuration.getString("mail.from");
            }
            String to = form.getEmail();

            Map<String, Object> param = Collections.<String, Object>singletonMap("form", formSubmission);

            sendEmail(formSubmission, from, to, param);
            log.info("Sent formsubmission {} on email", formSubmission.getFormSubmissionId());
        } catch (Exception e) {
            eventLog.log("System", null, Event.FAILED_FORM_SUBMISSION, "Form Id: " + form.getId(), null);
            log.error("Delivering form by email failed. Form Id: " + form.getId(), e);
        }

    }

    private void sendEmail(FormSubmission formSubmission, String from, String to, Map<String, Object> param) throws Exception {
        String xml = xmlFormsubmissionConverter.createXMLFromFormSubmission(formSubmission);
        byte[] pdf = pdfGenerator.createPDF(xml, xslFoDocumentPath);

        List<MimeBodyPart> bodyparts = new ArrayList<>();

        String body = MailSender.createStringFromVelocityTemplate(mailTemplate, param);
        MimeBodyPart messagePart = MailSender.createMimeBodyPartFromStringMessage(body);
        bodyparts.add(messagePart);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource ds = new ByteArrayDataSource(pdf, "application/pdf");

        attachmentPart.setDataHandler(new DataHandler(ds));
        attachmentPart.setHeader("Content-ID", "<pdf" + new Date().getTime() + ">");
        attachmentPart.addHeader("Content-Description", pdfFilename);
        attachmentPart.addHeader("Content-Disposition", "attachment; filename=\"" + pdfFilename + "\"");


        bodyparts.add(attachmentPart);

        MailSender.send(from, to, formSubmission.getForm().getTitle(), bodyparts.toArray(new MimeBodyPart[bodyparts.size()]));
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public void setPdfGenerator(PDFGenerator pdfGenerator) {
        this.pdfGenerator = pdfGenerator;
    }

    public void setXmlFormsubmissionConverter(XMLFormsubmissionConverter xmlFormsubmissionConverter) {
        this.xmlFormsubmissionConverter = xmlFormsubmissionConverter;
    }

    public void setXslFoDocumentPath(String xslFoDocumentPath) {
        this.xslFoDocumentPath = xslFoDocumentPath;
    }

    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }
}
