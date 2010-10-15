package no.kantega.publishing.modules.forms.filter;

import junit.framework.TestCase;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.model.FormValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 *
 */
@ContextConfiguration(locations=("application-editableforms.xml"))
public class FormSubmissionFillFilterTest extends TestCase {

    @Autowired
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("customfield", new String[] {"4"});
        params.put("field1", new String[] {"1"});
        params.put("field2", new String[] {"2"});
        params.put("field3", new String[] {"3"});


        Form form = new Form() {
            public int getId() {
                return 0;
            }

            public String getTitle() {
                return "title";
            }

            public String getFormDefinition() {
                return "<div class=\"formElement\"><input name=\"field1\" type=\"text\"><br><input name=\"field2\" type=\"text\"><select name=\"field3\"></select></div>";
            }

            public String getEmail() {
                return "donald@duck.com";
            }
        };

        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(params, form);

        pipeline.addFilter(filter);

        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(form.getFormDefinition()), sw);

        FormSubmission formSubmission = filter.getFormSubmission();
        List<FormValue> values = formSubmission.getValues();
        assertEquals(4, values.size());

        FormValue value = values.get(0);
        assertEquals("1", value.getValues()[0]);

        value = values.get(1);
        assertEquals("2", value.getValues()[0]);

        value = values.get(2);
        assertEquals("3", value.getValues()[0]);

        value = values.get(3);
        assertEquals("4", value.getValues()[0]);

    }
}
