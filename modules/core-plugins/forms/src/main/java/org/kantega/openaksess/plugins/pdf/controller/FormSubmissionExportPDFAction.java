package org.kantega.openaksess.plugins.pdf.controller;

import no.kantega.publishing.api.forms.model.FormSubmission;
import org.kantega.openaksess.plugins.pdf.PDFGenerator;
import org.kantega.openaksess.plugins.xml.XMLFormsubmissionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FormSubmissionExportPDFAction extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String xslFoDocumentPath;
    private PDFGenerator pdfGenerator;
    private XMLFormsubmissionConverter xmlFormsubmissionConverter;
    private String pdfFilename = "kvittering.pdf";


    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null) {
            FormSubmission formSubmission = (FormSubmission) session.getAttribute("aksessFormSubmission");
            if (formSubmission != null) {
                log.info("Serving formsubmission {} as PDF", formSubmission.getFormSubmissionId());

                String xml = xmlFormsubmissionConverter.createXMLFromFormSubmission(formSubmission);
                // Create PDF
                byte[] pdf = pdfGenerator.createPDF(xml, xslFoDocumentPath);

                // Add headers
                response.addHeader("Content-Disposition", "attachment; filename=\"" + pdfFilename + "\"");
                response.setContentType("application/pdf");
                response.setContentLength(pdf.length);

                //Send content to browser
                response.getOutputStream().write(pdf);
                response.getOutputStream().flush();
            }
        }
        return null;
    }

    public void setXslFoDocumentPath(String xslFoDocumentPath) {
        this.xslFoDocumentPath = xslFoDocumentPath;
    }

    public void setPdfGenerator(PDFGenerator pdfGenerator) {
        this.pdfGenerator = pdfGenerator;
    }

    public void setXmlFormsubmissionConverter(XMLFormsubmissionConverter xmlFormsubmissionConverter) {
        this.xmlFormsubmissionConverter = xmlFormsubmissionConverter;
    }

    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }
}
