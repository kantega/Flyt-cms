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

public class CleanupFormHtmlFilterTest {

    @Test
    public void testAddTextTypeOnInputElementsWithoutType() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.addFilter(new CleanupFormHtmlFilter());

        String input="<input name=\"inputName\">";
        String output = "<input name=\"inputName\" type=\"text\">";

        assertEquals(output, pipeline.filter(input));
    }

    @Test
    public void testNoModificationOfTextTypeInputElements() {
        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(new CleanupFormHtmlFilter());

        String input="<input name=\"inputName\" type=\"text\">";
        String output = "<input name=\"inputName\" type=\"text\">";

        assertEquals(output, pipeline.filter(input));
    }

    @Test
    public void testNoModificationOfRadioTypeInputElements() {
        FilterPipeline pipeline = new FilterPipeline();

        pipeline.addFilter(new CleanupFormHtmlFilter());

        String input="<input type=\"radio\" name=\"inputName\">";
        String output = "<input type=\"radio\" name=\"inputName\">";

        assertEquals(output, pipeline.filter(input));
    }

}
