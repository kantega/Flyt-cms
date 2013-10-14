package no.kantega.publishing.plugin.provider;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.spring.RuntimeMode;
import org.apache.commons.io.IOUtils;
import org.kantega.jexmec.ClassLoaderProvider;
import org.kantega.jexmec.jarfiles.EmbeddedLibraryPluginClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Collections.singleton;

/**
 *
 */
public class PluginHotDeployProvider implements ClassLoaderProvider {
    private static final Logger log = LoggerFactory.getLogger(PluginHotDeployProvider.class);

    private ClassLoaderProvider.Registry registry;
    private File pluginWorkDirectory;
    private Map<String, DeployedPlugin> loaders = new HashMap<String, DeployedPlugin>();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ClassLoader parentClassLoader;
    private File installedPluginsDirectory;

    @Autowired
    private RuntimeMode runtimeMode;

    public void deploy(PluginInfo pluginInfo) throws IOException {
        DeployedPlugin deployedPlugin = loaders.get(pluginInfo.getKey());
        if (deployedPlugin != null) {
            // Undeploy existing version first
            undeployPlugin(deployedPlugin.getPluginInfo());
            // Remove old plugin file if installed, but not if same file
            if (isInstalled(deployedPlugin.getPluginInfo())
                    && isInstalled(pluginInfo)
                    && !deployedPlugin.getPluginInfo().getSource().equals(pluginInfo.getSource())) {
                deployedPlugin.getPluginInfo().getSource().delete();
            }
        }
        logger.info("Adding classloader for plugin " + pluginInfo.getKey() + " from source " + pluginInfo.getSource().getAbsolutePath());

        ClassLoader parentClassLoader = getParentClassLoader(pluginInfo);

        ClassLoader loader = createClassLoader(pluginInfo, parentClassLoader);

        registry.add(singleton(loader));

        loaders.put(pluginInfo.getKey(), new DeployedPlugin(loader, pluginInfo));
    }

    private ClassLoader createClassLoader(PluginInfo pluginInfo, ClassLoader parentClassLoader) {
        if(runtimeMode == RuntimeMode.PRODUCTION || pluginInfo.getCompileClasspath() == null) {
            ClassLoader loader = pluginInfo.getSource().isFile() ?
                    new EmbeddedLibraryPluginClassLoader(pluginInfo.getSource(), parentClassLoader, pluginWorkDirectory) :
                    new EmbeddedLibraryPluginClassLoader(pluginInfo.getSource(), parentClassLoader);
            if (pluginInfo.getResourceDirectory() != null && pluginInfo.getResourceDirectory().exists() && pluginInfo.getResourceDirectory().isDirectory()) {
                loader = new ResourceDirectoryPreferringClassLoader(loader, pluginInfo.getResourceDirectory());
            }
            return loader;
        } else {
            JavaCompilingPluginClassLoader loader = new JavaCompilingPluginClassLoader(pluginInfo, parentClassLoader);
            loader.compileJava();
            return loader;
        }

    }

    private ClassLoader getParentClassLoader(PluginInfo pluginInfo) {
        Set<ClassLoader> delegates = new HashSet<ClassLoader>();

        for (String dep : pluginInfo.getDependencies()) {
            if (loaders.containsKey(dep)) {
                delegates.add(loaders.get(dep).getClassLoader());
            }
        }
        if (delegates.isEmpty()) {
            return parentClassLoader;
        } else {
            return new ResourceHidingClassLoader(new DelegateClassLoader(parentClassLoader, delegates), OpenAksessPlugin.class);
        }
    }

    private boolean isInstalled(PluginInfo pluginInfo) {
        return pluginInfo.getSource().getParentFile().equals(installedPluginsDirectory);
    }

    public void undeployPlugin(PluginInfo pluginInfo) {
        logger.info("Undeploying plugin " + pluginInfo.getKey());
        DeployedPlugin deployedPlugin = loaders.get(pluginInfo.getKey());
        if(deployedPlugin == null) {
            log.info("Can't undeploy plugin with key " + pluginInfo.getKey() +". No such plugin is currently deployed.");
        } else {
            ClassLoader classLoader = deployedPlugin.getClassLoader();
            closeClassLoader(classLoader);
            registry.remove(singleton(classLoader));
            loaders.remove(pluginInfo.getKey());
        }
    }

