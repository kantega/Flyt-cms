package no.kantega.publishing.plugin.provider;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.kantega.jexmec.PluginManager;
import org.kantega.jexmec.PluginManagerListener;
import org.kantega.jexmec.events.PluginLoadingExceptionEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class ThreadLocalPluginLoaderErrors {
    private ThreadLocal<Throwable> pluginLoadingException = new ThreadLocal<Throwable>();

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        pluginManager.addPluginManagerListener(new PluginManagerListener<OpenAksessPlugin>() {
            @Override
            public void pluginLoadingFailedWithException(PluginLoadingExceptionEvent<OpenAksessPlugin> openAksessPluginPluginLoadingExceptionEvent) {
                pluginLoadingException.set(openAksessPluginPluginLoadingExceptionEvent.getThrowable());
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
