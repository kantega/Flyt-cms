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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * Loads configuration and exposes its values as placeholders.
 */
public class PropertyReplacer implements BeanFactoryPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(PropertyReplacer.class);

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

        try {
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            Properties properties = new Properties(Aksess.getConfiguration().getProperties());

            properties.setProperty("appDir", Configuration.getApplicationDirectory());

            cfg.setProperties(properties);
            cfg.postProcessBeanFactory(configurableListableBeanFactory);

        } catch (ConfigurationException e) {
            log.error( "Error getting configuration", e);
        }
    }
}
