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

package no.kantega.publishing.api.forms.model;

import no.kantega.security.api.identity.Identity;

import java.util.Date;
import java.util.List;

/**
 * A FormSubmission contains data posted in a form as well as metadata about the user who submitted the form and information about the form which was submitted
 */
public interface FormSubmission {
    int getFormSubmissionId();
    Form getForm();
    void setForm(Form form);
    String getSubmittedByName();
    String getSubmittedByEmail();
    Identity getAuthenticatedIdentity();
    String getPassword();
    void setPassword(String password);
    Date getSubmissionDate();
    List<FormValue> getValues();
    void setAuthenticatedIdentity(Identity identity);
    void setSubmittedByName(String submittedBy);
    void setSubmittedByEmail(String email);
}
