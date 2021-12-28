package no.kantega.publishing.spring;

import no.kantega.commons.filter.AksessRequestFilter;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.kantega.jexmec.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import java.util.Locale;

/**
 */
public class ChainableVelocityViewResolver extends VelocityViewResolver {

    @Autowired
    private PluginManager<OpenAksessPlugin> pluginManager;

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        try {
            String path = getPrefix() + viewName + getSuffix();
            // Strip leading "/". Works on Jetty, but not on Tomcat.
            while(path.startsWith("/")) {
                path = path.substring(1);
            }

            if(viewName.startsWith(REDIRECT_URL_PREFIX) || getClassLoader().getResource(path) != null) {
                return super.resolveViewName(viewName, locale);
            } else {
                return null;
            }
            
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private ClassLoader getClassLoader() {
        //Workaround for NullPointer AksessRequestFilter.getRequest()

        if(AksessRequestFilter.getRequest() != null){
            OpenAksessPlugin plugin = (OpenAksessPlugin) AksessRequestFilter.getRequest().getAttribute(PluginDelegatingHandlerMapping.DELEGATED_PLUGIN_ATTR);

            if(plugin != null) {
                ClassLoader pluginClassLoader = pluginManager.getClassLoader(plugin);
                if(pluginClassLoader != null) {
                    return pluginClassLoader;
                }
            }
        }
        return getClass().getClassLoader();
    }
}
