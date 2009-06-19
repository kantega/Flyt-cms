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
import static org.junit.Assert.assertEquals;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;


/**
 *
 */
public class RelativeFileSystemResourceLoaderTest {
    @Test
    public void testGetInputStream() throws IOException {

        // Given
        File toReturn = File.createTempFile("test", "");
        toReturn.deleteOnExit();
        IOUtils.write("hello", new FileOutputStream(toReturn));

        RelativeFileSystemResourceLoader loader = new RelativeFileSystemResourceLoader(toReturn.getParentFile());


        // When
        Resource resource = loader.getResource(toReturn.getName());

        // Then
        assertEquals("hello", IOUtils.toString(resource.getInputStream()));

        // Clean up
        toReturn.delete();


    }


}

