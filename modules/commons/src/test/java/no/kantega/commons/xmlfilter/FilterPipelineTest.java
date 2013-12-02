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

package no.kantega.commons.xmlfilter;

import junit.framework.TestCase;
import no.kantega.commons.exception.SystemException;

import java.io.StringReader;
import java.io.StringWriter;

public class FilterPipelineTest extends TestCase {
    private FilterPipeline filterPipeline;

    protected void setUp() throws Exception {
        filterPipeline = new FilterPipeline();
    }

    public void testEmptyPipeline() {
        filterPipeline.removeFilters();
        String pre = "<html><head>";
        String meta = "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>";
        String body = "<body>Dette er en test med æøå ÆØÅ</body></html>";
        String input = pre + body;        
        StringWriter sw = new StringWriter();
        try {
            filterPipeline.filter(new StringReader(input), sw);
            assertEquals(pre + meta + "<body>Dette er en test med &aelig;&oslash;&aring; &AElig;&Oslash;&Aring;</body></html>", sw.toString());
            //
        } catch (SystemException e) {
            fail(e.getMessage());
        }

    }

    

}
