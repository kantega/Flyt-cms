package no.kantega.publishing.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Collections;

/**
 * Date: Jul 6, 2009
 * Time: 2:16:37 PM
 *
 * @author Tarje Killingberg
 */
public class AdditionalBeansExposingPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private List<String> exposedBeanNames = Collections.emptyList();


    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        for (String beanName : exposedBeanNames) {
            configurableListableBeanFactory.registerSingleton(beanName, applicationContext.getBean(beanName));
        }
        configurableListableBeanFactory.registerSingleton("rootApplicationContext", applicationContext);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setExposedBeanNames(List<String> exposedBeanNames) {
        this.exposedBeanNames = exposedBeanNames;
    }

}
