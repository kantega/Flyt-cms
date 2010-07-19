package no.kantega.publishing.spring;

import org.springframework.web.servlet.view.velocity.VelocityViewResolver;
import org.springframework.web.servlet.View;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.util.Locale;

/**
 */
public class ChainableVelocityViewResolver extends VelocityViewResolver {
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        try {
            if(getClass().getClassLoader().getResource(getPrefix() + viewName + getSuffix()) != null) {
                return super.resolveViewName(viewName, locale);
            } else {
                return null;
            }
            
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
}
