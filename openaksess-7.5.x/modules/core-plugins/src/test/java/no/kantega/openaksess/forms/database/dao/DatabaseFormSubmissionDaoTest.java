package no.kantega.openaksess.forms.database.dao;

import no.kantega.publishing.api.forms.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:testContext.xml")
public class DatabaseFormSubmissionDaoTest {

    @Autowired
    private FormSubmissionDao dao;

    @Test
    public void testGetFormSubmissionById() {
        FormSubmission output = dao.getFormSubmissionById(1);

        assertEquals("donald@duck.com", output.getSubmittedByEmail());
        assertEquals(3, output.getValues().size());
    }

    @Test
    public void testGetFormSubmissionsByFormId() {
        List submissions = dao.getFormSubmissionsByFormId(1);
        assertEquals(4, submissions.size());
    }

    @Test
    public void testGetFormSubmissionsByFormIdAndIdentity() {
        List submissions = dao.getFormSubmissionsByFormIdAndIdentity(1, "donald");
        assertEquals(1, submissions.size());
    }

    @Test
    public void testSaveFormSubmission() {
        DefaultForm form = new DefaultForm();
        form.setId(100);
        form.setTitle("TestForm");

        DefaultFormSubmission formSubmission = new DefaultFormSubmission();

        formSubmission.setFormSubmissionId(11);
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

        dao.saveFormSubmission(formSubmission);
    }

    @Test
    public void testGetFieldNamesForForm() {
        List fieldNames = dao.getFieldNamesForForm(1);

        assertEquals(3, fieldNames.size());        
    }
}
