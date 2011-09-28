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

package no.kantega.openaksess.forms.email.delivery;

import no.kantega.commons.log.Log;
import no.kantega.openaksess.forms.pdf.PDFGenerator;
import no.kantega.openaksess.forms.xml.XMLFormsubmissionConverter;
import no.kantega.publishing.api.forms.delivery.FormDeliveryService;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.util.*;


/**
 *  Send form submission via email
 */
public class EmailFormDeliveryService implements FormDeliveryService {
    private String mailTemplate = null;
    private String xslFoDocumentPath = null;
    private String pdfFilename = "kvittering.pdf";

    private PDFGenerator pdfGenerator;
    private XMLFormsubmissionConverter xmlFormsubmissionConverter;

    public String getId() {
        return "aksessEmail";
    }

    public void deliverForm(FormSubmission formSubmission) {
        if (formSubmission.getForm().getEmail() == null || formSubmission.getForm().getEmail().length() == 0) {
            Log.debug(getClass().getName(), "Email was blank, form not sent via email", null, null);
            return;
        }
        try {
            String from = formSubmission.getSubmittedByEmail();
            boolean notEmailAddress = from == null || !from.contains("@");
            if (notEmailAddress) {
                // Use default sender
                from = Aksess.getConfiguration().getString("mail.from");
            }
            String to = formSubmission.getForm().getEmail();

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("form", formSubmission);

            sendEmail(formSubmission, from, to, param);
        } catch (Exception e) {
            Log.error("Delivering form by email failed. Form Id: " + formSubmission.getForm().getId(), e, null, null);
        }

    }

    private void sendEmail(FormSubmission formSubmission, String from, String to, Map<String, Object> param) throws Exception {
        String xml = xmlFormsubmissionConverter.createXMLFromFormSubmission(formSubmission);
        byte[] pdf = pdfGenerator.createPDF(xml, xslFoDocumentPath);

        List<MimeBodyPart> bodyparts = new ArrayList<MimeBodyPart>();

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
