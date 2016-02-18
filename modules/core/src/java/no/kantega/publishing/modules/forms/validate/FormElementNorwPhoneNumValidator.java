package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.api.forms.model.FormValue;

import java.util.List;

public class FormElementNorwPhoneNumValidator implements FormElementValidator {

    private static String id = "norwegianphonenumber";


    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue, int currentFieldIndex, String[] args, List<FormError> formErrors) {
        String norwPhoneNum = formValue.getValues()[0];
        if (norwPhoneNum != null && 0 < norwPhoneNum.length()) {
            String norwPhoneNumNoSpaces = norwPhoneNum.replaceAll("\\s", ""); //Removes whitespaces
            String localNumber;
            if(!norwPhoneNumNoSpaces.startsWith("+47") && !norwPhoneNumNoSpaces.startsWith("47") && !norwPhoneNumNoSpaces.startsWith("0047")){
                localNumber = norwPhoneNumNoSpaces; //No Norwegian prefix
            } else {
                localNumber = norwPhoneNumNoSpaces.substring(norwPhoneNumNoSpaces.indexOf("47")+2);
            }
            if(localNumber.length() != 8) {
                //Non-Norwegian phone number length
                formErrors.add(new FormError(formValue.getName(), currentFieldIndex, "aksess.formerror.norwphone"));
            }
        }
        return formErrors;
    }
}
