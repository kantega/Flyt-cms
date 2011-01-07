package no.kantega.openaksess.forms.database.dao;

import junit.framework.TestCase;

import javax.sql.DataSource;

import no.kantega.publishing.api.forms.model.DefaultForm;
import no.kantega.publishing.api.forms.model.*;
import no.kantega.publishing.test.database.HSQLDBDatabaseCreator;

import java.util.*;

/**
 *
 */
public class DatabaseFormSubmissionDaoTest extends TestCase {
    public void testGetFormSubmissionById() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        DatabaseFormSubmissionDao dao = new DatabaseFormSubmissionDao();
        dao.setDataSource(dataSource);

        FormSubmission output = dao.getFormSubmissionById(1);

        assertEquals("donald@duck.com", output.getSubmittedByEmail());
        assertEquals(3, output.getValues().size());
    }

    public void testGetFormSubmissionsByFormId() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        DatabaseFormSubmissionDao dao = new DatabaseFormSubmissionDao();
        dao.setDataSource(dataSource);

        List submissions = dao.getFormSubmissionsByFormId(1);
        assertEquals(4, submissions.size());
    }

    public void testGetFormSubmissionsByFormIdAndIdentity() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        DatabaseFormSubmissionDao dao = new DatabaseFormSubmissionDao();
        dao.setDataSource(dataSource);
        List submissions = dao.getFormSubmissionsByFormIdAndIdentity(1, "donald");
        assertEquals(1, submissions.size());
    }

    public void testSaveFormSubmission() {
        DefaultForm form = new DefaultForm();
        form.setId(100);
        form.setTitle("TestForm");

        DefaultFormSubmission formSubmission = new DefaultFormSubmission();

        formSubmission.setFormSubmissionId(1);
        formSubmission.setForm(form);
        formSubmission.setSubmissionDate(new Date());
        formSubmission.setSubmittedByEmail("donald@duck.com");
        formSubmission.setPassword("dolly");

        List<FormValue> values = new ArrayList<FormValue>();
        DefaultFormValue name = new DefaultFormValue();
        name.setName("name");
        name.setValues(new String[] {"Donald Duck"});
        values.add(name);

        DefaultFormValue age = new DefaultFormValue();
        age.setName("age");
        age.setValues(new String[] {"31"});
        values.add(age);

        formSubmission.setValues(values);


        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        DatabaseFormSubmissionDao dao = new DatabaseFormSubmissionDao();
        dao.setDataSource(dataSource);

        dao.saveFormSubmission(formSubmission);
    }

    public void testGetFieldNamesForForm() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        DatabaseFormSubmissionDao dao = new DatabaseFormSubmissionDao();
        dao.setDataSource(dataSource);

        List fieldNames = dao.getFieldNamesForForm(1);

        assertEquals(3, fieldNames.size());        
    }
}
