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

package no.kantega.publishing.common.data;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.context.support.ServletContextResourceLoader;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ServletResourceLoaderTest {
    @Test
    public void testGetInputStream() throws IOException {


        ServletContextResourceLoader loader = mock(ServletContextResourceLoader.class);

        ServletResourceLoader source = new ServletResourceLoader();
        source.setResourceLoader(loader);

        ByteArrayResource webinfSource = new ByteArrayResource("test".getBytes());
        when(loader.getResource("/WEB-INF/web.xml")).thenReturn(webinfSource);

        ByteArrayResource rootSource = new ByteArrayResource("test".getBytes());
        when(loader.getResource("web.xml")).thenReturn(rootSource);

        assertEquals(rootSource, source.getResource("web.xml"));

        source.setPrefix("/WEB-INF/");

        assertEquals(webinfSource, source.getResource("web.xml"));
    }
}
