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

package org.kantega.openaksess.plugins.database.dao;

import no.kantega.publishing.api.forms.model.*;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class DatabaseFormSubmissionDao implements FormSubmissionDao {
    private FormSubmissionMapper formSubmissionMapper = new FormSubmissionMapper();
    private JdbcTemplate jdbcTemplate;

    public FormSubmission getFormSubmissionById(int formSubmissionId) {
        List<FormSubmission> list = jdbcTemplate.query("SELECT * FROM formsubmission WHERE FormSubmissionId = ?", formSubmissionMapper, formSubmissionId);
        if (list.size() > 0) {
            FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
            callback.setFormSubmission(list);
            jdbcTemplate.query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId = ? ORDER BY FieldNumber", new Object[]{formSubmissionId}, callback);
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<FormSubmission> getFormSubmissions() {
        List<FormSubmission> list = jdbcTemplate.query("SELECT * FROM formsubmission", formSubmissionMapper);
        FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
        callback.setFormSubmission(list);
        jdbcTemplate.query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission) ORDER BY FieldNumber", new Object[]{}, callback);
        return list;
    }

    @Override
    public List<FormSubmission> getFormSubmissionsByFormId(int formId) {
        List<FormSubmission> list = jdbcTemplate.query("SELECT * FROM formsubmission WHERE FormId = ?", formSubmissionMapper, formId);

        FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
        callback.setFormSubmission(list);

        jdbcTemplate.query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ?) ORDER BY FieldNumber", new Object[]{formId}, callback);

        return list;
    }

    @Override
    public List<FormSubmission> getFormSubmissionsByFormIdAndIdentity(int formId, String identity) {
        List<FormSubmission> list = jdbcTemplate.query("SELECT * FROM formsubmission WHERE FormId = ? AND AuthenticatedIdentity = ?", new FormSubmissionMapper(), formId, identity);

        FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
        callback.setFormSubmission(list);

        jdbcTemplate.query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ? AND AuthenticatedIdentity = ?) ORDER BY FieldNumber", new Object[]{formId, identity}, callback);

        return list;
    }

    @Override
    public int saveFormSubmission(final FormSubmission form) {
        int id = form.getFormSubmissionId();

        String auth = "";
        Identity identity = form.getAuthenticatedIdentity();
        if (identity != null) {
            if (identity.getDomain() != null && identity.getDomain().length() > 0) {
                auth = identity.getDomain() + ":";
            }
            auth += identity.getUserId();
        }

        final String userId = auth;

        if (form.getFormSubmissionId() > 0) {
            // Update
            jdbcTemplate.update("UPDATE formsubmission SET SubmittedBy = ?, AuthenticatedIdentity = ?, Password = ?, Email = ?, SubmittedDate = ? WHERE FormSubmissionId = ?",
                    form.getSubmittedByName(), userId, form.getPassword(), form.getSubmittedByEmail(), new Timestamp(new Date().getTime()), form.getFormSubmissionId());

            // Delete old form values
            jdbcTemplate.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId = ?", form.getFormSubmissionId());
        } else {
            // Insert new
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement p = connection.prepareStatement("INSERT INTO formsubmission (FormId, SubmittedBy, AuthenticatedIdentity, Password, Email, SubmittedDate) VALUES (?,?,?,?,?,?)", new String[] {"FORMSUBMISSIONID"});
                p.setInt(1, form.getForm().getId());
                p.setString(2, form.getSubmittedByName());
                p.setString(3, userId);
                p.setString(4, form.getPassword());
                p.setString(5, form.getSubmittedByEmail());
                p.setTimestamp(6, new Timestamp(new Date().getTime()));
                return p;
            }, keyHolder);

            id = keyHolder.getKey().intValue();
        }

        // Insert map values

        List<FormValue> values = form.getValues();
        if (values != null) {
            List<Object[]> batchvalues = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++) {
                FormValue value = values.get(i);
                if (value.getValues() != null) {
                    for (String v : value.getValues()) {
                        batchvalues.add(new Object[]{id, i, value.getName(), v});
                    }
                }
            }
            jdbcTemplate.batchUpdate("INSERT INTO formsubmissionvalues (FormSubmissionId, FieldNumber, FieldName, FieldValue) VALUES (?,?,?,?)", batchvalues);
        }

        return id;
    }

    @Override
    public List<String> getFieldNamesForForm(int formId) {
        List<String> uniqueNames = new ArrayList<>();
        Map<String, String> mapNames = new HashMap<>();

        // Get distinct names in correct order, cant do this only with SQL it seems
        List<String> allNames = jdbcTemplate.queryForList("SELECT FieldName FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ?) ORDER BY FieldNumber", new Object[] {formId}, String.class);
        for (String n : allNames) {
            if (mapNames.get(n) == null) {
                uniqueNames.add(n);
                mapNames.put(n, n);
            }
        }
        return uniqueNames;
    }

    @Override
    public void deleteFormSubmissionsByFormId(int formId) {
        jdbcTemplate.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ?)", formId);
        jdbcTemplate.update("DELETE FROM formsubmission WHERE FormId = ?", formId);
    }

    @Override
    public void deleteFormSubmissionsOlderThanDate(Calendar dateLimit) {
        jdbcTemplate.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE SubmittedDate < ?)",
                new Timestamp(dateLimit.getTime().getTime()));
        jdbcTemplate.update("DELETE FROM formsubmission where SubmittedDate < ?",
                new Timestamp(dateLimit.getTime().getTime()));
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate  = new JdbcTemplate(dataSource);
    }

    @Override
    public void deleteFormSubmissionById(int formSubmissionId) {
        jdbcTemplate.update("DELETE FROM formsubmission WHERE FormSubmissionId = ?", formSubmissionId);
        jdbcTemplate.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId = ?", formSubmissionId);
    }

    private static class FormSubmissionMapper implements RowMapper<FormSubmission> {
        public DefaultFormSubmission mapRow(ResultSet rs, int i) throws SQLException {
            DefaultFormSubmission formSubmission = new DefaultFormSubmission();
            formSubmission.setFormSubmissionId(rs.getInt("FormSubmissionId"));
            int formId = rs.getInt("FormId");

            DefaultForm form = new DefaultForm();
            form.setId(formId);
            formSubmission.setForm(form);
            formSubmission.setSubmittedByName(rs.getString("SubmittedBy"));

            String auth = rs.getString("AuthenticatedIdentity");
            if (auth != null) {
                DefaultIdentity identity = new DefaultIdentity();
                if (auth.contains(":")) {
                    String[] comps = auth.split(":");
                    identity.setDomain(comps[0]);
                    identity.setUserId(comps[1]);
                } else {
                    identity.setUserId(auth);
                }
                formSubmission.setAuthenticatedIdentity(identity);
            }


            formSubmission.setPassword(rs.getString("Password"));
            formSubmission.setSubmittedByEmail(rs.getString("Email"));
            formSubmission.setSubmissionDate(rs.getTimestamp("SubmittedDate"));
            formSubmission.setValues(new ArrayList<>());

            return formSubmission;
        }
    }

    private static class FormSubmissionValuesCallbackHandler implements RowCallbackHandler {
        private Map<Integer, FormSubmission> formSubmissions = new HashMap<>();

        public void setFormSubmission(List<FormSubmission> submissions) {
            for (FormSubmission s : submissions) {
                formSubmissions.put(s.getFormSubmissionId(), s);
            }
        }

        public void processRow(ResultSet rs) throws SQLException {
            int id = rs.getInt("FormSubmissionId");
            FormSubmission submission = formSubmissions.get(id);
            if (submission != null) {
                List<FormValue> formValues = submission.getValues();
                if (formValues == null) {
                    formValues = new ArrayList<>();
                    ((DefaultFormSubmission)submission).setValues(formValues);
                }

                String name = rs.getString("FieldName");
                DefaultFormValue formValue = null;
                for (FormValue fv : formValues) {
                    if (fv.getName().equals(name)) {
                        formValue = (DefaultFormValue)fv;
                        break;
                    }
                }
                if (formValue == null) {
                    formValue = new DefaultFormValue();
                }
                formValue.setName(name);

                // Add existing values if any
                String[] values = formValue.getValues();
                List<String> list = new ArrayList<>();
                if (values != null) {
                    list.addAll(Arrays.asList(values));
                }
                list.add(rs.getString("FieldValue"));
                formValue.setValues(list.toArray(new String[list.size()]));

                formValues.add(formValue);
            }
        }
    }
}
