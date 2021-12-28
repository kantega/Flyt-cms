/*

 */

package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.api.forms.model.FormValue;

import java.util.List;

public class FormElementNumberValidator  implements FormElementValidator {
    private static String id = "number";
    private static String numberRegex = "^\\d+$";

    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue, int currentFieldIndex, String[] args, List<FormError> formErrors) {
        String value = formValue.getValues()[0];
        if (value!=null && 0 < value.length()) {
            if (!value.matches(numberRegex)) {
                formErrors.add(new FormError(formValue.getName(), currentFieldIndex, "aksess.formerror.number"));
            }
        }
        return formErrors;
    }
}
