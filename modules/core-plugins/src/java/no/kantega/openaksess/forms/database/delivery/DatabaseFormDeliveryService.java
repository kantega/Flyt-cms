package no.kantega.openaksess.forms.database.delivery;

import no.kantega.openaksess.forms.database.dao.FormSubmissionDao;
import no.kantega.publishing.api.forms.delivery.FormDeliveryService;
import no.kantega.publishing.api.forms.model.FormSubmission;

/**
 *
 */
public class DatabaseFormDeliveryService implements FormDeliveryService {
    private FormSubmissionDao formSubmissionDao;

    public String getId() {
        return "aksessDatabase";
    }

    public void deliverForm(FormSubmission formSubmission) {
        formSubmissionDao.saveFormSubmission(formSubmission);
    }

    public void setFormSubmissionDao(FormSubmissionDao formSubmissionDao) {
        this.formSubmissionDao = formSubmissionDao;
    }
}
