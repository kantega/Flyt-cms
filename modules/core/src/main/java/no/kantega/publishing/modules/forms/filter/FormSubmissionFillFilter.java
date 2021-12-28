package no.kantega.publishing.modules.forms.filter;

import no.kantega.commons.xmlfilter.Filter;
import no.kantega.publishing.api.forms.model.*;
import no.kantega.publishing.modules.forms.validate.FormElementValidator;
import no.kantega.publishing.modules.forms.validate.FormElementValidatorFactory;
import no.kantega.publishing.modules.forms.validate.FormError;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Creates a FormSubmission filled with values based on a form and values sent from user
 * Validates input from user against form definition
 */
public class FormSubmissionFillFilter implements Filter {
    private final List<String> inputs = asList("input", "radio", "checkbox", "textarea", "select");
    private String[] excludedParameters = {"thisId", "contentId", "isAksessFormSubmit", "csrfkey"};

    private Map<String, String[]> params;
    private DefaultFormSubmission formSubmission;
    private List<FormError> errors;
    private FormElementValidatorFactory formElementValidatorFactory;

    private boolean shouldAddParametersNotInForm = true;

    public FormSubmissionFillFilter(Map<String, String[]> params, Form form, boolean shouldAddParametersNotInForm) {
        this.params = params;
        this.formSubmission = new DefaultFormSubmission();
        formSubmission.setForm(form);
        this.errors = new ArrayList<>();
        this.shouldAddParametersNotInForm = shouldAddParametersNotInForm;
    }

    public void setFormElementValidatorFactory(FormElementValidatorFactory formElementValidatorFactory) {
        this.formElementValidatorFactory = formElementValidatorFactory;
    }

    public FormSubmission getFormSubmission() {
        return formSubmission;
    }

    private void processFormElement(String name, Element element, int currentFieldIndex) {
        String inputName = element.attr("name");
        String inputType = element.attr("type");

        String[] values = params.get(inputName);

        if (values != null && values.length > 0) {
            DefaultFormValue formValue = new DefaultFormValue();
            formValue.setName(inputName);
            formValue.setValues(values);

            validateField(name, element, inputName, inputType, formValue, currentFieldIndex);
            setRecipientMailIfExists(formValue, element);

            formSubmission.addValue(formValue);
        } else if (element.parent().parent().hasClass("mandatory")) {
            errors.add(new FormError(inputName, currentFieldIndex, "aksess.formerror.mandatory"));
        }
    }

    private void setRecipientMailIfExists(FormValue formValue, Element element) {
        String fieldId = element.attr("id");
        if ("RecipientEmail".equalsIgnoreCase(fieldId)) {
            formSubmission.setSubmittedByEmail(formValue.getValuesAsString());
        }
    }

    private void validateField(String name, Element element, String inputName, String inputType, FormValue formValue, int currentFieldIndex) {
        String formValueAsString = formValue.getValuesAsString();
        boolean isEmpty = isBlank(formValueAsString);

        String inputClass = element.attr("class");
        if (!isEmpty && inputType != null && "input".equalsIgnoreCase(name) && "text".equalsIgnoreCase(inputType)) {
            validateMaxlength(element, inputName, formValueAsString, currentFieldIndex);
            validateFieldBasedOnType(formValue, inputClass, element, currentFieldIndex);
        }

        if (isEmpty && element.parent().parent().hasClass("mandatory")) {
            errors.add(new FormError(inputName, currentFieldIndex, "aksess.formerror.mandatory"));
        }
    }

    private void validateFieldBasedOnType(FormValue formValue, String inputClass, Element element, int currentFieldIndex) {
        if (formElementValidatorFactory != null) {
            FormElementValidator validator = formElementValidatorFactory.getFormElementValidatorById(inputClass);
            if (validator != null) {
                String validatorArg = getValidatorArg(element, inputClass);
                errors = validator.validate(formValue, currentFieldIndex, new String[]{ validatorArg }, errors);
            }
        }
    }

    private String getValidatorArg(Element element, String inputClass) {
        switch (inputClass) {
            case "regularexpression":
                Elements regex = element.parent().getElementsByClass("regex");
                if(regex.size() > 0){
                    return regex.get(0).text();
                }
                break;
            case "date":
                Elements dateformat = element.parent().getElementsByClass("dateformat");
                if(dateformat.size() > 0){
                    return dateformat.get(0).text();
                }
                break;
        }
        return "";
    }

    private void validateMaxlength(Element element, String inputName, String formValueAsString, int currentFieldIndex) {
        String maxlength = element.attr("maxlength");
        if (isNotBlank(maxlength)) {
            int inputMaxlength = Integer.parseInt(maxlength);
            if (formValueAsString.length() > inputMaxlength) {
                errors.add(new FormError(inputName, currentFieldIndex, "aksess.formerror.size"));
            }
        }
    }

    private void addCustomParametersNotInForm() {
        Map<String, String[]> tmpParameters = new LinkedHashMap<>(params);
        for (FormValue value : formSubmission.getValues()) {
            tmpParameters.remove(value.getName());
        }
        for (Map.Entry<String, String[]> entry : tmpParameters.entrySet()) {
            String name = entry.getKey();
            if (!isExcludedParameter(name)) {
                String[] value = entry.getValue();
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

    public List<FormError> getErrors() {
        return errors;
    }

    @Override
    public Document runFilter(Document document) {
        int currentFieldIndex = 0;
        for (String input : inputs) {
            Elements elementsByTag = document.getElementsByTag(input);
            for (Element element : elementsByTag) {
                processFormElement(input, element, currentFieldIndex++);
            }
        }

        if (shouldAddParametersNotInForm) {
            addCustomParametersNotInForm();
        }
        return document;
    }
}
