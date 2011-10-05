package no.kantega.publishing.modules.forms.filter;

import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.FormValue;
import no.kantega.publishing.modules.forms.validate.FormElementValidator;
import no.kantega.publishing.modules.forms.validate.FormElementValidatorFactory;
import no.kantega.publishing.modules.forms.validate.FormError;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.*;

/**
 * Creates a FormSubmission filled with values based on a form and values sent from user
 * Validates input from user against form definition
 */
public class FormSubmissionFillFilter extends XMLFilterImpl {
    private String[] excludedParameters = {"thisId", "contentId", "isAksessFormSubmit", "csrfkey"};

    private Map<String, String[]> params;
    private FormSubmission formSubmission;
    private boolean mandatory = false;
    private int currentFieldIndex;
    private FormElementValidatorFactory formElementValidatorFactory;
    private boolean capture = false;
    private StringBuilder validatorArg = new StringBuilder();

    public FormSubmissionFillFilter(Map<String, String[]> params, Form form) {
        this.params = params;
        this.formSubmission = new FormSubmission();
        formSubmission.setForm(form);
        formSubmission.setErrors(new ArrayList<FormError>());
        currentFieldIndex = 0;
    }

    public void setFormElementValidatorFactory(FormElementValidatorFactory formElementValidatorFactory) {
        this.formElementValidatorFactory = formElementValidatorFactory;
    }

    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equalsIgnoreCase("div")) {
            checkIfDivTagIsNewFormElement(name, attributes);
        } else if (name.equalsIgnoreCase("span")) {
            checkIfSpanTagContainsRegexp(name, attributes);
        } else if (name.equalsIgnoreCase("input")
                || name.equalsIgnoreCase("radio")
                || name.equalsIgnoreCase("checkbox")
                || name.equalsIgnoreCase("textarea")
                || name.equalsIgnoreCase("select")) {
            // New input field
            processFormElement(name, attributes);
        }

        super.startElement(string, localName, name, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        capture = false;
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException {
        if (capture) {
            char[] newChars = new char[length];
            System.arraycopy(ch, start, newChars, 0, length);
            validatorArg.append(newChars);
        }
        super.characters(ch, start, length);
    }

    public FormSubmission getFormSubmission() {
        return formSubmission;
    }

    private void checkIfDivTagIsNewFormElement(String name, Attributes attributes) {
        if (attributes != null && attributes.getValue("class") != null && attributes.getValue("class").contains("formElement")) {
            // New form element, check if it is mandatory
            mandatory = attributes.getValue("class").contains("mandatory");
            currentFieldIndex++;
        }
    }

    private void checkIfSpanTagContainsRegexp(String name, Attributes attributes) {
        capture = attributes != null
                && attributes.getValue("class") != null
                && attributes.getValue("class").contains("regex");
    }

    private void processFormElement(String name, Attributes attributes) {
        String inputName = attributes.getValue("name");
        String inputType = attributes.getValue("type");

        String[] values = params.get(inputName);

        if (values != null && values.length > 0) {
            FormValue formValue = new FormValue();
            formValue.setName(inputName);
            formValue.setValues(values);

            validateField(name, attributes, inputName, inputType, formValue);
            setRecipientMailIfExists(formValue, attributes);

            formSubmission.addValue(formValue);
        } else if (mandatory) {
            formSubmission.getErrors().add(new FormError(inputName, currentFieldIndex, "aksess.formerror.mandatory"));
        }

        mandatory = false;
    }

    private void setRecipientMailIfExists(FormValue formValue, Attributes attributes) {
        String fieldId = attributes.getValue("id");
        if ("RecipientEmail".equalsIgnoreCase(fieldId)) {
            formSubmission.setEmail(formValue.getValuesAsString());
        }
    }

    private void validateField(String name, Attributes attributes, String inputName, String inputType, FormValue formValue) {
        String formValueAsString = formValue.getValuesAsString();
        boolean isEmpty = true;
        if (formValueAsString != null && formValueAsString.length() > 0) {
            isEmpty = false;
        }


        String inputClass = attributes.getValue("class");
        if (!isEmpty && inputType != null && "input".equalsIgnoreCase(name) && "text".equalsIgnoreCase(inputType)) {
            validateMaxlength(attributes, inputName, formValueAsString);
            validateFieldBasedOnType(formValue, inputClass);
        }

        if (isEmpty && mandatory) {
            formSubmission.getErrors().add(new FormError(inputName, currentFieldIndex, "aksess.formerror.mandatory"));
        }
    }

    private void validateFieldBasedOnType(FormValue formValue, String inputClass) {
        if (formElementValidatorFactory != null) {
            FormElementValidator validator = formElementValidatorFactory.getFormElementValidatorById(inputClass);
            if (validator != null) {
                formSubmission.setErrors(validator.validate(formValue, currentFieldIndex, new String[]{ validatorArg.toString() }, formSubmission.getErrors()));
                validatorArg = new StringBuilder();
            }
        }
    }

    private void validateMaxlength(Attributes attributes, String inputName, String formValueAsString) {
        if (attributes.getValue("maxlength") != null) {
            int inputMaxlength = Integer.parseInt(attributes.getValue("maxlength"));
            if (formValueAsString.length() > inputMaxlength) {
                formSubmission.getErrors().add(new FormError(inputName, currentFieldIndex, "aksess.formerror.size"));
            }
        }
    }

    public void endDocument() throws SAXException {
        addCustomParametersNotInForm();
    }

    private void addCustomParametersNotInForm() {
        Map tmpParameters = new LinkedHashMap<String, String[]>(params);
        for (FormValue value : formSubmission.getValues()) {
            tmpParameters.remove(value.getName());
        }
        Iterator keys = tmpParameters.keySet().iterator();
        while (keys.hasNext()) {
            String name = (String)keys.next();
            if (!isExcludedParameter(name)) {
                String[] value = (String[])tmpParameters.get(name);
                FormValue formValue = new FormValue();
                formValue.setName(name);
                formValue.setValues(value);
                formSubmission.addValue(formValue);
            }
        }
    }

    private boolean isExcludedParameter(String name) {
        for (String excluded : excludedParameters) {
            if (excluded.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void setExcludedParameters(String[] excludedParameters) {
        this.excludedParameters = excludedParameters;
    }
}
