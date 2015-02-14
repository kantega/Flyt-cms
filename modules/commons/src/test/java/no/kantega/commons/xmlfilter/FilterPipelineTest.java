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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilterPipelineTest {
    private FilterPipeline filterPipeline;

    @Before
    public void setUp() throws Exception {
        filterPipeline = new FilterPipeline();
    }

    @Test
    public void testEmptyPipeline() {
        filterPipeline.removeFilters();
        String pre = "<html><head>";
        String meta = "</head>";
        String mainContent = "Dette er en test med æøå ÆØÅ";
        String body = "<body class=\"bodyclass\">" + mainContent + "</body></html>";
        String input = pre + body;
        String filtered = filterPipeline.filter(input);
        assertThat(filtered, is(mainContent));

    }



}
