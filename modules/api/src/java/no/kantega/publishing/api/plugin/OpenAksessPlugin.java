package no.kantega.publishing.api.plugin;

import org.kantega.jexmec.Plugin;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;

import no.kantega.publishing.api.content.ContentRequestListener;


/**
 */
public interface OpenAksessPlugin extends Plugin {


    public List<HandlerMapping> getHandlerMappings();

    List<ContentRequestListener> getContentRequestListeners();
}
