package no.kantega.publishing.plugin.config;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.plugin.config.PluginConfig;
import no.kantega.publishing.api.plugin.config.PluginConfigProvider;

import java.io.*;
import java.util.Properties;

/**
 *
 */
public class DefaultPluginConfigProvider implements PluginConfigProvider {
    private final File rootDirectory;

    public DefaultPluginConfigProvider(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public PluginConfig get(OpenAksessPlugin plugin) {
        if(plugin.getPluginUid() == null) {
            throw new IllegalArgumentException("Plugin " + plugin + " has null pluginUid" );
        }
        return new PropertyFileConfig(new File(rootDirectory, plugin.getPluginUid() +".properties"));
    }

    class PropertyFileConfig implements PluginConfig {
        private final File propertyFile;

        public PropertyFileConfig(File propertyFile) {
            this.propertyFile = propertyFile;
        }


        public String get(String name) {
            return get(name, null);
        }

        public String get(String name, String defaultValue) {
            final String value = read().getProperty(name);
            return value == null ? defaultValue : value;
        }

        public PluginConfig set(String name, String value) {
            final Properties properties = read();
            properties.setProperty(name, value);
            save(properties);
            return this;
        }

        private Properties read() {
            try {
                final Properties props = new Properties();
                if(propertyFile.exists()) {
                    final FileInputStream fis = new FileInputStream(propertyFile);
                    props.load(fis);
                    fis.close();
                }
                return props;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        private void save(Properties properties) {
            synchronized (DefaultPluginConfigProvider.this) {
                try {
                    if(!propertyFile.getParentFile().exists()) {
                        propertyFile.getParentFile().mkdirs();
                    }
                    properties.store(new FileOutputStream(propertyFile), null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
