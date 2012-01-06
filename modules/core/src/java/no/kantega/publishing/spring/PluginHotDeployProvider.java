package no.kantega.publishing.spring;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kantega.jexmec.PluginClassLoaderProvider;
import org.kantega.jexmec.PluginManagerListener;
import org.kantega.jexmec.events.PluginLoadingExceptionEvent;
import org.kantega.jexmec.jarfiles.EmbeddedLibraryPluginClassLoader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Collections.singleton;

/**
 *
 */
public class PluginHotDeployProvider implements PluginClassLoaderProvider {

    private Registry registry;
    private File pluginWorkDirectory;
    private Map<String, ClassLoader> loaders = new HashMap<String, ClassLoader>();
    private Logger logger = Logger.getLogger(getClass());
    private ClassLoader parentClassLoader;
    private File installedPluginsDirectory;

    public void deploy(String id, File fileOrDirectory) throws IOException {
        deploy(id, fileOrDirectory, null);
    }
    public void deploy(String id, File fileOrDirectory, File resourceDirectory) throws IOException {
        if(loaders.containsKey(id)) {
            logger.info("Removing already present classloader for plugin " + id);
            registry.remove(singleton(loaders.get(id)));
        }
        logger.info("Adding classloader for plugin " + id +" from source " + fileOrDirectory.getAbsolutePath());

        if(fileOrDirectory.isFile()) {
            URLConnection connection = getClass().getResource(getClass().getSimpleName() + ".class").openConnection();
            connection.setDefaultUseCaches(false);
        }


        ClassLoader loader = fileOrDirectory.isFile() ?
                new EmbeddedLibraryPluginClassLoader(fileOrDirectory, parentClassLoader, pluginWorkDirectory) :
                new EmbeddedLibraryPluginClassLoader(fileOrDirectory, parentClassLoader);
        if (resourceDirectory != null && resourceDirectory.exists() && resourceDirectory.isDirectory()) {
            loader = new ResourceDirectoryPreferringClassLoader(loader, resourceDirectory);
        }

        registry.add(singleton(loader));
        loaders.put(id, loader);
    }
    public void start(Registry registry, ClassLoader parentClassLoader) {
        this.registry = registry;
        this.parentClassLoader = parentClassLoader;
        deployInstalledPlugins();
    }

    public SortedSet<String> getDeployedPluginKeys() {
        return new TreeSet<String>(loaders.keySet());
    }
    private void deployInstalledPlugins() {
        Map<String, File> pluginFiles = findPluginFiles();

        for(String id : pluginFiles.keySet()) {
            File file = pluginFiles.get(id);
            try {
                deploy(id, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public File installPlugin(String filename, InputStream inputStream) {
        installedPluginsDirectory.mkdirs();
        try {
            File file = new File(installedPluginsDirectory, filename);
            FileOutputStream output = new FileOutputStream(file);
            IOUtils.copy(inputStream, output);
            output.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Map<String, File> findPluginFiles() {
        Map<String, File> pluginFiles = new HashMap<String, File>();

        if(installedPluginsDirectory.exists()) {
            File[] jars = installedPluginsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.getName().endsWith(".jar");
                }
            });

            if(jars != null) {
                for(File jar : jars) {
                    String id = findMavenGroupIdAndArtifactId(jar);
                    pluginFiles.put(id, jar);
                }
            }
        }
        return pluginFiles;
    }

    public String findMavenGroupIdAndArtifactId(File jar) {
        try {
            String groupIdArtifactId = null;
            JarFile jarFile = new JarFile(jar);
            try {
                Enumeration<JarEntry> entries = jarFile.entries();
                while(entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String prefix = "META-INF/maven/";
                    String suffix = "/pom.properties";
                    if(entry.getName().startsWith(prefix) && entry.getName().endsWith(suffix)) {
                        String ga = entry.getName().substring(prefix.length(), entry.getName().length()-suffix.length());
                        groupIdArtifactId = ga.replace('/',':');
                        break;
                    }
                }
            } finally {
                jarFile.close();
            }
            if(groupIdArtifactId == null) {
                throw new IllegalArgumentException("Plugin jar file did not contain expected file META-INF/groupId/artifactId/pom.properties: " + jar.getAbsolutePath());
            } else {
                return groupIdArtifactId;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        registry.remove(loaders.values());
        loaders.clear();
    }
    public void setPluginWorkDirectory(File pluginWorkDirectory) {
        this.pluginWorkDirectory = pluginWorkDirectory;
    }

    public void setInstalledPluginsDirectory(File installedPluginsDirectory) {
        this.installedPluginsDirectory = installedPluginsDirectory;
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
