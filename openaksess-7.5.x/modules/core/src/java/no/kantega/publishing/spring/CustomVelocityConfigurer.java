package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.kantega.jexmec.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import java.io.File;

/**
 *
 */
public class CustomVelocityConfigurer extends VelocityConfigurer{

    private static final String PLUGIN_MACRO_LIBRARY = "no/kantega/publishing/api/view/velocity/plugin.vm";
    private static final String PLUGINLOADER = "pluginloader";

    @Autowired
    private PluginManager<OpenAksessPlugin> pluginManager;

    @Override
    protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {

        final String resourceBases = System.getProperty("resourceBases");

        // For easy development reload of velocity templates
        if(resourceBases != null) {
            for(String base : resourceBases.split(File.pathSeparator)) {
                final String loaderName = "base_" + base.hashCode();
                velocityEngine.addProperty(VelocityEngine.RESOURCE_LOADER, loaderName);
                velocityEngine.setProperty(loaderName +".resource.loader.class", FileResourceLoader.class.getName());
                velocityEngine.setProperty(loaderName +".resource.loader.path", base);
                velocityEngine.setProperty(loaderName +".resource.loader.cache", "false");
                

            }
        }

        velocityEngine.addProperty(VelocityEngine.RESOURCE_LOADER, PLUGINLOADER);
        velocityEngine.setProperty(PLUGINLOADER +".resource.loader.class", VelocityPluginResourceLoader.class.getName());
        velocityEngine.setProperty(PLUGINLOADER+".resource.loader.cache", "false");


        velocityEngine.addProperty(
                        VelocityEngine.VM_LIBRARY, PLUGIN_MACRO_LIBRARY);

        velocityEngine.setApplicationAttribute(VelocityPluginResourceLoader.PLUGIN_MANAGER, pluginManager);

        super.postProcessVelocityEngine(velocityEngine);

    }
}
