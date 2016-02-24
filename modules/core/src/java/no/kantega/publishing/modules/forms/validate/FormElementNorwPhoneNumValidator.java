package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.api.forms.model.FormValue;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.util.List;

public class FormElementNorwPhoneNumValidator implements FormElementValidator {

    private static String id = "norwegianphonenumber";


    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue, int currentFieldIndex, String[] args, List<FormError> formErrors) {
        String norwPhoneNum = formValue.getValues()[0];
        if (isNotBlank(norwPhoneNum)){
            String norwPhoneNumNoSpaces = norwPhoneNum.replaceAll("\\s|-", ""); //Removes whitespaces and '-'
            String regex = "((00|\\+)?47)?([1-9]\\d{7})";
            if( !norwPhoneNumNoSpaces.matches(regex) ) {
                formErrors.add(new FormError(formValue.getName(), currentFieldIndex, "aksess.formerror.norwphone"));
            }
        }
        return formErrors;
    }
}
