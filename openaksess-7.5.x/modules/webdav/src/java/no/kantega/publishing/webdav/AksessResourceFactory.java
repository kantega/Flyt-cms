package no.kantega.publishing.webdav;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.webdav.resourcehandlers.AksessWebDavResourceHandler;

import java.util.Collection;

/**
 *
 */
public class AksessResourceFactory implements ResourceFactory {
    private final static String ROOT = "/webdav";

    private Collection<AksessWebDavResourceHandler> resourceHandlers = null;

    public Resource getResource(String host, String path) {
        try {
            Configuration config = Aksess.getConfiguration();
            if (config.getBoolean("webdav.enabled", false)) {
                if (!path.contains(ROOT)) {
                    return null;
                }
                if (path.contains(".")) {
                    // Ignore files starting with .
                    String[] elm = path.split("/");
                    if (elm != null && elm.length > 0) {
                        if (elm[elm.length - 1].startsWith(".")) {
                            return null;
                        }
                    }
                }
                Log.debug(this.getClass().getName(), "Get resource: " + path);

                path = path.substring(path.indexOf(ROOT) +  + ROOT.length(), path.length());


                if (resourceHandlers == null) {
                    resourceHandlers = RootContext.getInstance().getBeansOfType(AksessWebDavResourceHandler.class).values();
                }

                for (AksessWebDavResourceHandler resourceHandler : resourceHandlers) {
                    if (resourceHandler.canHandlePath(path)) {
                        return resourceHandler.getResourceFromPath(path);
                    }
                }
            }

        } catch (ConfigurationException e) {
            Log.error(this.getClass().getName(), e, null, null);
        }

        return null;
    }

}
