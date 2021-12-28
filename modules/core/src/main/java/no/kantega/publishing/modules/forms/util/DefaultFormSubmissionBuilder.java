package no.kantega.publishing.modules.forms.util;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.filter.FormSubmissionFillFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class DefaultFormSubmissionBuilder implements FormSubmissionBuilder {
    private static final Logger log = LoggerFactory.getLogger(DefaultFormSubmissionBuilder.class);
    public FormSubmission buildFormSubmission(Map<String, String[]> values, Form form) {
        return buildFormSubmission(values, form, true);
    }

    public FormSubmission buildFormSubmission(Map<String, String[]> values, Form form, boolean shouldAddParametersNotInForm) {
        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(values, form, shouldAddParametersNotInForm);

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        try {
            pipeline.filter(form.getFormDefinition());
        } catch (SystemException e) {
            log.error("", e);
            return null;
        }

        return filter.getFormSubmission();
    }
}
