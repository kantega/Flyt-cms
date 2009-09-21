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
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.exception.SystemException;
import java.io.StringWriter;
import java.io.StringReader;

/**
 * @author jogri
 */
//@Ignore
public class AmpersandFilterTest {

    private static AmpersandFilter ampersandFilter;
    private static FilterPipeline pipeline;

    @BeforeClass
    public static void init() {
        ampersandFilter = new AmpersandFilter();
        pipeline = SharedPipeline.getFilterPipeline();
        pipeline.addFilter(ampersandFilter);
    }

    @Test
    public void testStartElement() throws SystemException {
        // One "&amp;" entity missing
        String input = "<a href=\"http://kantega.no/content.ap?thisId=123&someThing\">link text</a>";
        String expectedOutput = "<a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing\">link text</a>";
        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        // Several "&amp;" entities missing
        input = "<img src=\"image.gif?width=10&height=10\"><a href=\"http://kantega.no/content.ap?thisId=123&someThing&anotherThing\">link text</a>";
        expectedOutput = "<img src=\"image.gif?width=10&amp;height=10\"><a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing&amp;anotherThing\">link text</a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        // One "&amp;" entity already existing and one missing
        input = "<a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing&anotherThing\">link text</a>";
        expectedOutput = "<a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing&amp;anotherThing\">link text</a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        // One "&amp;" entity missing and one already existing
        input = "<a href=\"http://kantega.no/content.ap?thisId=123&someThing&amp;anotherThing\">link text</a>";
        expectedOutput = "<a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing&amp;anotherThing\">link text</a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        // One "&amp;" entity already existing
        input = "<a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing\">link text</a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(input, sw.toString());

        // Several "&amp;" entities already existing
        input = "<a href=\"http://kantega.no/content.ap?thisId=123&amp;someThing&amp;anotherThing\">link text</a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(input, sw.toString());

        // Missing "href" attribute
        input = "<a name=\"someName\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(input, sw.toString());
    }

    /**
     * Necessary for running JUnit 4.x tests in Maven 1.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AmpersandFilterTest.class);
    }
}
