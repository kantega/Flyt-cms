package no.kantega.publishing.controls.plugin;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.spring.PluginHotDepoyProvider;
import no.kantega.publishing.spring.RuntimeMode;
import org.apache.log4j.Logger;
import org.kantega.jexmec.PluginManager;
import org.kantega.jexmec.PluginManagerListener;
import org.kantega.jexmec.events.PluginLoadingExceptionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@Controller
public class PluginDeploymentController {

    @Autowired
    private RuntimeMode runtimeMode;

    @Autowired
    private PluginHotDepoyProvider provider;


    private ThreadLocal<Throwable> pluginLoadingException = new ThreadLocal<Throwable>();
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        pluginManager.addPluginManagerListener(new PluginManagerListener<OpenAksessPlugin>() {
            @Override
            public void pluginLoadingFailedWithException(PluginLoadingExceptionEvent<OpenAksessPlugin> openAksessPluginPluginLoadingExceptionEvent) {
                pluginLoadingException.set(openAksessPluginPluginLoadingExceptionEvent.getThrowable());
            }
        });
    }

    @RequestMapping(method = RequestMethod.POST)
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(runtimeMode != RuntimeMode.DEVELOPMENT) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        response.setContentType("text/plain");
        String id = request.getParameter("id");
        String file = request.getParameter("file");
        String resourceDirectory = request.getParameter("resourceDirectory");

        File pluginFile = new File(file);
        pluginLoadingException.remove();
        try {
            provider.deploy(id, pluginFile, resourceDirectory == null ? null : new File(resourceDirectory));
            Throwable throwable = pluginLoadingException.get();
            if(throwable != null) {
                log.error("Error occured loading plugins from " + file, throwable);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Exception loading plugin(s) from file " + file +": " + throwable.getMessage());
                throwable.printStackTrace(response.getWriter());
            } else {
                response.getWriter().write("Plugin loaded: " + id);
            }
        } finally {
            pluginLoadingException.remove();
        }


    }
}
