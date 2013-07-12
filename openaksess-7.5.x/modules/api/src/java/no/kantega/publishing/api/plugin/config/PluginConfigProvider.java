package no.kantega.publishing.api.plugin.config;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;

/**
 *
 */
public interface PluginConfigProvider {
    PluginConfig get(OpenAksessPlugin plugin);
}
