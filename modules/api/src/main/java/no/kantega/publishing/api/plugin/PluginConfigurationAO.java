package no.kantega.publishing.api.plugin;

/**
 *
 */
public interface PluginConfigurationAO {

    String getProperty(String pluginUid, String name);
    void setProperty(String pluginUid, String name, String value);
}
