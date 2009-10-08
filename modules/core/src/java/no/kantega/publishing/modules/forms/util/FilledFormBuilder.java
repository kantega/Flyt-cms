package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.Form;

import java.util.Map;

/**
 *
 */
public interface FilledFormBuilder {
    public Form buildFilledForm(Map<String, String[]> values, Form form);
}
