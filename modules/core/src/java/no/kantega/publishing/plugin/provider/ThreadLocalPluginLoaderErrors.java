package no.kantega.publishing.plugin.provider;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.kantega.jexmec.ClassLoaderProvider;
import org.kantega.jexmec.PluginLoader;
import org.kantega.jexmec.PluginManager;
import org.kantega.jexmec.PluginManagerListener;

/**
 *
 */
public class ThreadLocalPluginLoaderErrors {
    private ThreadLocal<Throwable> pluginLoadingException = new ThreadLocal<Throwable>();

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        pluginManager.addPluginManagerListener(new PluginManagerListener<OpenAksessPlugin>() {

            @Override
            public void pluginLoadingFailedWithException(PluginManager<OpenAksessPlugin> pluginManager, ClassLoaderProvider classLoaderProvider, ClassLoader classLoader, PluginLoader<OpenAksessPlugin> pluginLoader, Throwable exception) {
                pluginLoadingException.set(exception);
            }

        });
    }

    public void remove() {
        pluginLoadingException.remove();
    }

    public Throwable get() {
        return pluginLoadingException.get();
    }

}
