package org.kantega.openaksess.plugins.database.delivery;

import no.kantega.publishing.api.forms.delivery.FormDeliveryService;
import no.kantega.publishing.api.forms.model.FormSubmission;
import org.kantega.openaksess.plugins.database.dao.FormSubmissionDao;

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
