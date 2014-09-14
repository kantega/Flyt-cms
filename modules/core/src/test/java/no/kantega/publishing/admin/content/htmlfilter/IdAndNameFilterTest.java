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

public class IdAndNameFilterTest  {

    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();
        IdAndNameFilter filter = new IdAndNameFilter();
        pipeline.addFilter(filter);

        String input = "<a id=\"bookmark with spaces\" name=\"bookmark with spaces\"></a>";
        String expectedOutput = "<a id=\"bookmark_with_spaces\" name=\"bookmark_with_spaces\"></a>";

        assertEquals(expectedOutput, pipeline.filter(input));

        input = "<a id=\" bookmark\" name=\"bookmark\"></a>";
        expectedOutput = "<a id=\"bookmark\" name=\"bookmark\"></a>";
        assertEquals(expectedOutput, pipeline.filter(input));


        input = "<a id=\"\u00E6\u00F8\u00E5\" name=\"\u00E6\u00F8\u00E5\"></a>";
        expectedOutput = "<a id=\"aoa\" name=\"aoa\"></a>";
        assertEquals(expectedOutput, pipeline.filter(input));


        input = "<a id=\"123\" name=\"123\"></a>";
        expectedOutput = "<a id=\"b_123\" name=\"b_123\"></a>";
        assertEquals(expectedOutput, pipeline.filter(input));


        input = "<a id=\"abc*\" name=\"abc*\"></a>";
        expectedOutput = "<a id=\"abc\" name=\"abc\"></a>";
        assertEquals(expectedOutput, pipeline.filter(input));


        input = "<a id=\"abc+\" name=\"abc+\"></a>";
        expectedOutput = "<a id=\"abc-\" name=\"abc-\"></a>";
        assertEquals(expectedOutput, pipeline.filter(input));

    }

}

