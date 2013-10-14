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

import no.kantega.publishing.common.Aksess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;


public class SecurityBeanLoader implements BeanFactoryPostProcessor, ResourceLoaderAware {

    private String defaultSecurityResource;
    private ResourceLoader resourceLoader;

    private static final Logger log = LoggerFactory.getLogger(SecurityBeanLoader.class);

    public void postProcessBeanFactory(ConfigurableListableBeanFactory context) throws BeansException {

            String securityDefinitionResource = Aksess.getConfiguration().getString("security.definition");

            if (securityDefinitionResource == null) {
                securityDefinitionResource = defaultSecurityResource;
                log.info("Loading default security definition from " + securityDefinitionResource);
            } else {
                log.info("Loading custom security definition from " + securityDefinitionResource);
            }

            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;

            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);

            reader.loadBeanDefinitions(resourceLoader.getResource(securityDefinitionResource));
    }


    public void setDefaultSecurityResource(String defaultSecurityResource) {
        this.defaultSecurityResource = defaultSecurityResource;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
