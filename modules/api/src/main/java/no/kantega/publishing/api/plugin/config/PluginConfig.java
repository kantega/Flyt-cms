package no.kantega.publishing.api.plugin.config;

/**
 *
 */
public interface PluginConfig {
    String get(String name);
    String get(String name, String defaultValue);
    PluginConfig set(String name, String value);
}
