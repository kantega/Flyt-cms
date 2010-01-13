package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.filter.FormSubmissionFillFilter;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.xmlfilter.FilterPipeline;
import java.util.*;
import java.io.StringReader;
import java.io.StringWriter;

/**
 *
 */
public class DefaultFormSubmissionBuilder implements FormSubmissionBuilder {

    public FormSubmission buildFormSubmission(Map<String, String[]> values, Form form) {
        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(values, form);

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        StringWriter sw = new StringWriter();
        try {
            pipeline.filter(new StringReader(form.getFormDefinition()), sw);
        } catch (SystemException e) {
            Log.error(getClass().getName(), e, null, null);
            return null;
        }

        return filter.getFormSubmission();
    }
}
