package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.model.FormSubmission;

/**
 *
 */
public interface FilledFormBuilder {

    public Form buildFilledForm(FormSubmission formSubmission);
}
