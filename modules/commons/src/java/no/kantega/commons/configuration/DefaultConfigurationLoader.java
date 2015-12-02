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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DefaultConfigurationLoader implements ConfigurationLoader {
    private List<String> resources = new ArrayList<>();

    private ResourceLoader resourceLoader;

    private static final Logger log = LoggerFactory.getLogger(DefaultConfigurationLoader.class);

    public DefaultConfigurationLoader() {

    }

    public DefaultConfigurationLoader(ResourceLoader resourceLoader, String... resources) {
        this.resources = new ArrayList<>(Arrays.asList(resources));
        this.resourceLoader = resourceLoader;
    }

    public Properties loadConfiguration() {
        Properties properties = new Properties();

        if(resources != null) {
            for(String resourceLocation : resources)
                try {
                    final Resource resource = resourceLoader.getResource(resourceLocation);

                    if(resource.exists()) {
                        log.info("Loading properties from: " +resource.getDescription());
                        try (InputStream is = resource.getInputStream()){
                            properties.load(is);
                        }
                    } else {
                        log.info("Ignoring property resource: " +resource.getDescription() +" because it does not exist");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        return properties;
    }

    public void setResources(String... resources) {
        this.resources = new ArrayList<>(Arrays.asList(resources));
    }

    public void addResource(String resourceLocation) {
        this.resources.add(resourceLocation);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
