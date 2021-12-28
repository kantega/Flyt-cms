package no.kantega.publishing.spring;

import no.kantega.publishing.common.Aksess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * Imports a Spring context if a configuration property equals a certain value.
 */
public class ConditionalResourceImporter implements BeanFactoryPostProcessor, ResourceLoaderAware {

    private static final Logger log = LoggerFactory.getLogger(ConditionalResourceImporter.class);

    private ResourceLoader resourceLoader;
    private String propertyName;
    private String propertyValue;
    private String resourceLocation;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if(propertyName == null) {
            throw new InvalidPropertyException(ConditionalResourceImporter.class, "propertyName", "propertyName cannot be null");
        }

        if(resourceLocation == null) {
            throw new InvalidPropertyException(ConditionalResourceImporter.class, "resourceLocation", "resourceLocation cannot be null");
        }

        String actualPropertyValue = Aksess.getConfiguration().getString(propertyName);

        if ((actualPropertyValue == null && propertyValue == null) || propertyValue != null && propertyValue.equals(actualPropertyValue)) {
            log.info("Loading resources from " + resourceLocation);

            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);

            reader.loadBeanDefinitions(resourceLoader.getResource(resourceLocation));
        } else {
            log.info("Skipped loading resources from " + resourceLocation);
        }

    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
