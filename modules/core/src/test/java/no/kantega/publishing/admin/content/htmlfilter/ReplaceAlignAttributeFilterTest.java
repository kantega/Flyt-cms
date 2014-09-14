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

import static org.junit.Assert.assertEquals;

public class ReplaceAlignAttributeFilterTest {

    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();
        ReplaceAlignAttributeFilter filter = new ReplaceAlignAttributeFilter();
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

        input = "<p align=\"left\">...</p>";
        expectedOutput = "<p style=\"text-align: left;\">...</p>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<p align=\"center\">...</p>";
        expectedOutput = "<p style=\"text-align: center;\">...</p>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<p align=\"right\">...</p>";
        expectedOutput = "<p style=\"text-align: right;\">...</p>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div align=\"left\">...</div>";
        expectedOutput = "<div style=\"text-align: left;\">...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div align=\"center\">...</div>";
        expectedOutput = "<div style=\"text-align: center;\">...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div align=\"right\">...</div>";
        expectedOutput = "<div style=\"text-align: right;\">...</div>";
        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<div align=\"right\"><p align=\"right\">...</p></div>";
        expectedOutput = "<div style=\"text-align: right;\"><p style=\"text-align: right;\">...</p></div>";
        assertEquals(expectedOutput, pipeline.filter(input));
    }

}
