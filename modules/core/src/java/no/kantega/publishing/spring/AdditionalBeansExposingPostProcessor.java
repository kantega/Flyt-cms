package no.kantega.publishing.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collections;
import java.util.List;

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
            register(configurableListableBeanFactory, beanName, applicationContext.getBean(beanName));
        }
        register(configurableListableBeanFactory, "rootApplicationContext", applicationContext);
    }

    private void register(ConfigurableListableBeanFactory configurableListableBeanFactory, String name, Object bean) {
        //

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) configurableListableBeanFactory;

        RootBeanDefinition definition = new RootBeanDefinition(bean.getClass());

        registry.registerBeanDefinition(name, definition);
        configurableListableBeanFactory.registerSingleton(name, bean);

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setExposedBeanNames(List<String> exposedBeanNames) {
        this.exposedBeanNames = exposedBeanNames;
    }

}