    private void closeClassLoader(ClassLoader classLoader) {
        if(classLoader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();

            Set<JarFile> closables = new LinkedHashSet<JarFile>();

            for(URL url : urls) {
                if(url.getFile().endsWith(".jar")) {
                    try {
                        URL jarURL = new URL("jar:" + url.toExternalForm() + "!/");
                        JarURLConnection urlConnection = (JarURLConnection) jarURL.openConnection();
                        JarFile jarFile = urlConnection.getJarFile();
                        closables.add(jarFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            for(JarFile file : closables) {
                try {
                    file.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    public void start(Registry registry, ClassLoader parentClassLoader) {
        this.registry = registry;
        this.parentClassLoader = parentClassLoader;
        deployInstalledPlugins();
    }

    public SortedMap<String, PluginInfo> getDeployedPluginKeys() {
        TreeMap<String, PluginInfo> map = new TreeMap<String, PluginInfo>();
        for (Map.Entry<String, DeployedPlugin> entry : this.loaders.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getPluginInfo());
        }
        return map;
    }

    private void deployInstalledPlugins() {
        List<PluginInfo> plugins = sortByDependencies(findInstalledPlugins());

        logger.info("About to deploy " + plugins.size() + " plugins in the following order:");
        for (int i = 0; i < plugins.size(); i++) {
            PluginInfo pluginInfo = plugins.get(i);
            logger.info(i +": " + pluginInfo.getKey() + " '" + pluginInfo.getName() +"'");
        }

        for (PluginInfo info : plugins) {
            try {
                deploy(info);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<PluginInfo> sortByDependencies(Map<String, PluginInfo> plugins) {

        Map<String, Boolean> colors = new HashMap<String, Boolean>();
        List<PluginInfo> sorted = new LinkedList<PluginInfo>();

        for (PluginInfo info : plugins.values()) {
            if (!colors.containsKey(info.getKey()))
                dfs(info, plugins, colors, sorted);
        }
        return sorted;
    }

    private void dfs(PluginInfo info, Map<String, PluginInfo> plugins, Map<String, Boolean> colors, List<PluginInfo> sorted) {
        colors.put(info.getKey(), Boolean.FALSE);
        for (String dep : info.getDependencies()) {
            if (plugins.containsKey(dep) && !colors.containsKey(dep)) {
                dfs(plugins.get(dep), plugins, colors, sorted);
            }
        }
        colors.put(info.getKey(), Boolean.TRUE);
        sorted.add(info);
    }

    public void uninstallPlugin(String key) {
        DeployedPlugin deployedPlugin = loaders.get(key);
        if (deployedPlugin != null) {
            PluginInfo pluginInfo = deployedPlugin.getPluginInfo();
            logger.info("Removing plugin " + key);
            undeployPlugin(pluginInfo);
            logger.info("Uninstalling plugin " + key);
            if (pluginInfo.getSource().getParentFile().equals(installedPluginsDirectory)) {
                pluginInfo.getSource().delete();
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

    private Map<String, PluginInfo> findInstalledPlugins() {
        Map<String, PluginInfo> pluginFiles = new HashMap<String, PluginInfo>();

        if (installedPluginsDirectory.exists()) {
            File[] jars = installedPluginsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.getName().endsWith(".jar");
                }
            });

            if (jars != null) {
                for (File jar : jars) {
                    PluginInfo info = parsePluginInfo(jar);
                    pluginFiles.put(info.getKey(), info);
                }
            }
        }
        return pluginFiles;
    }

    public PluginInfo parsePluginInfo(File jar) {
        try {
            PluginInfo pluginInfo = null;

            String name = null;
            String description = null;
            Set<String> dependencies = new HashSet<String>();

            JarFile jarFile = new JarFile(jar);
            try {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String prefix = "META-INF/maven/";
                    String propSuffix = "/pom.properties";
                    String xmlSuffix = "/pom.xml";

                    if (entry.getName().startsWith(prefix) && entry.getName().endsWith(propSuffix)) {
                        Properties props = new Properties();
                        InputStream inputStream = jarFile.getInputStream(entry);
                        props.load(inputStream);
                        inputStream.close();
                        pluginInfo = new PluginInfo(jar, props.getProperty("groupId"),
                                props.getProperty("artifactId"),
                                props.getProperty("version"));
                        break;
                    } else if (entry.getName().startsWith(prefix) && entry.getName().endsWith(xmlSuffix)) {
                        XMLInputFactory xmlInput = XMLInputFactory.newFactory();
                        try {
                            InputStream inputStream = jarFile.getInputStream(entry);
                            XMLEventReader reader = xmlInput.createXMLEventReader(inputStream);

                            int level = 0;
                            boolean inDependencies = false;
                            boolean inDependency = false;

                            String groupId = null;
                            String artifactId = null;
                            while (reader.hasNext()) {
                                XMLEvent xmlEvent = reader.nextEvent();
                                if (xmlEvent.getEventType() == XMLEvent.START_ELEMENT) {
                                    level++;
                                    StartElement e = (StartElement) xmlEvent;

                                    String localPart = e.getName().getLocalPart();
                                    if (level == 2 && "name".equals(localPart)) {
                                        name = readTextContent(reader);
                                    } else if (level == 2 && "description".equals(localPart)) {
                                        description = readTextContent(reader);
                                    } else if (level == 2 && "dependencies".equals(localPart)) {
                                        inDependencies = true;
                                    } else if (level == 3 && inDependencies && "dependency".equals(localPart)) {
                                        inDependency = true;
                                    } else if (level == 4 && inDependency && "groupId".equals(localPart)) {
                                        groupId = readTextContent(reader);
                                    } else if (level == 4 && inDependency && "artifactId".equals(localPart)) {
                                        artifactId = readTextContent(reader);
                                    }
                                    if (pluginInfo != null && pluginInfo.getName() != null && pluginInfo.getDescription() != null) {
                                        break;
                                    }
                                }

                                if (xmlEvent.getEventType() == XMLEvent.END_ELEMENT) {

                                    EndElement e = (EndElement) xmlEvent;
                                    String localPart = e.getName().getLocalPart();
                                    if (level == 2 && "dependencies".equals(localPart)) {
                                        inDependencies = false;
                                    } else if (level == 3 && "dependency".equals(localPart)) {
                                        inDependency = false;
                                        dependencies.add(groupId + ":" + artifactId);
                                    }
                                    level--;
                                }

                            }
                            inputStream.close();
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (pluginInfo == null) {
                    throw new IllegalArgumentException("Plugin file " + jar.getAbsolutePath() + " did not contain expected file META-INF/maven/groupId/artifactId/pom.properties");
                }
                pluginInfo.setName(name);
                pluginInfo.setDescription(description);
                pluginInfo.setDependencies(dependencies);
            } finally {
                jarFile.close();
            }
            return pluginInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readTextContent(XMLEventReader reader) throws XMLStreamException {
        StringBuilder sb = new StringBuilder();

        while (reader.hasNext()) {
            XMLEvent next = reader.peek();
            if (next.getEventType() == XMLEvent.CHARACTERS) {
                next = reader.nextEvent();
                sb.append(((Characters) next).getData());
            } else if (next.getEventType() == XMLEvent.END_ELEMENT) {
                break;
            }

        }
        return sb.toString();
    }

    public void stop() {
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        for (DeployedPlugin plugin : loaders.values()) {
            classLoaders.add(plugin.getClassLoader());
        }
        registry.remove(classLoaders);

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
            if (local.exists()) {
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
            if (local.exists()) {
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

    private class DeployedPlugin {
        private PluginInfo pluginInfo;
        private ClassLoader classLoader;

        DeployedPlugin(ClassLoader classLoader, PluginInfo pluginInfo) {
            this.classLoader = classLoader;
            this.pluginInfo = pluginInfo;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }

        public PluginInfo getPluginInfo() {
            return pluginInfo;
        }
    }

    private class DelegateClassLoader extends ClassLoader {
        private final Set<ClassLoader> delegates;

        public DelegateClassLoader(ClassLoader parentClassLoader, Set<ClassLoader> delegates) {
            super(parentClassLoader);
            this.delegates = delegates;
        }


        @Override
        public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                return getParent().loadClass(name);
            } catch (ClassNotFoundException e) {

                for (ClassLoader delegate : delegates) {
                    try {
                        return delegate.loadClass(name);
                    } catch (ClassNotFoundException e1) {

                    }

                }
                throw new ClassNotFoundException(name);
            }
        }

        @Override
        protected URL findResource(String name) {
            URL resource = getParent().getResource(name);
            if (resource != null) {
                return resource;
            }
            for (ClassLoader delegate : delegates) {
                resource = delegate.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
            return null;
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            Enumeration<URL> parentResources = getParent().getResources(name);
            List<URL> delegateResources = null;
            for (ClassLoader delegate : delegates) {
                Enumeration<URL> resources = delegate.getResources(name);
                if(resources != null ) {
                    if(delegateResources == null) {
                        delegateResources = new LinkedList<URL>();
                    }
                    delegateResources.addAll(Collections.list(resources));
                }

            }
            if(delegateResources == null) {
                return parentResources;
            } else {
                LinkedList<URL> urls = new LinkedList<URL>();
                if(parentResources != null) {
                    urls.addAll(Collections.list(parentResources));
                }
                urls.addAll(delegateResources);
                return Collections.enumeration(urls);
            }
        }
    }

    class ResourceHidingClassLoader extends ClassLoader {

        private final String[] localResourcePrefixes;

        /**
         * Creates a ResourceHidingClassLoader hiding resources in <code>META-INF/services/PluginName/</code> and
         * <code>META-INF/services/com.example.PluginName/</code>.
         *
         * @param parent      the parent class loader
         * @param pluginClass the plugin class to hide resources for.
         */
        public ResourceHidingClassLoader(ClassLoader parent, Class pluginClass) {
            super(parent);
            localResourcePrefixes = new String[]{"META-INF/services/" + pluginClass.getSimpleName() + "/",
                    "META-INF/services/" + pluginClass.getName() + "/"};
        }


        @Override
        public InputStream getResourceAsStream(String name) {
            final URL resource = getResource(name);
            try {
                return resource == null ? null : resource.openStream();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public URL getResource(String name) {
            if (isLocalResource(name)) {
                return super.findResource(name);
            } else {
                return super.getResource(name);
            }
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            if (isLocalResource(name)) {
                return super.findResources(name);
            } else {
                return super.getResources(name);
            }
        }

        protected boolean isLocalResource(String name) {
            for (String localResourcePrefix : localResourcePrefixes) {
                if (name.startsWith(localResourcePrefix)) {
                    return true;
                }
            }
            return false;
        }
    }
}
