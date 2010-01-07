/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.kantega.publishing.modules.forms.validate;

import java.util.List;
import no.kantega.publishing.modules.forms.model.FormValue;

public class FormElementEmailValidator  implements FormElementValidator {
    private static String id = "email";
    private static String emailRegex = "^[\\w-_.]*[\\w-_.]\\@[\\w].+[\\w]+[\\w]$";

    public String getId() {
        return id;
    }

    public List<FormError> validate(FormValue formValue,List<FormError> formErrors) {
        String value = formValue.getValues()[0];
        if (value!=null && 0 < value.length()) {
            if (!value.matches(emailRegex)) {
                formErrors.add(new FormError(id,"Invalid email"));
            }
        }
        return formErrors;
    }
}
