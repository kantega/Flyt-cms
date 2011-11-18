package no.kantega.publishing.spring;

import org.apache.log4j.Logger;
import org.kantega.jexmec.PluginClassLoaderProvider;
import org.kantega.jexmec.jarfiles.EmbeddedLibraryPluginClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singleton;

/**
 *
 */
public class PluginHotDepoyProvider implements PluginClassLoaderProvider {

    private Registry registry;
    private File pluginWorkDirectory;
    private Map<String, ClassLoader> loaders = new HashMap<String, ClassLoader>();
    private Logger logger = Logger.getLogger(getClass());
    private ClassLoader parentClassLoader;

    public void deploy(File jarFile, File resourceDirectory) throws IOException {
        if(loaders.containsKey(jarFile.getAbsolutePath())) {
            logger.info("Removing already present classloader for plugin " + jarFile.getAbsolutePath());
            registry.remove(singleton(loaders.get(jarFile.getAbsolutePath())));
        }
        logger.info("Adding classloader for plugin " + jarFile.getAbsolutePath());

        URLConnection connection = getClass().getResource(getClass().getSimpleName() + ".class").openConnection();
        connection.setDefaultUseCaches(false);


        ClassLoader loader = new EmbeddedLibraryPluginClassLoader(jarFile, parentClassLoader, pluginWorkDirectory);
        if (resourceDirectory != null && resourceDirectory.exists() && resourceDirectory.isDirectory()) {
            loader = new ResourceDirectoryPreferringClassLoader(loader, resourceDirectory);
        }
        registry.add(singleton(loader));
        loaders.put(jarFile.getAbsolutePath(), loader);
    }
    public void start(Registry registry, ClassLoader parentClassLoader) {
        this.registry = registry;
        this.parentClassLoader = parentClassLoader;
    }

    public void stop() {
        registry.remove(loaders.values());
        loaders.clear();
    }
    public void setPluginWorkDirectory(File pluginWorkDirectory) {
        this.pluginWorkDirectory = pluginWorkDirectory;
    }

    private class ResourceDirectoryPreferringClassLoader extends ClassLoader {
        private final File resourceDirectory;

        public ResourceDirectoryPreferringClassLoader(ClassLoader parentClassLoader, File resourceDirectory) {
            super(parentClassLoader);
            this.resourceDirectory = resourceDirectory;
        }

        @Override
        public URL getResource(String path) {
            File local = new File(resourceDirectory, path);
            if(local.exists()) {
                try {
                    return local.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.getResource(path);
        }

        @Override
        public InputStream getResourceAsStream(String path) {
            File local = new File(resourceDirectory, path);
            if(local.exists()) {
                try {
                    return local.toURI().toURL().openStream();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.getResourceAsStream(path);
        }

    }
}
