package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.Form;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *
 */
public interface FormSubmissionBuilder {
    public FormSubmission buildFormSubmission(Map<String, String[]> values, Form form);
}
