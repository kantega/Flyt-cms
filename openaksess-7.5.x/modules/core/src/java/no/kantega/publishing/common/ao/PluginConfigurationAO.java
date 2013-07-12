package no.kantega.publishing.common.ao;

/**
 *
 */
public interface PluginConfigurationAO {

    String getProperty(String pluginUid, String name);
    void setProperty(String pluginUid, String name, String value);
}
