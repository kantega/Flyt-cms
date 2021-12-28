package no.kantega.publishing.plugin.provider;

import org.apache.commons.io.IOUtils;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 *
 */
public class JavaCompilingPluginClassLoader extends URLClassLoader {
    private PluginInfo pluginInfo;

    private final static JavaCompiler compiler;
    private final static StandardJavaFileManager fileManager;

    static {
        compiler = ToolProvider.getSystemJavaCompiler();

        fileManager = compiler.getStandardFileManager(null, null, null);
    }
    private final long pluginXmlLoadTime;
    private final File pluginXmlFile;


    public JavaCompilingPluginClassLoader(PluginInfo pluginInfo, ClassLoader parentClassLoader) {
        super(getUrls(pluginInfo), parentClassLoader);
        this.pluginInfo = pluginInfo;
        pluginXmlFile = new File(pluginInfo.getResourceDirectory(), "META-INF/services/OpenAksessPlugin/spring.xml");
        pluginXmlLoadTime = pluginXmlFile.lastModified();
    }

    private static URL[] getUrls(PluginInfo pluginInfo) {
        try {
            List<URL> urls = new ArrayList<URL>();
            urls.add(pluginInfo.getResourceDirectory().toURI().toURL());
            urls.add(pluginInfo.getSource().toURI().toURL());
            String runtimeClasspath = pluginInfo.getRuntimeClasspath();
            if(runtimeClasspath != null) {
                for(String path : runtimeClasspath.split(":")) {
                    urls.add(new URL("file:" +path));
                }
            }
            return urls.toArray(new URL[urls.size()]);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Enumeration<URL> getResources(String path) throws IOException {
        Enumeration<URL> resources = super.getResources(path);
        if("META-INF/services/OpenAksessPlugin/spring.xml".equals(path)) {
            List<URL> filtered = new ArrayList<URL>();
            while(resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if(!url.getFile().startsWith(pluginInfo.getSource().getAbsolutePath())) {
                    filtered.add(url);
                }
            }
            return Collections.enumeration(filtered);
        } else {
            return resources;
        }
    }

    public boolean isStale() {
        if(pluginXmlFile.lastModified() > pluginXmlLoadTime) {
            return true;
        }

        File src = new File(pluginInfo.getSource().getParentFile().getParentFile(), "src/main/java");

        List<File> sourceFiles = new ArrayList<File>();

        long newestClass = newest(pluginInfo.getSource(), 0);

        addCompilationUnits(src, sourceFiles, newestClass);

        return ! sourceFiles.isEmpty();
    }


    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    public void compileJava() {

        File src = new File(pluginInfo.getSource().getParentFile().getParentFile(), "src/main/java");


        long newestClass = newest(pluginInfo.getSource(), 0);

        List<File> sourceFiles = new ArrayList<File>();
        addCompilationUnits(src, sourceFiles, newestClass);

        if (!sourceFiles.isEmpty()) {

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

            String cp = pluginInfo.getCompileClasspath();
            cp += File.pathSeparator +pluginInfo.getSource().getAbsolutePath();

            pluginInfo.getSource().mkdirs();
            List<String> options = new ArrayList<String>(Arrays.asList("-g", "-classpath", cp, "-d", pluginInfo.getSource().getAbsolutePath()));

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, fileManager.getJavaFileObjectsFromFiles(sourceFiles));

            boolean success = task.call();

            if (!success) {
                throw new JavaCompilationException(diagnostics.getDiagnostics());
            }
        }
    }

    private long newest(File src, long max) {
        if(src.isFile()) {
            return Math.max(max, src.lastModified());
        }
        File[] children = src.listFiles();

        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    max = Math.max(max, newest(child, max));
                } else {
                    max = newest(child, max);
                }

            }
        }

        return max;
    }

    private void addCompilationUnits(File src, List<File> compilationUnits, long newestClass) {
        if (src.isDirectory()) {
            File[] children = src.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        addCompilationUnits(child, compilationUnits, newestClass);
                    } else if (child.getName().endsWith(".java") && child.lastModified() > newestClass) {
                        compilationUnits.add(child);
                    }
                }
            }
        }
    }

    class JavaSourceFromURL extends SimpleJavaFileObject {
        private final URL code;
        private final String name;

        JavaSourceFromURL(String name, URL code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
            this.name = name;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return IOUtils.toString(code.openStream(), "utf-8");
        }

        @Override
        public boolean isNameCompatible(String s, Kind kind) {
            return super.isNameCompatible(s, kind);
        }

        @Override
        public URI toUri() {
            try {
                return code.toURI();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
