/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.openaksess.forms.database.dao;

import no.kantega.publishing.api.forms.model.FormSubmission;

import java.util.Calendar;
import java.util.List;

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
     * Delete all form submissionis that is older than the date represented
     * by dateLimit.
     * @param dateLimit - the oldest a formsubmission can be.
     */
    public void deleteFormSubmissionsOlderThanDate(Calendar dateLimit);

    /**
     * Delete saved form submissions with given id
     * @param formSubmissionId - id of form submission
     */
    public void deleteFormSubmissionById(int formSubmissionId);

}
