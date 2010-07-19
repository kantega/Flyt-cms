/*

 */

package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.modules.forms.model.FormValue;

import java.util.List;


public class FormElementRegExValidator implements FormElementValidator {

    private static String id = "regularexpression";


    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue, int currentFieldIndex, String[] args, List<FormError> formErrors) {
        String value = formValue.getValues()[0];
        if (value != null && 0 < value.length()) {
            String regex = args.length > 0 ? args[0] : null;
            if (regex != null && !"".matches(regex)) {
                if (!value.matches(regex)) {
                    formErrors.add(new FormError(formValue.getName(), currentFieldIndex, "aksess.formerror.regex"));
                }
            }
        }
        return formErrors;
    }

}
