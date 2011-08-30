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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DefaultFormSubmission implements FormSubmission {

    private int formSubmissionId;
    private Form form;
    private String submittedBy;
    private Identity authenticatedIdentity;
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

    public String getSubmittedByName() {
        return submittedBy;
    }

    public void setSubmittedByName(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Identity getAuthenticatedIdentity() {
        return authenticatedIdentity;
    }

    public void setAuthenticatedIdentity(Identity authenticatedIdentity) {
        this.authenticatedIdentity = authenticatedIdentity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword() {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubmittedByEmail() {
        return email;
    }

    public void setSubmittedByEmail(String email) {
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