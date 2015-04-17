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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 28, 2009
 * Time: 12:33:02 PM
 */
public class CompoundResourceLoaderTest {

    @Test
    public void testCompoundInputStreamSource() {
        // Given two named input sources
        ResourceLoader a = mock(ResourceLoader.class);
        ResourceLoader b = mock(ResourceLoader.class);

        Resource aResource = new ByteArrayResource("a".getBytes());
        Resource bResource = new ByteArrayResource("b".getBytes());

        when(a.getResource("a")).thenReturn(aResource);
        when(b.getResource("b")).thenReturn(bResource);

        CompoundResourceLoader compound = new CompoundResourceLoader();

        // When no sources are used, should return null
        assertNull(compound.getResource("a"));


        // Adding a should return a
        compound.addResourceLoader(a);
        assertSame(aResource, compound.getResource("a"));

        // Adding a should return null for b
        assertNull(compound.getResourceByPath("b"));

        // Adding b should return b
        compound.addResourceLoader(b);
        assertSame(bResource, compound.getResource("b"));

        // A should still return a
        assertSame(aResource, compound.getResource("a"));
    }
}
