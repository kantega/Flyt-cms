package no.kantega.publishing.modules.forms.model;

import java.util.Date;

/**
 * A summary of the formsubmissions which exists for a form
 */
public class FormSubmissionsSummary {
    private int formId;
    private int noSubmissions;
    private Date firstSubmissionDate;
    private Date lastSubmissionDate;

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getNoSubmissions() {
        return noSubmissions;
    }

    public void setNoSubmissions(int noSubmissions) {
        this.noSubmissions = noSubmissions;
    }

    public Date getFirstSubmissionDate() {
        return firstSubmissionDate;
    }

    public void setFirstSubmissionDate(Date firstSubmissionDate) {
        this.firstSubmissionDate = firstSubmissionDate;
    }

    public Date getLastSubmissionDate() {
        return lastSubmissionDate;
    }

    public void setLastSubmissionDate(Date lastSubmissionDate) {
        this.lastSubmissionDate = lastSubmissionDate;
    }
}
