package no.kantega.publishing.modules.forms.util;

import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.filter.FormFillFilter;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;

import java.util.Map;
import java.io.StringWriter;
import java.io.StringReader;

/**
 *
 */
public class DefaultFilledFormBuilder implements FilledFormBuilder {
    public Form buildFilledForm(Map<String, String[]> values, final Form form) {
        FormFillFilter filter = new FormFillFilter(values);

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        StringWriter sw = new StringWriter();
        try {
            pipeline.filter(new StringReader(form.getFormDefinition()), sw);
        } catch (SystemException e) {
            Log.error(getClass().getName(), e, null, null);
            return null;
        }

        final String fd = sw.toString();

        return new Form() {
            public int getId() {
                return form.getId();
            }

            public String getTitle() {
                return form.getTitle();
            }

            public String getFormDefinition() {
                return fd;
            }

            public String getEmail() {
                return form.getEmail();
            }
        };
    }
}
