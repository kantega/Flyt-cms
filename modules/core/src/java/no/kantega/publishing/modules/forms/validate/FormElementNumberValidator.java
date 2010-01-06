/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.kantega.publishing.modules.forms.validate;

import java.util.List;
import no.kantega.publishing.modules.forms.model.FormValue;

public class FormElementNumberValidator  implements FormElementValidator {
    private static String id = "number";
    private static String numberRegex = "^\\d+$";

    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue,List<FormError> formErrors) {
        String value = formValue.getValues()[0];
        if (value!=null && 0 < value.length()) {
            // negates for testing
            if (value.matches(numberRegex)) {
                formErrors.add(new FormError(id,"Invalid number"));
            }
        }
        return formErrors;
    }
}
