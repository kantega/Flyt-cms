package no.kantega.publishing.plugin.config;

import no.kantega.commons.configuration.ConfigurationLoader;
import no.kantega.commons.configuration.DefaultConfigurationLoader;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.plugin.config.PluginConfig;
import no.kantega.publishing.api.plugin.config.PluginConfigProvider;
import no.kantega.publishing.common.ao.PluginConfigurationAO;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class DefaultPluginConfigProvider implements PluginConfigProvider, ResourceLoaderAware {
    private final File environmentDirectory;
    private final PluginConfigurationAO pluginConfigurationAO;

    private Map<String, PluginConfig> configurations = new HashMap<String, PluginConfig>();

    private ResourceLoader resourceLoader;

    public DefaultPluginConfigProvider(File environmentDirectory, PluginConfigurationAO pluginConfigurationAO) {
        this.environmentDirectory = environmentDirectory;
        this.pluginConfigurationAO = pluginConfigurationAO;
    }

    public synchronized PluginConfig get(OpenAksessPlugin plugin) {
        if(plugin.getPluginUid() == null) {
            throw new IllegalArgumentException("Plugin " + plugin + " has null pluginUid" );
        }
        PluginConfig config = configurations.get(plugin.getPluginUid());
        if(config == null) {
            config = new NestedPropertiesConfig(plugin.getPluginUid());
            configurations.put(plugin.getPluginUid(), config);
        }
        return config;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    class NestedPropertiesConfig implements PluginConfig {
        private final ConfigurationLoader configurationLoader;
        private final String pluginUid;


        public NestedPropertiesConfig(String pluginUid) {
            this.pluginUid = pluginUid;
            DefaultConfigurationLoader configurationLoader = new DefaultConfigurationLoader(resourceLoader);
            this.configurationLoader = configurationLoader;
            // First, add the plugin's default properties
            configurationLoader.addResource("classpath:META-INF/services/" + OpenAksessPlugin.class.getName() +"/" + pluginUid + ".conf");
            // Then add the project config
            configurationLoader.addResource("/WEB-INF/config/plugins/" +pluginUid + ".conf");
            // Then add environment specific config
            configurationLoader.addResource("file:" + new File(environmentDirectory, pluginUid +".conf").getAbsoluteFile());
        }


        public String get(String name) {
            return get(name, null);
        }

        public String get(String name, String defaultValue) {
            // Try database first
            String value = pluginConfigurationAO.getProperty(pluginUid, name);
            if(value == null) {
                value = read().getProperty(name);
            }
            return value == null ? defaultValue : value;
        }

        public PluginConfig set(String name, String value) {
            pluginConfigurationAO.setProperty(pluginUid, name, value);
            return this;
        }

        private Properties read() {
            return configurationLoader.loadConfiguration();
        }

    }


}
