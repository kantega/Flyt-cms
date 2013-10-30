/*
 * Copyright 2011 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class RemoveNestedSpanTagsFilterTest {
    @Ignore
    @Test
    public void shouldRemoveSimpleNestedTag() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        RemoveNestedSpanTagsFilter filter = new RemoveNestedSpanTagsFilter();
        pipeline.addFilter(filter);

        String htmlBefore = "<span><span>test</span></span>";
        String expectedHtmlAfter = "<span>test</span>";

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(htmlBefore), sw);

        assertEquals(expectedHtmlAfter, sw.toString());
    }

    @Test
    public void shouldNotRemoveNestedTagsWithDifferentClass() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        RemoveNestedSpanTagsFilter filter = new RemoveNestedSpanTagsFilter();
        pipeline.addFilter(filter);

        String htmlBefore = "<span class=\"class1\"><span class=\"class2\">test</span></span>";
        String expectedHtmlAfter = htmlBefore;

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(htmlBefore), sw);

        assertEquals(expectedHtmlAfter, sw.toString());
    }

    @Test
    public void shouldRemoveNestedTagWithoutRemovingInnerTags() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        RemoveNestedSpanTagsFilter filter = new RemoveNestedSpanTagsFilter();
        pipeline.addFilter(filter);

        String htmlBefore = "<span><span><p><span>test</span></p></span></span>";
        String expectedHtmlAfter = "<span><p><span>test</span></p></span>";

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(htmlBefore), sw);

        assertEquals(expectedHtmlAfter, sw.toString());
    }

    @Test
    public void shouldRemoveNestedTagWithSameStyle() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        RemoveNestedSpanTagsFilter filter = new RemoveNestedSpanTagsFilter();
        pipeline.addFilter(filter);

        String htmlBefore = "<p><strong><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"> </span></span></span></span></span></span></span></span></span></span></span></span></span></span></strong></p>";
        String expectedHtmlAfter = "<p><strong><span style=\"text-decoration: underline;\"> </span></strong></p>";

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(htmlBefore), sw);

        assertEquals(expectedHtmlAfter, sw.toString());
    }

    @Test
    public void shouldRemoveNestedTagWithMultipelSimpleSpans() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        RemoveNestedSpanTagsFilter filter = new RemoveNestedSpanTagsFilter();
        pipeline.addFilter(filter);

        String htmlBefore = "<p><span style=\"font-size: 1em;\"><span><span>ff</span></span></span></p>";
        String expectedHtmlAfter = "<p><span style=\"font-size: 1em;\"><span>ff</span></span></p>";

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(htmlBefore), sw);

        assertEquals(expectedHtmlAfter, sw.toString());
    }

    @Test
    public void shouldRemoveNestedTagLeavingOneTag() throws SystemException {
        FilterPipeline pipeline = SharedPipeline.getFilterPipeline();
        RemoveNestedSpanTagsFilter filter = new RemoveNestedSpanTagsFilter();
        pipeline.addFilter(filter);

        String htmlBefore = "<p><span><span>gg<span>ff</span></span></span></p>";
        String expectedHtmlAfter = "<p><span>ggff</span></p>";

        StringWriter sw = new StringWriter();
        pipeline.filter(new StringReader(htmlBefore), sw);

        assertEquals(expectedHtmlAfter, sw.toString());
    }

}
