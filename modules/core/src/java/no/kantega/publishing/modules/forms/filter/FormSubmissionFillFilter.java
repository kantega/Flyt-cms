package no.kantega.publishing.modules.forms.filter;

import java.util.ArrayList;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.util.Map;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.validate.FormError;
import no.kantega.publishing.modules.forms.model.FormValue;
import no.kantega.publishing.modules.forms.validate.FormElementValidatorFactory;
import no.kantega.publishing.modules.forms.validate.FormElementValidator;
import no.kantega.publishing.spring.RootContext;

/**
 *
 */
public class FormSubmissionFillFilter extends XMLFilterImpl {

    private Map<String, String[]> params;
    private FormSubmission formSubmission;
    private boolean mandatory = false;
    private int DEFAULT_SIZE = 128;
    private FormElementValidatorFactory formElementValidatorFactory;

    public FormSubmissionFillFilter(Map<String, String[]> params, Form form) {
        this.params = params;
        this.formSubmission = new FormSubmission();
        formSubmission.setForm(form);
        formSubmission.setErrors(new ArrayList());
        formElementValidatorFactory = (FormElementValidatorFactory) RootContext.getInstance().getBean("aksessFormElementValidatorFactory");
    }

    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equalsIgnoreCase("div")) {
            if (attributes != null && attributes.getValue("class") != null && attributes.getValue("class").contains("formElement")) {
                mandatory = attributes.getValue("class").contains("mandatory");
            }

        }

        if (name.equalsIgnoreCase("input")
                || name.equalsIgnoreCase("radio")
                || name.equalsIgnoreCase("checkbox")
                || name.equalsIgnoreCase("textarea")
                || name.equalsIgnoreCase("select")) {

            String inputName = attributes.getValue("name");
            String inputType = attributes.getValue("type");


            String[] values = params.get(inputName);
            boolean isEmpty = true;
            if (values != null && values.length > 0) {
                FormValue formValue = new FormValue();
                formValue.setName(inputName);
                formValue.setValues(values);
                String v = formValue.getValuesAsString();
                if (v != null && v.length() > 0) {
                    isEmpty = false;
                }

                String inputClass = attributes.getValue("class");

                if (!isEmpty && inputType != null && "input".equalsIgnoreCase(name) && "text".equalsIgnoreCase(inputType)) {
                    if (inputClass != null || !"".equals(inputClass)) {
                        int inputSize = attributes.getValue("size") != null ? Integer.parseInt(attributes.getValue("size")) : DEFAULT_SIZE;
                        int inputMaxlength = attributes.getValue("maxlength") != null ? Integer.parseInt(attributes.getValue("maxlength")) : DEFAULT_SIZE;

                        if (v.length() > java.lang.Math.min(inputSize, inputMaxlength)) {
                            formSubmission.getErrors().add(new FormError(inputName, "aksess.formerror.size"));
                        } else {
                            FormElementValidator fev = formElementValidatorFactory.getFormElementValidatorById(inputClass);
                            if (fev != null) {
                                formSubmission.setErrors(fev.validate(formValue, formSubmission.getErrors()));
                            }
                        }
                    }
                }

                formSubmission.addValue(formValue);
            }

            if (isEmpty && mandatory) {
                formSubmission.getErrors().add(new FormError(inputName, "aksess.formerror.mandatory"));
            }

            mandatory = false;

        }



        super.startElement(string, localName, name, attributes);

    }

    public FormSubmission getFormSubmission() {
        return formSubmission;
    }
}
