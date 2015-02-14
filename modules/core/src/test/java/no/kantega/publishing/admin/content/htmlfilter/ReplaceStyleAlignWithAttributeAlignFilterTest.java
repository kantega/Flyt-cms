package no.kantega.publishing.admin.content.htmlfilter;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReplaceStyleAlignWithAttributeAlignFilterTest {
    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();
        ReplaceStyleAlignWithAttributeAlignFilter filter = new ReplaceStyleAlignWithAttributeAlignFilter();
        pipeline.addFilter(filter);

        String input = "<p>...</p>";
        String expectedOutput = "<p>...</p>";

        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div>...</div>";
        expectedOutput = "<div>...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div>...</div>";
        expectedOutput = "<div>...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<p style=\"text-align: left;\">...</p>";
        expectedOutput = "<p align=\"left\">...</p>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<p style=\"text-align: center;\">...</p>";
        expectedOutput = "<p align=\"center\">...</p>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<p style=\"text-align: right;\">...</p>";
        expectedOutput = "<p align=\"right\">...</p>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div style=\"text-align: left;\">...</div>";
        expectedOutput = "<div align=\"left\">...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div style=\"text-align: center;\">...</div>";
        expectedOutput = "<div align=\"center\">...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div style=\"text-align: right;\">...</div>";
        expectedOutput = "<div align=\"right\">...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div style=\"text-align: right;\"><p style=\"text-align: right;\">...</p></div>";
        expectedOutput = "<div align=\"right\"><p align=\"right\">...</p></div>";
        assertEquals(expectedOutput, pipeline.filter(input));
    }
}