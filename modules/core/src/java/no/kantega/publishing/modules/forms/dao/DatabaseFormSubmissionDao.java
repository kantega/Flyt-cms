package no.kantega.publishing.modules.forms.dao;

import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.modules.forms.model.FormSubmissionsSummary;
import no.kantega.publishing.modules.forms.model.FormValue;
import no.kantega.publishing.common.data.Content;

import javax.sql.DataSource;
import java.util.*;
import java.util.Date;
import java.sql.*;

import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 *
 */
public class DatabaseFormSubmissionDao implements FormSubmissionDao {
    private FormSubmissionsSummaryMapper formSubmissionsSummaryMapper = new FormSubmissionsSummaryMapper();
    private FormSubmissionMapper formSubmissionMapper = new FormSubmissionMapper();
    private DataSource dataSource;

    public FormSubmission getFormSubmissionById(int formSubmissionId) {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
        List<FormSubmission> list = template.query("SELECT * FROM formsubmission WHERE FormSubmissionId = ?", formSubmissionMapper, formSubmissionId);
        if (list.size() > 0) {
            FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
            callback.setFormSubmission(list);
            new JdbcTemplate(dataSource).query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId = ? ORDER BY FieldNumber", new Object[]{formSubmissionId}, callback);
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<FormSubmission> getFormSubmissionsByFormId(int formId) {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);

        List<FormSubmission> list = template.query("SELECT * FROM formsubmission WHERE FormId = ?", formSubmissionMapper, formId);

        FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
        callback.setFormSubmission(list);

        new JdbcTemplate(dataSource).query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ?) ORDER BY FieldNumber", new Object[]{formId}, callback);

        return list;
    }

    @SuppressWarnings("unchecked")
    public List<FormSubmission> getFormSubmissionsByFormIdAndIdentity(int formId, String identity) {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);

        List<FormSubmission> list = template.query("SELECT * FROM formsubmission WHERE FormId = ? AND AuthenticatedIdentity = ?", new FormSubmissionMapper(), formId, identity);

        FormSubmissionValuesCallbackHandler callback = new FormSubmissionValuesCallbackHandler();
        callback.setFormSubmission(list);

        new JdbcTemplate(dataSource).query("SELECT * FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ? AND AuthenticatedIdentity = ?) ORDER BY FieldNumber", new Object[]{formId, identity}, callback);

