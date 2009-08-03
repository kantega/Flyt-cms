package no.kantega.publishing.api.plugin;

import org.kantega.jexmec.AbstractPlugin;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Collections;

import no.kantega.publishing.api.content.ContentRequestListener;

/**
 */
public class OpenAksessPluginAdapter extends AbstractPlugin implements OpenAksessPlugin {

    private List<HandlerMapping> handlerMappings = Collections.emptyList();

    private List<ContentRequestListener> contentRequestListeners = Collections.emptyList();

    public OpenAksessPluginAdapter(String pluginId) {
        super(pluginId);
    }

    public void setHandlerMappings(List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    public List<HandlerMapping> getHandlerMappings() {
        return handlerMappings;
    }

    public List<ContentRequestListener> getContentRequestListeners() {
        return contentRequestListeners;
    }

    public void setContentRequestListeners(List<ContentRequestListener> contentRequestListeners) {
        this.contentRequestListeners = contentRequestListeners;
    }
}
