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

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import no.kantega.commons.xmlfilter.FilterPipeline;

/**
 *
 */
public class ContextPathFilterTest {
    private static ContextPathFilter contextPathFilter;
    private static FilterPipeline pipeline;

    @BeforeClass
    public static void init() {
        contextPathFilter = new ContextPathFilter();
        contextPathFilter.setContextPath("/test");
        contextPathFilter.setRootUrlToken("<@WEB@>");
        pipeline = SharedPipeline.getFilterPipeline();
        pipeline.addFilter(contextPathFilter);
    }

    @Test
    public void testStartElement() {
        String input = "<a href=\"/test/content.ap?thisId=3\">link text</a>";
        String expectedOutput = "<a href=\"<@WEB@>/content.ap?thisId=3\">link text</a>";
        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<img href=\"/test/content.ap?thisId=3\">";
        expectedOutput = "<img href=\"<@WEB@>/content.ap?thisId=3\">";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a href=\"/test2/content.ap?thisId=3\">link text</a>";
        expectedOutput = input;
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<a href=\"../content.ap?thisId=3\">link text</a>";
        expectedOutput = "<a href=\"/test/content.ap?thisId=3\">link text</a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

    }
}
