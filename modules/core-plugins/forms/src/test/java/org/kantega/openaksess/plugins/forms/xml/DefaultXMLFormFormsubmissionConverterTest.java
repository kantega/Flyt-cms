package org.kantega.openaksess.plugins.forms.xml;

import no.kantega.publishing.api.forms.model.DefaultFormSubmission;
import no.kantega.publishing.api.forms.model.DefaultFormValue;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import org.junit.Test;
import org.kantega.openaksess.plugins.xml.DefaultXMLFormFormsubmissionConverter;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


public class DefaultXMLFormFormsubmissionConverterTest {
    @Test
    public void testCreateXMLFromFormSubmission() throws Exception {
        FormSubmission formSubmission = createFormSubmission();

        DefaultXMLFormFormsubmissionConverter converter = new DefaultXMLFormFormsubmissionConverter();
        String xml = converter.createXMLFromFormSubmission(formSubmission);

        assertNotNull("xml != null", xml);
        assertTrue("Contains <formsubmission>", xml.contains("<formsubmission>"));
        assertTrue("Contains <formname>myform</formname>", xml.contains("<formname>myform</formname>"));
        assertTrue("Contains <value>anders</value>", xml.contains("<value>anders</value>"));
    }

    private FormSubmission createFormSubmission() {
        DefaultFormSubmission formSubmission = new DefaultFormSubmission();
        Form form = new Form() {

            public int getId() {
                return 0;
            }

            public String getTitle() {
                return "myform";
            }

            public String getFormDefinition() {
                return "";
            }

            public String getEmail() {
                return null;
            }

            public List<String> getFieldNames() {
                return null;
            }

            public String getUrl() {
                return null;
            }
        };

        formSubmission.setForm(form);

        DefaultFormValue name = new DefaultFormValue();
        name.setName("name");
        name.setValue("anders");

        formSubmission.addValue(name);

        DefaultFormValue email = new DefaultFormValue();
        email.setName("email");
        email.setValue("noreply@kantega.no");

        formSubmission.addValue(email);

        DefaultFormValue fv = new DefaultFormValue();
        fv.setName("test");
        fv.setValue("A test with & &x; special chars <br>");
        formSubmission.addValue(fv);

        DefaultFormValue url = new DefaultFormValue();
        url.setName("url");
        url.setValue("http://www.valid.url");

        return formSubmission;
    }
}
