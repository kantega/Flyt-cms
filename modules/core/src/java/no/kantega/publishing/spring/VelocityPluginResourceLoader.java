package no.kantega.publishing.spring;

import no.kantega.commons.filter.AksessRequestFilter;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.kantega.jexmec.PluginManager;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 *
 */
public class VelocityPluginResourceLoader extends ResourceLoader {
    public static final String PLUGIN_MANAGER = "pluginManager";
    private PluginManager<OpenAksessPlugin> pluginManager;

    public void init(ExtendedProperties configuration) {
        pluginManager = (PluginManager<OpenAksessPlugin>) this.rsvc.getApplicationAttribute(PLUGIN_MANAGER);
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {

        OpenAksessPlugin plugin = getPlugin();
        if(plugin != null) {
            final ClassLoader loader = pluginManager.getClassLoader(plugin);
            final InputStream stream = loader.getResourceAsStream(source);
            if(stream == null) {
                throw new ResourceNotFoundException("Resource '" + source +"' was not found in class loader of plugin " + plugin.getPluginUid());
            }
            return stream;
        }
        throw new ResourceNotFoundException ("No resource found");
    }

    private OpenAksessPlugin getPlugin() {
        HttpServletRequest request = AksessRequestFilter.getRequest();

        return request == null ? null : (OpenAksessPlugin) request.getAttribute(PluginDelegatingHandlerMapping.DELEGATED_PLUGIN_ATTR);
    }


    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0;
    }
}
