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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.util.Properties;

public abstract class ApplicationContextUtils {

    public static void addAutowiredSupport(ConfigurableWebApplicationContext wac) {
        wac.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

                final AutowiredAnnotationBeanPostProcessor autowireProcessor = new AutowiredAnnotationBeanPostProcessor();
                autowireProcessor.setBeanFactory(beanFactory);
                beanFactory.addBeanPostProcessor(autowireProcessor);

            }
        });
    }

    public static void addAppDirPropertySupport(ConfigurableWebApplicationContext wac) {
        PropertyPlaceholderConfigurer configurer  = new PropertyPlaceholderConfigurer();
        final Properties properties = new Properties();
        File dataDir = (File) wac.getServletContext().getAttribute(OpenAksessContextLoaderListener.APPLICATION_DIRECTORY);
        properties.setProperty("appDir", dataDir.getAbsolutePath());
        configurer.setProperties(properties);
        configurer.setIgnoreUnresolvablePlaceholders(true);
        wac.addBeanFactoryPostProcessor(configurer);
    }

}
