package no.kantega.publishing.modules.forms.validate;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.modules.forms.filter.FormSubmissionFillFilter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultFormSubmissionValidator implements FormSubmissionValidator {
    FormElementValidatorFactory formElementValidatorFactory;

    public List<FormError> validate(FormSubmission formSubmission) {
        Map<String, String[]> values = new HashMap<String, String[]>();
        for (FormValue value : formSubmission.getValues()) {
            values.put(value.getName(), value.getValues());
        }

        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(values, formSubmission.getForm());
        filter.setFormElementValidatorFactory(formElementValidatorFactory);

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        StringWriter sw = new StringWriter();
        try {
            pipeline.filter(new StringReader(formSubmission.getForm().getFormDefinition()), sw);
        } catch (SystemException e) {
            Log.error(getClass().getName(), e, null, null);
            return null;
        }

        return filter.getErrors();
    }

    public void setFormElementValidatorFactory(FormElementValidatorFactory formElementValidatorFactory) {
        this.formElementValidatorFactory = formElementValidatorFactory;
    }
}


