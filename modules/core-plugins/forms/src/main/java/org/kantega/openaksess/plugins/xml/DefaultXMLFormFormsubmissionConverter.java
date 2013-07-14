package org.kantega.openaksess.plugins.xml;

import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultXMLFormFormsubmissionConverter implements XMLFormsubmissionConverter {
    public String createXMLFromFormSubmission(FormSubmission formSubmission) {
        Document doc = XMLHelper.newDocument();

        Element formsubmission = XMLHelper.setChild(doc, doc, "formsubmission");

        addMetadata(formSubmission, doc, formsubmission);
        addValues(formSubmission, doc, formsubmission);

        return XMLHelper.getString(doc);
    }

    private void addValues(FormSubmission formSubmission, Document doc, Element formsubmission) {
        Element formvalues = XMLHelper.setChild(doc, formsubmission, "formvalues");
        for (FormValue fv : formSubmission.getValues()) {
            Element formvalue = doc.createElement("formvalue");
            formvalues.appendChild(formvalue);
            XMLHelper.setChildText(doc, formvalue, "name", fv.getName());
            XMLHelper.setChildText(doc, formvalue, "value", fv.getValuesAsString());
        }
    }

    private void addMetadata(FormSubmission formSubmission, Document doc, Element form) {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        Element forminfo = XMLHelper.setChild(doc, form, "metadata");
        XMLHelper.setChildText(doc, forminfo, "formname", formSubmission.getForm().getTitle());
        XMLHelper.setChildText(doc, forminfo, "submissiondate", df.format(now));
    }
}

