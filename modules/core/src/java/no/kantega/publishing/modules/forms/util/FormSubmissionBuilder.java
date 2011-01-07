package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;

import java.util.Map;

/**
 *
 */
public interface FormSubmissionBuilder {
    public FormSubmission buildFormSubmission(Map<String, String[]> values, Form form);
}
