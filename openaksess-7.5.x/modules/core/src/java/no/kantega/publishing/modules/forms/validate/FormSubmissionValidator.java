package no.kantega.publishing.modules.forms.validate;

import no.kantega.publishing.api.forms.model.FormSubmission;

import java.util.List;

public interface FormSubmissionValidator {
    List<FormError> validate(FormSubmission formSubmission);
}
