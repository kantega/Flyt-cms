package no.kantega.publishing.spring;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import java.util.Locale;

/**
 */
public class ChainableVelocityViewResolver extends VelocityViewResolver {
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        try {
            String path = getPrefix() + viewName + getSuffix();
            // Strip leading "/". Works on Jetty, but not on Tomcat.
            while(path.startsWith("/")) {
                path = path.substring(1);
            }
            if(viewName.startsWith(REDIRECT_URL_PREFIX) || getClass().getClassLoader().getResource(path) != null) {
                return super.resolveViewName(viewName, locale);
            } else {
                return null;
            }
            
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
}
