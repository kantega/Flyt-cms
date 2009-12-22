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

import org.simplericity.datadirlocator.DataDirectoryLocatorStrategy;
import org.simplericity.datadirlocator.DefaultDataDirectoryLocator;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.BeansException;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Properties;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.configuration.DefaultConfigurationLoader;
import no.kantega.commons.configuration.ConfigurationLoader;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

/**
 * Custom ContextLoaderListener for Aksess.
 * Locates the application directory and exposes it as a ${appDir} property in the Spring Application Context.
 */
public class OpenAksessContextLoaderListener extends ContextLoaderListener {

    public static final String APPLICATION_DIRECTORY = OpenAksessContextLoaderListener.class.getName() +"_APPLICATION_DIRECTORY";
    @Override
    protected ContextLoader createContextLoader() {
        return new ContextLoader() {
            @Override
            protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext wac) {

                DefaultDataDirectoryLocator locator = new DefaultDataDirectoryLocator();

                locator.setContextParamName("kantega.appDir");
                locator.setSystemProperty("kantega.appDir");

                locator.addStrategy(new LegacyKantegaDirLocatorStrategy());
                locator.setServletContext(servletContext);
                File dataDir = locator.locateDataDirectory();

                Configuration.setApplicationDirectory(dataDir);

                // Make dataDir available as Servlet Context attribute
                servletContext.setAttribute(APPLICATION_DIRECTORY, dataDir);
                
                // Set up @Autowired support
                ApplicationContextUtils.addAutowiredSupport(wac);

                // Add ${appDir} property for the Spring Context
                ApplicationContextUtils.addAppDirPropertySupport(wac);

                ConfigurationLoader loader = createConfigurationLoader(servletContext);

                final Properties properties = loader.loadConfiguration();
                final Configuration configuration = new Configuration(properties);

                Configuration.setDefaultConfiguration(configuration);

                // Set and load configuration on these classes since they are not DI-based (hackish..)
                Aksess.setConfiguration(configuration);
                Aksess.loadConfiguration();

                dbConnectionFactory.setConfiguration(configuration);
                dbConnectionFactory.loadConfiguration();

                // Add the Configuration and the ConfigurationLoader as Spring beans
                addConfigurationInstance(wac, configuration, loader);

                // Replace ${} properties in Spring with config properties
                exposeConfigurationProperties(wac, properties);

            }
        };
    }

    private void exposeConfigurationProperties(ConfigurableWebApplicationContext wac, final Properties properties) {
        wac.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
                cfg.setProperties(properties);
                cfg.postProcessBeanFactory(beanFactory);
            }
        });
    }

    private void addConfigurationInstance(ConfigurableWebApplicationContext wac, final Configuration configuration, final ConfigurationLoader loader) {
        wac.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                beanFactory.registerSingleton("aksessConfiguration", configuration);
                beanFactory.registerSingleton("aksessConfigurationLoader", loader);

            }
        });
    }

    private ConfigurationLoader createConfigurationLoader(ServletContext context) {
        DefaultConfigurationLoader loader = new DefaultConfigurationLoader(new ServletContextResourceLoader(context));

        File dataDir = (File) context.getAttribute(APPLICATION_DIRECTORY);

        // First, load Aksess defaults
        loader.addResource("/WEB-INF/config/aksess-defaults.conf");

        loader.addResource("classpath:no/kantega/publishing/configuration/aksess-organization.conf");

        // Override with project specific settings
        loader.addResource("/WEB-INF/config/aksess-project.conf");

        // Override with environment specific setting
        loader.addResource("file:" + dataDir.getAbsolutePath() +"/conf/aksess.conf");

        return loader;
    }

    private class LegacyKantegaDirLocatorStrategy implements DataDirectoryLocatorStrategy {

        private static final String CONFIGFILE = "kantega.properties";
        private static final String DEFAULT_WIN_DIR = "C:\\Kantega";
        private static final String DEFAULT_UNIX_DIR = "/usr/local/kantega";


        public File locateDataDirectory() {

            File kantegaDir = new File(getKantegaDir());

            if(!kantegaDir.exists()) {
                throw new RuntimeException("Could not find kantega.dir " + kantegaDir);
            }

            String app = getApplication();
            

            if(app == null || app.length() == 0) {
                return kantegaDir;
            } else {

                File appDir = new File(kantegaDir, app);

                if(!appDir.exists()) {
                    throw new RuntimeException("Could not find Aksess application directory " + appDir);
                }

                return appDir;
            }



        }

        private String getKantegaDir() {
            String ps = File.separator;

            String kantegaDir = null;
            try {
                Properties p = new Properties();
                p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGFILE));
                kantegaDir = p.getProperty("kantega.dir");
            } catch (Exception e) {
                // Gjør ingenting, bruker default
            }

            if (kantegaDir == null || kantegaDir.length() == 0) {
                kantegaDir = System.getProperty("kantega.dir");
                if(kantegaDir == null) {
                    if (ps.equals("/")) {
                        kantegaDir = DEFAULT_UNIX_DIR;
                    } else {
                        kantegaDir = DEFAULT_WIN_DIR;
                    }
                }

            }
            if(!kantegaDir.endsWith(ps)) {
                kantegaDir += ps;
            }

            return kantegaDir;
        }


        public String getApplication() {
            String application = null;
            try {
                Properties p = new Properties();
                p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGFILE));
                application = p.getProperty("application.name");
                if (application == null) {
                    application = p.getProperty("application");
                }
            } catch (Exception e) {
                // Do nothing, file could not be found or read
            }

            return application;
        }
    }
}
