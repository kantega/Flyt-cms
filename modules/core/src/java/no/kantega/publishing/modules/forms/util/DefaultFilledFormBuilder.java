package no.kantega.publishing.modules.forms.util;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.forms.model.DefaultForm;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.modules.forms.filter.FormFillFilter;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.modules.forms.validate.FormError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DefaultFilledFormBuilder implements FilledFormBuilder {
    private static final Logger log = LoggerFactory.getLogger(DefaultFilledFormBuilder.class);
    public Form buildFilledForm(final FormSubmission submission, List<FormError> errors) {
        Map<String, String[]> values = new HashMap<String, String[]>();
        for (FormValue value : submission.getValues()) {
            values.put(value.getName(), value.getValues());
        }

        FormFillFilter filter = new FormFillFilter(values, errors);

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        StringWriter sw = new StringWriter();
        try {
            pipeline.filter(new StringReader(submission.getForm().getFormDefinition()), sw);
        } catch (SystemException e) {
            log.error("", e);
            return null;
        }

        final String fd = sw.toString();

        DefaultForm form = new AksessContentForm(submission.getForm());
        form.setFormDefinition(fd);

        return form;
    }
}
