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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import java.io.StringWriter;
import java.io.StringReader;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 7, 2009
 * Time: 12:25:01 PM
 */
public class IdAndNameFilterTest  {

    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        IdAndNameFilter filter = new IdAndNameFilter();
        pipeline.addFilter(filter);

        String input = "<a id=\"bookmark with spaces\" name=\"bookmark with spaces\"></a>";
        String expectedOutput = "<a id=\"bookmark_with_spaces\" name=\"bookmark_with_spaces\"></a>";
        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a id=\" bookmark\" name=\"bookmark\"></a>";
        expectedOutput = "<a id=\"bookmark\" name=\"bookmark\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a id=\"\u00E6\u00F8\u00E5\" name=\"\u00E6\u00F8\u00E5\"></a>";
        expectedOutput = "<a id=\"aoa\" name=\"aoa\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a id=\"123\" name=\"123\"></a>";
        expectedOutput = "<a id=\"b_123\" name=\"b_123\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a id=\"abc*\" name=\"abc*\"></a>";
        expectedOutput = "<a id=\"abc-\" name=\"abc-\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a id=\"abc+\" name=\"abc+\"></a>";
        expectedOutput = "<a id=\"abc-\" name=\"abc-\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());
    }

    /**
     * Necessary for running JUnit 4.x tests in Maven 1.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(IdAndNameFilterTest.class);
    }
}

