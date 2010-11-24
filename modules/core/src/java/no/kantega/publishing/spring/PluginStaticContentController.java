/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.commons.io.IOUtils;
import org.kantega.jexmec.ClassLoaderAwarePluginManager;
import org.kantega.jexmec.PluginManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class PluginStaticContentController extends AbstractController {
    private PluginManager<OpenAksessPlugin> pluginManager;
    private String prefix;
    private final File[] baseDirectories;

    public PluginStaticContentController() {
        final String resourceBases = System.getProperty("resourceBases");

        // For easy development reload of plugin resources

        List<File> baseDirectories = new ArrayList<File>();

        if(resourceBases != null) {
            for(String base : resourceBases.split(File.pathSeparator)) {
                File baseFile = new File(base);
                if(baseFile.exists() && baseFile.isDirectory()) {
                    baseDirectories.add(baseFile);
                }
            }
        }

        this.baseDirectories = baseDirectories.toArray(new File[baseDirectories.size()]);

    }

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String pathInfo = request.getPathInfo();
        String path = pathInfo.substring("/static/".length());


        String resourcePath = prefix + path;

        URL resource = getResourceFromDevelopmentPaths(resourcePath);
        if(resource == null) {
            resource = getResourceFromPlugins(resourcePath);
        }
        if(resource != null) {
            String mimeType = getServletContext().getMimeType(pathInfo.substring(pathInfo.lastIndexOf("/") +1));
            response.setContentType(mimeType);
            IOUtils.copy(resource.openStream(), response.getOutputStream());
            return null;
        } else {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    private URL getResourceFromDevelopmentPaths(String resourcePath) {
        for(File base : baseDirectories) {
            final File file = new File(base, resourcePath);
            if(file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private URL getResourceFromPlugins(String resourcePath) {
        ClassLoaderAwarePluginManager<OpenAksessPlugin> pluginManager = ClassLoaderAwarePluginManager.class.cast(this.pluginManager);
        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            ClassLoader classLoader = pluginManager.getClassLoader(plugin);

            if(classLoader != null) {
                URL resource = classLoader.getResource(resourcePath);
                if(resource != null) {
                    return resource;
                }
            }
        }
        return null;
    }

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
