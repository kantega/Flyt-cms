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

import java.util.*;

/**
 * A FormSubmission contains data posted in a form as well as metadata about the user who submitted the form and information about the form which was submitted
 */
public interface FormSubmission {
    public int getFormSubmissionId();
    public Form getForm();
    public void setForm(Form form);
    public String getSubmittedByName();
    public String getSubmittedByEmail();
    public Identity getAuthenticatedIdentity();
    public String getPassword();
    public void setPassword();
    public Date getSubmissionDate();
    public List<FormValue> getValues();
    public void setAuthenticatedIdentity(Identity identity);
    public void setSubmittedByName(String submittedBy);
    public void setSubmittedByEmail(String email);
}
