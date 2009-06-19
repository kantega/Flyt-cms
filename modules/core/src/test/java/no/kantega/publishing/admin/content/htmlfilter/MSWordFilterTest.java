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

import java.io.StringWriter;
import java.io.StringReader;

public class MSWordFilterTest extends TestCase {

    FilterPipeline pipeline = SharedPipeline.getFilterPipeline();

    public void testStripFontTag() throws SystemException {
        pipeline.removeFilters();
        pipeline.addFilter(new MSWordFilter());

        String input="<font size=1>hello</font>";
        String output = "hello";

        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());

    }
    public void testRemoveOPTag() throws SystemException {
        pipeline.removeFilters();
        pipeline.addFilter(new MSWordFilter());

        String input="<html><body><o:p>tada</o:p></body></html>";
        String output = "<html><body>tada</body></html>";

        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());

    }
    public void testRemoveSpanTag() throws SystemException {
        pipeline.removeFilters();
        pipeline.addFilter(new MSWordFilter());

        String input="<html><body><span>tada</span></body></html>";
        String output = "<html><body>tada</body></html>";

        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());

    }
    /*
    public void testRemoveDivTag() throws SystemException {
        pipeline.removeFilters();
        pipeline.addFilter(new MSWordFilter());

        String input="<html><body><div>tada</div></body></html>";
        String output = "<html><body>tada</body></html>";

        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());

    }*/

    public void testRemoveMsClassTag() throws SystemException {
        pipeline.removeFilters();
        pipeline.addFilter(new MSWordFilter());

        String input="<html><body><p class=\"MsClass\">tada</p></body></html>";
        String output = "<html><body><p>tada</p></body></html>";

        StringWriter  sw = new StringWriter();
        pipeline.filter(new StringReader(input), sw);
        assertEquals(output, sw.toString());

    }
}
