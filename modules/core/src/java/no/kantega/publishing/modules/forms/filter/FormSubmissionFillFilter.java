package no.kantega.publishing.modules.forms.filter;

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.util.Map;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.model.FormValue;

/**
 *
 */
public class FormSubmissionFillFilter extends XMLFilterImpl {
    private Map<String, String[]> params;
    private FormSubmission formSubmission;

    public FormSubmissionFillFilter(Map<String, String[]> params, Form form) {
        this.params = params;
        this.formSubmission = new FormSubmission();
        formSubmission.setForm(form);
    }

    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equalsIgnoreCase("input") ||
            name.equalsIgnoreCase("radio") ||
            name.equalsIgnoreCase("checkbox") ||
            name.equalsIgnoreCase("textarea") ||
            name.equalsIgnoreCase("select")) {

            String inputName = attributes.getValue("name");

            String[] values = params.get(inputName);
            if (values != null && values.length > 0) {
                FormValue formValue = new FormValue();
                formValue.setName(inputName);
                formValue.setValues(values);
                formSubmission.addValue(formValue);
            }
        }

        super.startElement(string, localName, name, attributes);

    }

    public FormSubmission getFormSubmission() {
        return formSubmission;
    }
}
