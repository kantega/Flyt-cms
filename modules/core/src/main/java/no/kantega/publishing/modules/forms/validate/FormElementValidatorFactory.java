/*
 */

package no.kantega.publishing.modules.forms.validate;

import java.util.List;

public class FormElementValidatorFactory {
    private List<FormElementValidator> formElementValidators;

    public FormElementValidator getFormElementValidatorById(String id) {
        for (FormElementValidator validator : formElementValidators) {
            if (validator.getId().equals(id)) {
                return validator;
            }
        }
        return null;
    }

    public void setFormElementValidators(List<FormElementValidator> formElementValidators) {
        this.formElementValidators = formElementValidators;
    }
}
