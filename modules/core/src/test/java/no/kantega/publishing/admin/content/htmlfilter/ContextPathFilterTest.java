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

import no.kantega.commons.xmlfilter.FilterPipeline;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ContextPathFilterTest {

    @Test
    public void testStartElement() {
        ContextPathFilter contextPathFilter = new ContextPathFilter();
        contextPathFilter.setContextPath("/test");
        contextPathFilter.setRootUrlToken("<@WEB@>");
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.addFilter(contextPathFilter);

        String input = "<a href=\"/test/content.ap?thisId=3\">link text</a>";
        String expectedOutput = "<a href=\"<@WEB@>/content.ap?thisId=3\">link text</a>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<img href=\"/test/content.ap?thisId=3\">";
        expectedOutput = "<img href=\"<@WEB@>/content.ap?thisId=3\">";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<a href=\"/test2/content.ap?thisId=3\">link text</a>";
        expectedOutput = input;
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<a href=\"../content.ap?thisId=3\">link text</a>";
        expectedOutput = "<a href=\"<@WEB@>/content.ap?thisId=3\">link text</a>";
        assertEquals(expectedOutput, pipeline.filter(input));

    }
}
