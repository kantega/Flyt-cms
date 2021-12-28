package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.validate.FormError;

import java.util.List;

/**
 *
 */
public interface FilledFormBuilder {
    public Form buildFilledForm(FormSubmission formSubmission, List<FormError> errors);
}
