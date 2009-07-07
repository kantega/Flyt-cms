package no.kantega.publishing.api.plugin;

import org.kantega.jexmec.AbstractPlugin;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Collections;

/**
 * Date: Jul 6, 2009
 * Time: 1:18:27 PM
 *
 * @author Tarje Killingberg
 */
public class OpenAksessPluginAdapter extends AbstractPlugin implements OpenAksessPlugin {

    private List<HandlerMapping> handlerMappings = Collections.emptyList();


    public OpenAksessPluginAdapter(String pluginId) {
        super(pluginId);
    }

    public void setHandlerMappings(List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    public List<HandlerMapping> getHandlerMappings() {
        return handlerMappings;
    }

}
