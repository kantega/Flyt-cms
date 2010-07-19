package no.kantega.publishing.spring;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.kantega.jexmec.PluginManager;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;

import javax.servlet.http.HttpServletRequest;

/**
 */
public class PluginDelegatingHandlerMapping implements HandlerMapping {


    private PluginManager<OpenAksessPlugin> pluginManager;

    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        for (OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            for (HandlerMapping handlerMapping : plugin.getHandlerMappings()) {
                HandlerExecutionChain executionChain = handlerMapping.getHandler(request);
                if (executionChain != null) {
                    return executionChain;
                }
            }
        }
        return null;
    }

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }

}
