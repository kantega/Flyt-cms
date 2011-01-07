/*

 */
package no.kantega.publishing.modules.forms.validate;

import java.util.List;
import no.kantega.publishing.api.forms.model.FormValue;

public interface FormElementValidator {


    /**
     * Returns an ID for this validator.
     *
     * @return an ID for this validator.
     */
    public String getId();

    /**
     * Validates the given form value. If the value is not valid, a FormError is added to the formErrors-list.
     *
     * @param formValue a value.
     * @param currentFieldIndex the index of the field for which the value was provided.
     * @param args validator arguments
     * @param formErrors a list of form errors.
     * @return the formErrors-list given as argument.
     */
    public List<FormError> validate(FormValue formValue, int currentFieldIndex,  String[] args, List<FormError> formErrors);

}
