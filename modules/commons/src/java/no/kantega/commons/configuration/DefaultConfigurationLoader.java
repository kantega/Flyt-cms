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

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bjorsnos
 * Date: May 29, 2009
 * Time: 12:47:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultConfigurationLoader implements ConfigurationLoader {
    private List<String> resources = new ArrayList<String>();

    private ResourceLoader resourceLoader;

    private Logger log = Logger.getLogger(getClass());

    public DefaultConfigurationLoader() {

    }

    public DefaultConfigurationLoader(ResourceLoader resourceLoader, String... resources) {
        this.resources = new ArrayList(Arrays.asList(resources));
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
                        properties.load(resource.getInputStream());
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
        this.resources = new ArrayList(Arrays.asList(resources));
    }

    public void addResource(String resourceLocation) {
        this.resources.add(resourceLocation);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
