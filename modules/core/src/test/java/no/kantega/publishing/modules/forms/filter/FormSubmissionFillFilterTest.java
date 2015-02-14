package no.kantega.publishing.modules.forms.filter;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.modules.forms.validate.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class FormSubmissionFillFilterTest {

    private FormElementValidatorFactory formElementValidatorFactory;

    @Before
    public void setup(){
        formElementValidatorFactory = new FormElementValidatorFactory();
        formElementValidatorFactory.setFormElementValidators(
                asList(
                        new FormElementDateValidator(),
                        new FormElementEmailValidator(),
                        new FormElementNumberValidator(),
                        new FormElementNorwegianSsnValidator(),
                        new FormElementRegExValidator()
                ));
    }

    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();
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

            public List<String> getFieldNames() {
                return null;
            }

            public String getUrl() {
                return "http://www.valid.url";
            }
        };

        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(params, form, true);

        pipeline.addFilter(filter);

        pipeline.filter(form.getFormDefinition());

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

    @Test
    public void testShouldPickupValueForRecipientEmail() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();

        String recipientEmail = "test@kantega.no";
        params.put("email", new String[] {recipientEmail});
        params.put("email2", new String[] {"test2@kantega.no"});


        Form form = new Form() {
            public int getId() {
                return 0;
            }

            public String getTitle() {
                return "title";
            }

            public String getFormDefinition() {
                return "<div class=\"formElement\"><input name=\"email\" type=\"text\" id=\"RecipientEmail\"><br><input name=\"email2\" type=\"text\"></div>";
            }

            public String getEmail() {
                return "donald@duck.com";
            }

            public List<String> getFieldNames() {
                return null;
            }

            public String getUrl() {
                return "http://www.valid.url";
            }
        };

        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(params, form, true);

        pipeline.addFilter(filter);

        pipeline.filter(form.getFormDefinition());

        FormSubmission formSubmission = filter.getFormSubmission();

        assertEquals(recipientEmail, formSubmission.getSubmittedByEmail());
    }

    @Test
    public void shouldGetEverything(){
        Form form = new Form() {
            public int getId() {
                return 0;
            }

            public String getTitle() {
                return "title";
            }

            public String getFormDefinition() {
                return "<div class=\"formElement\"><div class=\"heading\"><label>Felt 1ytu</label></div><div class=\"inputs text\"><input type=\"text\" name=\"Felt 1ytu\" size=\"20\"></div><div class=\"helpText\">tyu</div></div>\n" +
                       "<div class=\"formElement\"><div class=\"heading\"><label>Felt 2</label></div><div class=\"inputs textarea\"><textarea rows=\"3\" cols=\"40\" name=\"Felt 2\"></textarea></div></div>\n" +
                       "<div class=\"formElement\"><div class=\"heading\"><label>Felt 3</label> (dd.mm.åååå)</div><div class=\"inputs text\"><span class=\"dateformat\" style=\"display:none\">dd.MM.yyyy</span><input type=\"text\" name=\"Felt 3\" size=\"10\" maxlength=\"10\" class=\"date\"></div></div>\n" +
                       "<div class=\"formElement\"><div class=\"heading\"><label>Felt 4</label></div><div class=\"inputs text\"><input type=\"text\" name=\"Felt 4\" size=\"20\" class=\"email\"></div></div>\n" +
                       "<div class=\"formElement\"><div class=\"heading\"><label>Felt 5</label></div><div class=\"inputs text\"><input type=\"text\" name=\"Felt 5\" size=\"20\" class=\"number\"></div></div>\n" +
                       "<div class=\"formElement\"><div class=\"heading\"><label>Felt 6</label></div><div class=\"inputs text\"><span class=\"regex\" style=\"display:none\">\\d+</span><input type=\"text\" name=\"Felt 6\" size=\"20\" class=\"regularexpression\"></div></div>";
            }

            public String getEmail() {
                return "donald@duck.com";
            }

            public List<String> getFieldNames() {
                return null;
            }

            public String getUrl() {
                return "http://www.valid.url";
            }
        };
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();
        params.put("Felt 6", new String[]{"lol123"});
        FormSubmissionFillFilter filter = new FormSubmissionFillFilter(params, form, true);
        filter.setFormElementValidatorFactory(formElementValidatorFactory);

        pipeline.addFilter(filter);

        pipeline.filter(form.getFormDefinition());

        FormSubmission formSubmission = filter.getFormSubmission();
        List<FormValue> values = formSubmission.getValues();
        assertThat(values.size(), is(1));
        assertThat(filter.getErrors().size(), is(1));

        assertThat(filter.getErrors().get(0).field, is("Felt 6"));
        assertThat(values.get(0).getName(), is("Felt 6"));
    }
}
