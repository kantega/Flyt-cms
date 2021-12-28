package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.kantega.jexmec.PluginManager;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class PluginDelegatingHandlerMapping implements HandlerMapping, Ordered {


    private PluginManager<OpenAksessPlugin> pluginManager;

    public static final String DELEGATED_PLUGIN_ATTR = PluginDelegatingHandlerMapping.class.getName() +"_DELEGATED_PLUGIN_ATTR";
    private int order;


    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        for (final OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            for (HandlerMapping handlerMapping : plugin.getHandlerMappings()) {
                final HandlerExecutionChain executionChain = handlerMapping.getHandler(request);

                if (executionChain != null) {
                    executionChain.addInterceptor(new HandlerInterceptorAdapter() {
                        @Override
                        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                            request.setAttribute(DELEGATED_PLUGIN_ATTR, plugin);
                            return super.preHandle(request, response, handler);
                        }

                        @Override
                        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                            request.removeAttribute(DELEGATED_PLUGIN_ATTR);
                        }
                    });
                    return executionChain;
                }
            }
        }
        return null;
    }

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
