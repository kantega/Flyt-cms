package no.kantega.publishing.controls.plugin;

import no.kantega.publishing.plugin.provider.PluginHotDeployProvider;
import no.kantega.publishing.plugin.provider.PluginInfo;
import no.kantega.publishing.plugin.provider.ThreadLocalPluginLoaderErrors;
import no.kantega.publishing.spring.RuntimeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Controller
public class PluginDeploymentController {

    @Autowired
    private RuntimeMode runtimeMode;

    @Autowired
    private PluginHotDeployProvider provider;


    private static final Logger log = LoggerFactory.getLogger(PluginDeploymentController.class);

    @Autowired
    private ThreadLocalPluginLoaderErrors threadLocalPluginLoaderErrors;

    @RequestMapping(method = RequestMethod.POST, value = "/PluginDeployment.action")
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(runtimeMode != RuntimeMode.DEVELOPMENT) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        response.setContentType("text/plain");

        String file = request.getParameter("file");

        File source = new File(file);


        if(! source.exists()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Source " + file + " does not exists");
            return;

        }

        PluginInfo pluginInfo;

        if(source.isDirectory()) {
            String groupId = request.getParameter("groupId");
            String artifactId = request.getParameter("artifactId");
            String version = request.getParameter("version");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String dependencies = request.getParameter("dependencies");
            String runtimeClasspath = request.getParameter("runtimeClasspath");
            String compileClasspath = request.getParameter("compileClasspath");


            Set<String> missingParams = new HashSet<String>();
            if(groupId == null) {
                missingParams.add("groupId");
            }
            if(artifactId == null) {
                missingParams.add("artifactId");
            }
            if(version == null) {
                missingParams.add("version");
            }

            if(!missingParams.isEmpty()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Parameters required when deploying from directory: " + missingParams);
                return;
            }
            pluginInfo = new PluginInfo(source, groupId, artifactId, version);
            pluginInfo.setName(name);
            pluginInfo.setDescription(description);
            pluginInfo.setRuntimeClasspath(runtimeClasspath);
            pluginInfo.setCompileClasspath(compileClasspath);
            if(dependencies != null) {
                pluginInfo.setDependencies(new HashSet<String>(Arrays.asList(dependencies.split(","))));
            }
        } else {
            pluginInfo = provider.parsePluginInfo(source);
        }
        String resourceDirectoryPath = request.getParameter("resourceDirectory");
        File resourceDirectory = new File(resourceDirectoryPath);
        if(resourceDirectory.exists() && resourceDirectory.isDirectory()) {
            pluginInfo.setResourceDirectory(resourceDirectory);
        }

        threadLocalPluginLoaderErrors.remove();
        try {
            provider.deploy(pluginInfo);
            Throwable throwable = threadLocalPluginLoaderErrors.get();
            if(throwable != null) {
                log.error("Error occured loading plugins from " + file, throwable);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Exception loading plugin(s) from file " + file +": " + throwable.getMessage());
                throwable.printStackTrace(response.getWriter());
            } else {
                response.getWriter().write("Plugin loaded: " + pluginInfo.getKey());
            }
        } finally {
            threadLocalPluginLoaderErrors.remove();
        }


    }
}
