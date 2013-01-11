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
import no.kantega.commons.configuration.ConfigurationLoader;
import no.kantega.commons.configuration.DefaultConfigurationLoader;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.ServletContextResourceLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Custom ContextLoaderListener for Aksess.
 * Locates the application directory and exposes it as a ${appDir} property in the Spring Application Context.
 */
public class OpenAksessContextLoaderListener extends ContextLoaderListener {

    public static final String APPLICATION_DIRECTORY = OpenAksessContextLoaderListener.class.getName() +"_APPLICATION_DIRECTORY";
    public static final String LISTENER_ATTR = OpenAksessContextLoader.class.getName() +".this";
    private ServletContext servletContext;
    private Properties properties;
    private File dataDirectory;

    private ConfigurationLoader configurationLoader;
    private Logger log = Logger.getLogger(getClass());
    private ServletContextEvent event;

    @Override
    public void contextInitialized(final ServletContextEvent event) {

        this.event = event;

        this.servletContext = event.getServletContext();

        log.info("Starting OpenAksess " + getOpenAksessVersion());

        dataDirectory = getDataDirectory(event.getServletContext());

        log.info("Using data directory " + dataDirectory.getAbsolutePath());

        configurationLoader = createConfigurationLoader(servletContext, dataDirectory);

        log.info("Loading configuration");

        properties = configurationLoader.loadConfiguration();

        servletContext.setAttribute(LISTENER_ATTR, this);

        initContext();

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            super.contextDestroyed(event);
        } finally {
            dbConnectionFactory.closePool();
        }
    }

    public synchronized void initContext() {
        checkThatRequiredPropertiesPresentAndValid(this.properties);
        super.contextInitialized(event);
    }

    private void checkThatRequiredPropertiesPresentAndValid(Properties properties) {
        checkDatabaseConfigured(properties);
        if(!properties.containsKey("location.contextpath")) {
            throw new IllegalStateException("Required configuration property 'location.contextpath' not found.");
        }
    }

    private void checkDatabaseConfigured(Properties properties) {
        log.debug("Determining if database is configured");
        List<String> missingProperties = new ArrayList<String>();

        String driverClass = properties.getProperty("database.driver");

        if(StringUtils.isEmpty(driverClass)) {
            missingProperties.add("database.driver");
        }
        String url = properties.getProperty("database.url");

        if(StringUtils.isEmpty(url)) {
            missingProperties.add("database.url");
        }

        boolean useNTML = Boolean.parseBoolean(properties.getProperty("database.useNTMLauthentication", "false"));
        String username = properties.getProperty("database.username");
        if(StringUtils.isEmpty(username) && !useNTML) {
            missingProperties.add("database.username");
        }
        String password = properties.getProperty("database.password");
        if(StringUtils.isEmpty(password) && !useNTML) {
            missingProperties.add("database.password");
        }

        // Some properties are missing
        if(missingProperties.size() > 0) {
            throw new IllegalStateException("OpenAksess could not be started. The following database configuration properties are missing: " + missingProperties);
        }
    }

    private File getDataDirectory(ServletContext context) {
        File dataDirectory = (File) context.getAttribute(DataDirectoryContextListener.DATA_DIRECTORY_ATTR);

        if(dataDirectory == null) {
            throw new NullPointerException("dataDirectory attribute " + DataDirectoryContextListener.DATA_DIRECTORY_ATTR
                    +" was not set");
        }
        return dataDirectory;
    }


    @Override
    protected ContextLoader createContextLoader() {
        return new OpenAksessContextLoader();

    }

    public String getOpenAksessVersion() {
        URL versionResource = getClass().getClassLoader().getResource("no/kantega/publishing/common/aksessVersion.properties");
        if(versionResource == null) {
            return "<unknown>";
        }

        Properties versionProps = new Properties();

        try {
            versionProps.load(versionResource.openStream());
        } catch (IOException e) {
            return "<unknown>";
        }
        String theVersion = versionProps.getProperty("version");

        return theVersion == null ? "<unknown>" : theVersion;

    }


    class OpenAksessContextLoader extends ContextLoader {

        @Override
        protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext wac) {


            Configuration.setApplicationDirectory(dataDirectory);

            // Make dataDir available as Servlet Context attribute
            servletContext.setAttribute(APPLICATION_DIRECTORY, dataDirectory);

            // Set up @Autowired support
            ApplicationContextUtils.addAutowiredSupport(wac);

            // Add ${appDir} property for the Spring Context
            ApplicationContextUtils.addAppDirPropertySupport(wac);

            final Configuration configuration = new Configuration(properties);

            Configuration.setDefaultConfiguration(configuration);

            // Set and load configuration on these classes since they are not DI-based (hackish..)
            Aksess.setConfiguration(configuration);
            Aksess.loadConfiguration();

            dbConnectionFactory.setServletContext(servletContext);
            dbConnectionFactory.setConfiguration(configuration);
            dbConnectionFactory.loadConfiguration();

            // Add the Configuration and the ConfigurationLoader as Spring beans
            addConfigurationAndLoaderAsSingletonsInContext(wac, configuration, configurationLoader);

            // Replace ${} properties in Spring with config properties
            addConfigurationPropertyReplacer(wac, properties);

            RootContext.setInstance(wac);

        }

    }



    private void addConfigurationPropertyReplacer(ConfigurableWebApplicationContext wac, final Properties properties) {
        wac.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
                cfg.setProperties(properties);
                cfg.postProcessBeanFactory(beanFactory);
            }
        });
    }

    private void addConfigurationAndLoaderAsSingletonsInContext(ConfigurableWebApplicationContext wac, final Configuration configuration, final ConfigurationLoader loader) {
        wac.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                beanFactory.registerSingleton("aksessConfiguration", configuration);
                beanFactory.registerSingleton("aksessConfigurationLoader", loader);

            }
        });
    }

    private ConfigurationLoader createConfigurationLoader(ServletContext context, File dataDirectory) {
        DefaultConfigurationLoader loader = new DefaultConfigurationLoader(new ServletContextResourceLoader(context));


        // First, load Aksess defaults
        loader.addResource("/WEB-INF/config/aksess-defaults.conf");

        loader.addResource("classpath:no/kantega/publishing/configuration/aksess-organization.conf");

        // Override with project specific settings
        loader.addResource("/WEB-INF/config/aksess-project.conf");

        // Override with environment specific setting
        loader.addResource("file:" + dataDirectory.getAbsolutePath() +"/conf/aksess.conf");

        return loader;
    }

    public Properties getProperties() {
        return properties;
    }
}