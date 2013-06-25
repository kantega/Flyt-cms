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

package no.kantega.commons.configuration;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultConfigurationLoaderTest {


    @Test
    public void testLoadConfiguration() {
         // Given

        DefaultConfigurationLoader loader = new DefaultConfigurationLoader();
        loader.setResourceLoader(new DefaultResourceLoader());
        final String file1 = "classpath:no/kantega/commons/configuration/file1.properties";
        final String file2 = "classpath:no/kantega/commons/configuration/file2.properties";
        // Does not exist
        final String file3 = "classpath:no/kantega/commons/configuration/file3.properties";
        loader.setResources(file1);

        // When

        Properties props = loader.loadConfiguration();

        // Expect
        assertEquals(1, props.keySet().size());
        assertTrue(props.keySet().contains("key"));
        assertEquals("a", props.getProperty("key"));

        // When
        loader.setResources(file1,file2, file3);
        props = loader.loadConfiguration();

        // Expect
        assertEquals(2, props.keySet().size());
        assertTrue(props.keySet().contains("key"));
        assertTrue(props.keySet().contains("key2"));
        assertEquals("b", props.getProperty("key"));
        assertEquals("c", props.getProperty("key2"));

    }
}
