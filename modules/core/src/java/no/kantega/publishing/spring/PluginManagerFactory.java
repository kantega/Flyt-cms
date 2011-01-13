package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.kantega.jexmec.*;
import org.kantega.jexmec.simple.SimpleClassLoaderStrategy;
import org.kantega.jexmec.spring.SpringPluginLoader;
import org.kantega.jexmec.spring.SpringServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;


/**
 */
public class PluginManagerFactory extends AbstractFactoryBean implements ApplicationContextAware, ServletContextAware, ApplicationListener {

    private ApplicationContext applicationContext;
    private File pluginsDirectory;
    private File pluginsWorkDirectory;
    private ServletContext servletContext;
    private ServiceLocator serviceLocator;
    private List<PluginLoader<OpenAksessPlugin>> pluginLoaders;
    private List<BeanFactoryPostProcessor> postProcessors;
    private Class<? extends Services> servicesClass;
    private Class<? extends Plugin> pluginClass;

    public Class getObjectType() {
        return PluginManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object createInstance() throws Exception {
        ServiceLocator serviceLocator = new SpringServiceLocator(applicationContext, servicesClass);
        SpringPluginLoader<OpenAksessPlugin> spring = new SpringPluginLoader<OpenAksessPlugin>() {

            @Override
            public List<OpenAksessPlugin> loadPlugins(Class<OpenAksessPlugin> pluginClass, ClassLoader classLoader, ServiceLocator serviceLocator) {
                checkWrongSpringXmlLocation(classLoader);
                return super.loadPlugins(pluginClass, classLoader, serviceLocator);
            }

            private void checkWrongSpringXmlLocation(ClassLoader classLoader) {
                String contextLocation = "META-INF/services/" +pluginClass.getName() +"/spring.xml";
                String contextLocationWithSlashInsteadOfDot = "META-INF/services/" +pluginClass.getName().replace('.','/') +"/spring.xml";

                try {
                    final Enumeration<URL> resources = classLoader.getResources(contextLocationWithSlashInsteadOfDot);

                    if(resources.hasMoreElements()) {
                        throw new IllegalArgumentException("We found a spring.xml file located at " + resources.nextElement() +". " +
                                "OpenAksess expects to find the spring.xml files in 'META-INF/services/" + contextLocation +"'" +
                                ", NOT '" + contextLocationWithSlashInsteadOfDot +"'. Please put spring.xml at the currect location.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected ConfigurableApplicationContext
            createApplicationContext(URL url, ClassLoader loader) {
                final String resourceBases = System.getProperty("resourceBases");
                final XmlWebApplicationContext context = resourceBases == null ? new XmlWebApplicationContext() : new DevXmlWebApplicationContext(resourceBases);
                context.setClassLoader(loader);
                context.setConfigLocation(url.toExternalForm());
                context.setServletContext(servletContext);

                return context;
            }

        };
        if (postProcessors != null) {
            spring.setBeanFactoryPostProcessors(postProcessors);
        }

        pluginClass = OpenAksessPlugin.class;
        final DefaultPluginManager manager = new DefaultPluginManager(pluginClass,
                serviceLocator);
        manager.addClassLoaderStrategy(new SimpleClassLoaderStrategy(applicationContext.getClassLoader()));
        manager.addPluginLoader(spring);
        return manager;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    public void setPostProcessors(List<BeanFactoryPostProcessor> postProcessors) {
        this.postProcessors = postProcessors;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent && event.getSource() == applicationContext) {
            try {
                DefaultPluginManager manager = (DefaultPluginManager) getObject();
                manager.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setServicesClass(Class<? extends Services> servicesClass) {
        this.servicesClass = servicesClass;
    }

    public void setPluginClass(Class<? extends Plugin> pluginClass) {
        this.pluginClass = pluginClass;
    }

    /**
     * Check resources on resourceBases first
     */
    private class DevXmlWebApplicationContext extends XmlWebApplicationContext {
        private final String[] resourceBases;

        public DevXmlWebApplicationContext(String resourceBases) {
            this.resourceBases = resourceBases.split(File.pathSeparator);
        }

        @Override
        public Resource getResource(String location) {
            if (location.startsWith(CLASSPATH_URL_PREFIX)) {
                for(String base : resourceBases) {
                    final FileSystemResource resource = new FileSystemResource(new File(base, location.substring(CLASSPATH_URL_PREFIX.length())));
                    if(resource.exists()) {
                        return resource;
                    }
                }
            }
            return super.getResource(location);
        }
    }
}
