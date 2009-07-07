package no.kantega.publishing.api.plugin;

import org.kantega.jexmec.Plugin;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

/**
 * Date: Jul 6, 2009
 * Time: 1:17:12 PM
 *
 * @author Tarje Killingberg
 */
public interface OpenAksessPlugin extends Plugin {


    public List<HandlerMapping> getHandlerMappings();

}
