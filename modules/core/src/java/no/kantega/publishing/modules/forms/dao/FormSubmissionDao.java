package no.kantega.publishing.modules.forms.dao;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.FormSubmissionsSummary;

import java.util.List;

/**
 *
 */
public interface FormSubmissionDao {
    /**
     * Get FormSubmission with given id
     * @param formSubmissionId - id of formsubmission
     * @return - FormSubmission
     */
    public FormSubmission getFormSubmissionById(int formSubmissionId);

    /**
     * Get all form submissions for given form
     * @param formId - id of form
     * @return - List of FormSubmission
     */
    public List<FormSubmission> getFormSubmissionsByFormId(int formId);

    /**
     * Get form submission based on form id and identity
     * @param formId - id of form
     * @param identity - identity, eg username
     * @return - list of FormSubmission
     */
    public List<FormSubmission> getFormSubmissionsByFormIdAndIdentity(int formId, String identity);

    /**
     * Save a form submission
     * @param formSubmission - filled form
     * @return - id of saved form submission
     */
    public int saveFormSubmission(FormSubmission formSubmission);

    /**
     * Get all unique field names for form
     * @param formId - id of form
     * @return - list of field names
     */
    public List<String> getFieldNamesForForm(int formId);

    /**
     * Delete all saved form submissions for given form
     * @param formId - id of form
     */
    public void deleteFormSubmissionsByFormId(int formId);

    /**
     * Delete saved form submissions with given id
     * @param formSubmissionId - id of form submission
     */
    public void deleteFormSubmissionById(int formSubmissionId);

    /**
     * Get a summary of all formsubmissions for all forms
     * @return - list of FormSubmissionsSummary
     */
    public List<FormSubmissionsSummary> getFormSubmissionsSummaryForAllForms();

}
