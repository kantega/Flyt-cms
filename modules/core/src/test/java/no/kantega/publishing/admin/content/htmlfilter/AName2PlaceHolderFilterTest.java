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

import junit.framework.TestCase;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.commons.exception.SystemException;

import java.io.StringReader;
import java.io.StringWriter;

public class AName2PlaceHolderFilterTest extends TestCase {

    public void testFilter() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        AName2PlaceHolderFilter filter = new AName2PlaceHolderFilter();
        pipeline.addFilter(filter);

        String input = "<a id=\"bookmark\" name=\"bookmark\"></a>";
        String expectedOutput ="<img id=\"bookmark\" name=\"bookmark\" src=\"../bitmaps/common/placeholder/anchor.gif\">";
        StringWriter stringWriter = new StringWriter();
        pipeline.filter(new StringReader(input), stringWriter);
        assertEquals(expectedOutput, stringWriter.toString());

        input = "<a id=\"bookmark with spaces\" name=\"bookmark with spaces\"></a>";
        expectedOutput ="<img id=\"bookmark_with_spaces\" name=\"bookmark_with_spaces\" src=\"../bitmaps/common/placeholder/anchor.gif\">";
        stringWriter = new StringWriter();
        pipeline.filter(new StringReader(input), stringWriter);
        assertEquals(expectedOutput, stringWriter.toString());
    }
    
}
