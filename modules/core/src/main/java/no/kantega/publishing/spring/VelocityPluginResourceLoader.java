package no.kantega.publishing.spring;

import no.kantega.commons.filter.AksessRequestFilter;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;
import org.kantega.jexmec.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 *
 */
public class VelocityPluginResourceLoader extends ResourceLoader {
    private static final Logger log = LoggerFactory.getLogger(VelocityPluginResourceLoader.class);
    public static final String PLUGIN_MANAGER = "pluginManager";
    private PluginManager<OpenAksessPlugin> pluginManager;
    @Override
    public void init(ExtProperties extProperties) {
        pluginManager = (PluginManager<OpenAksessPlugin>) this.rsvc.getApplicationAttribute(PLUGIN_MANAGER);
    }

    @Override
    public Reader getResourceReader(String s, String s1) throws ResourceNotFoundException {
        try {
            return  new BufferedReader(new InputStreamReader(getResourceStream(s), s1));
        } catch (UnsupportedEncodingException e) {
            log.error("unsupported encoding {}", s1);
            return null;
        }
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
