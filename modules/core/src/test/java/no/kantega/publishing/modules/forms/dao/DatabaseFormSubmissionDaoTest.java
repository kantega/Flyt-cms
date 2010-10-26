package no.kantega.publishing.modules.forms.dao;

import junit.framework.TestCase;

import javax.sql.DataSource;

import no.kantega.publishing.modules.forms.model.FormSubmissionsSummary;
import no.kantega.publishing.test.database.HSQLDBDatabaseCreator;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.modules.forms.model.FormValue;
import no.kantega.publishing.common.data.Content;

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

        assertEquals("donald@duck.com", output.getEmail());
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
        Content content = new Content();
        content.setId(100);
        content.setTitle("TestForm");

        FormSubmission formSubmission = new FormSubmission();

        formSubmission.setFormSubmissionId(1);
        formSubmission.setForm(new AksessContentForm(content));
        formSubmission.setAuthenticatedIdentity("donald");
        formSubmission.setSubmissionDate(new Date());
        formSubmission.setEmail("donald@duck.com");
        formSubmission.setPassword("dolly");

        List<FormValue> values = new ArrayList<FormValue>();
        FormValue name = new FormValue();
        name.setName("name");
        name.setValues(new String[] {"Donald Duck"});
        values.add(name);

        FormValue age = new FormValue();
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

    public void testGetFormSubmissionsSummaryForAllForms() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-db.sql")).createDatabase();

        DatabaseFormSubmissionDao dao = new DatabaseFormSubmissionDao();
        dao.setDataSource(dataSource);

        List<FormSubmissionsSummary> summaries = dao.getFormSubmissionsSummaryForAllForms();

        assertEquals(1, summaries.size());

        assertEquals(4, summaries.get(0).getNoSubmissions());
    }

}
