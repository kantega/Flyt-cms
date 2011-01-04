package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.web.servlet.support.PluginRequestContext;
import no.kantega.publishing.web.servlet.support.DefaultPluginRequestContext;
import org.apache.velocity.context.Context;
import org.springframework.web.servlet.view.velocity.VelocityView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 */
public class CustomVelocityView extends VelocityView {



    @Override
    protected void exposeHelpers(Context context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        context.put("contextPath", request.getContextPath());

    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exposePluginRequestContext(model, request, response);
        super.renderMergedTemplateModel(model, request, response);
    }

    /**
     * Expose a plugin requestcontext using the plugin's WebApplicationContext if this is a plugin delegated request
     */
    private void exposePluginRequestContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {

        OpenAksessPlugin plugin = (OpenAksessPlugin) request.getAttribute(PluginDelegatingHandlerMapping.DELEGATED_PLUGIN_ATTR);

        if(plugin!= null) {
            model.put(PluginRequestContext.PLUGIN_REQUEST_CONTEXT_ATTRIBUTE, new DefaultPluginRequestContext(plugin, request, response, getServletContext(), model));
        }
    }
}
