package no.kantega.publishing.admin.content.htmlfilter;

import no.kantega.commons.xmlfilter.FilterPipeline;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConvertUnderlineToEditorStyleFilterTest {

    @Test
    public void replaceUnderlineWithU(){
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.addFilter(new ConvertUnderlineToEditorStyleFilter());

        assertThat(pipeline.filter("<span style=\"text-decoration:underline;\">text</span>"),
                is("<u>text</u>"));
    }
}