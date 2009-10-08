package no.kantega.publishing.modules.forms.model;

import java.util.*;

/**
 *
 */
public class FormSubmission {
    private int formSubmissionId;
    private Form form;
    private String submittedBy;
    private String authenticatedIdentity;
    private String password;
    private String email;
    private Date submissionDate;
    private List<FormValue> values;

    public int getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(int formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getAuthenticatedIdentity() {
        return authenticatedIdentity;
    }

    public void setAuthenticatedIdentity(String authenticatedIdentity) {
        this.authenticatedIdentity = authenticatedIdentity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public List<FormValue> getValues() {
        return values;
    }

    public void setValues(List<FormValue> values) {
        this.values = values;
    }

    public void addValue(FormValue value) {
        if (values == null) {
            values = new ArrayList<FormValue>();
        }
        for (FormValue v : values) {
            if (v.getName().equals(value.getName())) {
                // Value already added
                return;
            }
        }
        values.add(value);
    }
}
