package no.kantega.publishing.modules.forms.filter;

import no.kantega.publishing.api.forms.model.DefaultFormSubmission;
import no.kantega.publishing.api.forms.model.DefaultFormValue;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormValue;
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
    private DefaultFormSubmission formSubmission;
    private List<FormError> errors;
    private boolean mandatory = false;
    private int currentFieldIndex;
    private FormElementValidatorFactory formElementValidatorFactory;
    private boolean capture = false;
    private StringBuilder validatorArg = new StringBuilder();

    public FormSubmissionFillFilter(Map<String, String[]> params, Form form) {
        this.params = params;
        this.formSubmission = new DefaultFormSubmission();
        formSubmission.setForm(form);
        this.errors = new ArrayList<FormError>();
        currentFieldIndex = 0;
    }

    public void setFormElementValidatorFactory(FormElementValidatorFactory formElementValidatorFactory) {
        this.formElementValidatorFactory = formElementValidatorFactory;
    }

    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equalsIgnoreCase("div")) {
            processDiv(name, attributes);
        } else if (name.equalsIgnoreCase("span")) {
            processSpan(name, attributes);
        } else if (name.equalsIgnoreCase("input")
                || name.equalsIgnoreCase("radio")
                || name.equalsIgnoreCase("checkbox")
                || name.equalsIgnoreCase("textarea")
                || name.equalsIgnoreCase("select")) {
            // New input field
            processInput(name, attributes);
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

    public DefaultFormSubmission getFormSubmission() {
        return formSubmission;
    }

    public List<FormError> getErrors() {
        return errors;
    }

    private void processDiv(String name, Attributes attributes) {
        if (attributes != null && attributes.getValue("class") != null && attributes.getValue("class").contains("formElement")) {
            // New form element, check if it is mandatory
            mandatory = attributes.getValue("class").contains("mandatory");
            currentFieldIndex++;
        }
    }

    private void processSpan(String name, Attributes attributes) {
        capture = attributes != null
                && attributes.getValue("class") != null
                && attributes.getValue("class").contains("regex");
    }

    private void processInput(String name, Attributes attributes) {
        String inputName = attributes.getValue("name");
        String inputType = attributes.getValue("type");

        String[] values = params.get(inputName);
        boolean isEmpty = true;
        if (values != null && values.length > 0) {
            DefaultFormValue formValue = new DefaultFormValue();
            formValue.setName(inputName);
            formValue.setValues(values);
            String v = formValue.getValuesAsString();
            if (v != null && v.length() > 0) {
                isEmpty = false;
            }

            String inputClass = attributes.getValue("class");

            if (!isEmpty && inputType != null && "input".equalsIgnoreCase(name) && "text".equalsIgnoreCase(inputType)) {
                if (inputClass != null && inputClass.length() > 0) {
                    // Check if element length > maxlength
                    if (attributes.getValue("maxlength") != null) {
                        int inputMaxlength = Integer.parseInt(attributes.getValue("maxlength"));
                        if (v.length() > inputMaxlength) {
                            errors.add(new FormError(inputName, currentFieldIndex, "aksess.formerror.size"));
                        }
                    }
                    // Get form element validator for given class
                    if (formElementValidatorFactory != null) {
                        FormElementValidator fev = formElementValidatorFactory.getFormElementValidatorById(inputClass);
                        if (fev != null) {
                            // Validate value
                            errors = fev.validate(formValue, currentFieldIndex, new String[]{ validatorArg.toString() }, errors);
                            validatorArg = new StringBuilder();
                        }
                    }
                }
            }

            formSubmission.addValue(formValue);
        }

        if (isEmpty && mandatory) {
            // Field is mandatory
            errors.add(new FormError(inputName, currentFieldIndex, "aksess.formerror.mandatory"));
        }

        mandatory = false;
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
                DefaultFormValue formValue = new DefaultFormValue();
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
