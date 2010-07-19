package no.kantega.publishing.modules.forms.formdelivery;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.dao.FormSubmissionDao;

/**
 *
 */
public class DatabaseFormDeliveryService implements FormDeliveryService {
    private FormSubmissionDao formSubmissionDao;

    public void deliverForm(FormSubmission formSubmission) {
        formSubmissionDao.saveFormSubmission(formSubmission);
    }

    public void setFormSubmissionDao(FormSubmissionDao formSubmissionDao) {
        this.formSubmissionDao = formSubmissionDao;
    }
}