        return list;
    }

    public int saveFormSubmission(final FormSubmission form) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        if (form.getFormSubmissionId() > 0) {
            // Update
            template.update("UPDATE formsubmission SET SubmittedBy = ?, AuthenticatedIdentity = ?, Password = ?, Email = ?, SubmittedDate = ? WHERE FormSubmissionId = ?",
                    new Object[] {form.getSubmittedBy(), form.getAuthenticatedIdentity(), form.getPassword(), form.getEmail(), new Timestamp(new Date().getTime()), form.getFormSubmissionId()});

            // Delete old form values
            template.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId = ?", new Object[] {form.getFormSubmissionId()});
        } else {
            // Insert new
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

            template.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement p = connection.prepareStatement("INSERT INTO formsubmission (FormId, SubmittedBy, AuthenticatedIdentity, Password, Email, SubmittedDate) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    p.setInt(1, form.getForm().getId());
                    p.setString(2, form.getSubmittedBy());
                    p.setString(3, form.getAuthenticatedIdentity());
                    p.setString(4, form.getPassword());
                    p.setString(5, form.getEmail());
                    p.setTimestamp(6, new Timestamp(new Date().getTime()));
                    return p;
                }
            }, keyHolder);

            form.setFormSubmissionId(keyHolder.getKey().intValue());
        }

        // Insert map values

        List values = form.getValues();
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                FormValue value = (FormValue)values.get(i);
                if (value.getValues() != null) {
                    for (String v : value.getValues()) {
                        template.update("INSERT INTO formsubmissionvalues (FormSubmissionId, FieldNumber, FieldName, FieldValue) VALUES (?,?,?,?)", new Object[]{form.getFormSubmissionId(), i, value.getName(), v});
                    }                    
                }
            }
        }

        return form.getFormSubmissionId();
    }

    @SuppressWarnings("unchecked")
    public List<String> getFieldNamesForForm(int formId) {
        List<String> uniqueNames = new ArrayList<String>();
        Map<String, String> mapNames = new HashMap<String, String>();

        // Get distinct names in correct order, cant do this only with SQL it seems
        JdbcTemplate template = new JdbcTemplate(dataSource);
        List<String> allNames = template.queryForList("SELECT FieldName FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ?) ORDER BY FieldNumber", new Object[] {formId}, String.class);
        for (String n : allNames) {
            if (mapNames.get(n) == null) {
                uniqueNames.add(n);
                mapNames.put(n, n);
            }
        }
        return uniqueNames;
    }

    public void deleteFormSubmissionsByFormId(int formId) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId IN (SELECT FormSubmissionId FROM formsubmission WHERE FormId = ?)", new Object[] {formId});        
        template.update("DELETE FROM formsubmission WHERE FormId = ?", new Object[] {formId});
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deleteFormSubmissionById(int formSubmissionId) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update("DELETE FROM formsubmission WHERE FormSubmissionId = ?", new Object[] {formSubmissionId});
        template.update("DELETE FROM formsubmissionvalues WHERE FormSubmissionId = ?", new Object[] {formSubmissionId});        
    }

    public List<FormSubmissionsSummary> getFormSubmissionsSummaryForAllForms() {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
        List <FormSubmissionsSummary> summaries = template.query("SELECT FormId, COUNT(*) AS NoSubmissions, MIN(SubmittedDate) AS FirstDate, MAX(SubmittedDate) AS LastDate FROM formsubmission group by formid", formSubmissionsSummaryMapper);
        return summaries;
    }

    private class FormSubmissionMapper implements ParameterizedRowMapper<FormSubmission> {
        public FormSubmission mapRow(ResultSet rs, int i) throws SQLException {
            FormSubmission formSubmission = new FormSubmission();
            formSubmission.setFormSubmissionId(rs.getInt("FormSubmissionId"));
            int formId = rs.getInt("FormId");
            Content content = new Content();
            content.setId(formId);
            formSubmission.setForm(new AksessContentForm(content));
            formSubmission.setSubmittedBy(rs.getString("SubmittedBy"));
            formSubmission.setAuthenticatedIdentity(rs.getString("AuthenticatedIdentity"));
            formSubmission.setPassword(rs.getString("Password"));
            formSubmission.setEmail(rs.getString("Email"));
            formSubmission.setSubmissionDate(rs.getDate("SubmittedDate"));
            formSubmission.setValues(new ArrayList<FormValue>());
            
            return formSubmission;
        }
    }

    private class FormSubmissionsSummaryMapper implements ParameterizedRowMapper<FormSubmissionsSummary> {
        public FormSubmissionsSummary mapRow(ResultSet rs, int i) throws SQLException {
            FormSubmissionsSummary summary = new FormSubmissionsSummary();
            summary.setFormId(rs.getInt("FormId"));
            summary.setNoSubmissions(rs.getInt("NoSubmissions"));
            summary.setFirstSubmissionDate(rs.getDate("FirstDate"));
            summary.setLastSubmissionDate(rs.getDate("LastDate"));

            return summary;
        }
    }


    private class FormSubmissionValuesCallbackHandler implements RowCallbackHandler {
        private Map<Integer, FormSubmission> formSubmissions = new HashMap<Integer, FormSubmission>();

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
                    formValues = new ArrayList<FormValue>();
                    submission.setValues(formValues);
                }

                String name = rs.getString("FieldName");
                FormValue formValue = null;
                for (FormValue fv : formValues) {
                    if (fv.getName().equals(name)) {
                        formValue = fv;
                        break;
                    }
                }
                if (formValue == null) {
                    formValue = new FormValue();
                }
                formValue.setName(name);

                // Add existing values if any
                String[] values = formValue.getValues();
                List<String> list = new ArrayList<String>();
                if (values != null) {
                    list.addAll(Arrays.asList(values));
                }
                list.add(rs.getString("FieldValue"));
                formValue.setValues(list.toArray(new String[0]));

                formValues.add(formValue);
            }
        }
    }
}
