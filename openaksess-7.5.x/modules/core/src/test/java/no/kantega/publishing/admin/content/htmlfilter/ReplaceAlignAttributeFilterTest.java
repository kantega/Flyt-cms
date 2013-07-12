/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.htmlfilter;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.exception.SystemException;

import java.io.StringWriter;
import java.io.StringReader;

import junit.framework.JUnit4TestAdapter;

/**
 * @jogri
 */
//@Ignore
public class ReplaceAlignAttributeFilterTest {

    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        ReplaceAlignAttributeFilter filter = new ReplaceAlignAttributeFilter();
        pipeline.addFilter(filter);

        String input = "<p>...</p>";
        String expectedOutput = "<p>...</p>";
        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<div>...</div>";
        expectedOutput = "<div>...</div>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<div>...</div>";
        expectedOutput = "<div>...</div>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<p align=\"left\">...</p>";
        expectedOutput = "<p style=\"text-align: left;\">...</p>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<p align=\"center\">...</p>";
        expectedOutput = "<p style=\"text-align: center;\">...</p>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<p align=\"right\">...</p>";
        expectedOutput = "<p style=\"text-align: right;\">...</p>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<div align=\"left\">...</div>";
        expectedOutput = "<div style=\"text-align: left;\">...</div>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<div align=\"center\">...</div>";
        expectedOutput = "<div style=\"text-align: center;\">...</div>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<div align=\"right\">...</div>";
        expectedOutput = "<div style=\"text-align: right;\">...</div>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<div align=\"right\"><p align=\"right\">...</p></div>";
        expectedOutput = "<div style=\"text-align: right;\"><p style=\"text-align: right;\">...</p></div>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());
    }

    /**
     * Necessary for running JUnit 4.x tests in Maven 1.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ReplaceAlignAttributeFilterTest.class);
    }
}