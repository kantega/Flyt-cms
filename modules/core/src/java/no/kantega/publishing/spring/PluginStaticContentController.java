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

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import org.kantega.jexmec.PluginManager;
import org.kantega.jexmec.ClassLoaderAwarePluginManager;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.commons.media.MimeTypes;
import no.kantega.commons.media.MimeType;

import java.net.URL;

/**
 */
public class PluginStaticContentController extends AbstractController {
    private PluginManager<OpenAksessPlugin> pluginManager;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String pathInfo = request.getPathInfo();
        String path = pathInfo.substring("/static/".length());

        String resourcePath = "no/kantega/openaksess/static/" + path;

        ClassLoaderAwarePluginManager<OpenAksessPlugin> pluginManager = ClassLoaderAwarePluginManager.class.cast(this.pluginManager);
        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            ClassLoader classLoader = pluginManager.getClassLoader(plugin);

            if(classLoader != null) {
                URL resource = classLoader.getResource(resourcePath);
                if(resource != null) {
                    MimeType mimeType = MimeTypes.getMimeType(pathInfo.substring(pathInfo.lastIndexOf("/")));
                    response.setContentType(mimeType.getType());
                    IOUtils.copy(resource.openStream(), response.getOutputStream());
                    return null;
                }
            }
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }
}
