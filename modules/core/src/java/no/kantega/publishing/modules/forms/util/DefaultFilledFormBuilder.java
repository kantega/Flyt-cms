package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.FormValue;
import no.kantega.publishing.modules.forms.filter.FormFillFilter;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.StringWriter;
import java.io.StringReader;

/**
 *
 */
public class DefaultFilledFormBuilder implements FilledFormBuilder {
    public Form buildFilledForm(final FormSubmission submission) {
        Map<String, String[]> values = new HashMap<String, String[]>();
        for (FormValue value : submission.getValues()) {
            values.put(value.getName(), value.getValues());
        }

        FormFillFilter filter = new FormFillFilter(values, submission.getErrors());

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        StringWriter sw = new StringWriter();
        try {
            pipeline.filter(new StringReader(submission.getForm().getFormDefinition()), sw);
        } catch (SystemException e) {
            Log.error(getClass().getName(), e, null, null);
            return null;
        }

        final String fd = sw.toString();

        return new Form() {
            public int getId() {
                return submission.getForm().getId();
            }

            public String getTitle() {
                return submission.getForm().getTitle();
            }

            public String getFormDefinition() {
                return fd;
            }

            public String getEmail() {
                return submission.getForm().getEmail();
            }
        };
    }
}
