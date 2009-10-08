package no.kantega.publishing.modules.forms.filter;

import no.kantega.publishing.admin.content.htmlfilter.SharedPipeline;
import no.kantega.publishing.modules.forms.filter.FormFillFilter;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.exception.SystemException;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;

/**
 *
 */
public class FormFillFilterTest extends TestCase {

    public void testInputText() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] {"Lorum ipsum"});

        FormFillFilter filter = new FormFillFilter(params);

        pipeline.addFilter(filter);

        String input = "<input type=\"text\" name=\"name\"><input type=\"text\" name=\"email\">";
        String output ="<input type=\"text\" name=\"name\" value=\"Lorum ipsum\"><input type=\"text\" name=\"email\">";
        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());        
    }


   public void testInputTextArea() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] {"Lorum ipsum"});

        FormFillFilter filter = new FormFillFilter(params);

        pipeline.addFilter(filter);

        String input = "<input type=\"text\" name=\"x\" value=\"\"><textarea name=\"name\"></textarea>";
        String output ="<input type=\"text\" name=\"x\" value=\"\"><textarea name=\"name\">Lorum ipsum</textarea>";
        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);

        assertEquals(output, sw.toString());
    }
    

    public void testInputRadio() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("choice", new String[] {"one"});

        FormFillFilter filter = new FormFillFilter(params);

        pipeline.addFilter(filter);

        String input = "<input type=\"radio\" name=\"choice\" value=\"one\"><input type=\"radio\" name=\"choice\" value=\"two\" checked>";
        String output = "<input type=\"radio\" name=\"choice\" value=\"one\" checked><input type=\"radio\" name=\"choice\" value=\"two\">";
        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());

    }

    public void testInputSelect() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("choice", new String[] {"one"});

        FormFillFilter filter = new FormFillFilter(params);

        pipeline.addFilter(filter);

        String input = "<select name=\"choice\"><option value=\"one\">One</option><option value=\"two\">Two</option></select>";
        String output = "<select name=\"choice\"><option value=\"one\" selected>One</option><option value=\"two\">Two</option></select>";
        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());
    }

}