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
import junit.framework.JUnit4TestAdapter;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.exception.SystemException;
import java.io.StringWriter;
import java.io.StringReader;

/**
 * @author andska, jogri
 */

public class PlaceHolder2ANameFilterTest {

    @Test
    public void testStartElement() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        PlaceHolder2ANameFilter filter = new PlaceHolder2ANameFilter();
        pipeline.addFilter(filter);

        String input = "<img id=\"bookmark\" name=\"bookmark\" src=\"../bitmaps/common/placeholder/anchor.gif\">";
        String expectedOutput = "<a id=\"bookmark\" name=\"bookmark\"></a>";
        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<img id=\"bookmark\" src=\"../bitmaps/common/placeholder/anchor.gif\">";
        expectedOutput = "<a id=\"bookmark\" name=\"bookmark\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());

        input = "<img name=\"bookmark\" src=\"../bitmaps/common/placeholder/anchor.gif\">";
        expectedOutput = "<a id=\"bookmark\" name=\"bookmark\"></a>";
        sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(expectedOutput, sw.toString());
    }

    /**
     * Necessary for running JUnit 4.x tests in Maven 1.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(PlaceHolder2ANameFilterTest.class);
    }
}
