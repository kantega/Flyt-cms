package no.kantega.publishing.modules.forms.validate;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.modules.forms.filter.FormSubmissionFillFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultFormSubmissionValidator implements FormSubmissionValidator {
    private static final Logger log = LoggerFactory.getLogger(DefaultFormSubmissionValidator.class);
    FormElementValidatorFactory formElementValidatorFactory;

    public List<FormError> validate(FormSubmission formSubmission) {
        Map<String, String[]> values = new HashMap<>();
        for (FormValue value : formSubmission.getValues()) {
            values.put(value.getName(), value.getValues());
        }

        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(values, formSubmission.getForm(), true);
        filter.setFormElementValidatorFactory(formElementValidatorFactory);

        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(filter);

        try {
            pipeline.filter(formSubmission.getForm().getFormDefinition());
        } catch (SystemException e) {
            log.error("", e);
            return null;
        }

        return filter.getErrors();
    }

    public void setFormElementValidatorFactory(FormElementValidatorFactory formElementValidatorFactory) {
        this.formElementValidatorFactory = formElementValidatorFactory;
    }
}


