package no.kantega.publishing.controls.plugin;

import no.kantega.publishing.spring.PluginHotDepoyProvider;
import no.kantega.publishing.spring.RuntimeMode;
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

    @RequestMapping(method = RequestMethod.POST)
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(runtimeMode != RuntimeMode.DEVELOPMENT) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        response.setContentType("text/plain");
        String file = request.getParameter("file");
        String resourceDirectory = request.getParameter("resourceDirectory");

        File pluginFile = new File(file);
        provider.deploy(pluginFile, resourceDirectory == null ? null : new File(resourceDirectory));

        response.getWriter().write("Plugin loaded: " + pluginFile.getName());
    }
}
