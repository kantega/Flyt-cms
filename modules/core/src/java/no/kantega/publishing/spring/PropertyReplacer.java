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

package no.kantega.publishing.spring;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.Aksess;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Properties;
import java.io.File;

/**
 *
 */
public class PropertyReplacer implements BeanFactoryPostProcessor, ServletContextAware {
    private ServletContext servletContext;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

        try {
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            Properties properties = new Properties(Aksess.getConfiguration().getProperties());

            File dataDir = (File) servletContext.getAttribute(OpenAksessContextLoaderListener.APPLICATION_DIRECTORY);
            properties.setProperty("appDir", dataDir.getAbsolutePath());

            cfg.setProperties(properties);
            cfg.postProcessBeanFactory(configurableListableBeanFactory);

        } catch (ConfigurationException e) {
            System.out.println("Error getting configuration");
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
