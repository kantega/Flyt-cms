package no.kantega.publishing.plugin.provider;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.spring.RuntimeMode;
import org.kantega.jexmec.ClassLoaderProvider;
import org.kantega.jexmec.PluginManager;
import org.kantega.jexmec.PluginManagerListener;
import org.springframework.context.ApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class StalePluginClassLoaderFilter implements Filter {
    private RuntimeMode runtimeMode;

    private Map<String, JavaCompilingPluginClassLoader> classLoaders = new HashMap<>();
    private PluginHotDeployProvider provider;
    private ThreadLocalPluginLoaderErrors pluginLoadingErrors;

    public void init(FilterConfig filterConfig) throws ServletException {
        doInit();
    }

    private void doInit() {
        ApplicationContext context = RootContext.getInstance();
        runtimeMode = context.getBean(RuntimeMode.class);
        pluginLoadingErrors = context.getBean(ThreadLocalPluginLoaderErrors.class);

        provider = context.getBean(PluginHotDeployProvider.class);
        PluginManager pluginManager = context.getBean(PluginManager.class);

        pluginManager.addPluginManagerListener(new PluginManagerListener<OpenAksessPlugin>() {

            @Override
            public void beforeClassLoaderAdded(PluginManager<OpenAksessPlugin> pluginManager, ClassLoaderProvider classLoaderProvider, ClassLoader classLoader) {
                if (classLoader instanceof JavaCompilingPluginClassLoader) {
                    synchronized (this) {
                        JavaCompilingPluginClassLoader loader = (JavaCompilingPluginClassLoader) classLoader;
                        classLoaders.put(loader.getPluginInfo().getKey(), loader);
                    }
                }
            }

            @Override
            public void afterClassLoaderRemoved(PluginManager<OpenAksessPlugin> pluginManager, ClassLoaderProvider classLoaderProvider, ClassLoader classLoader) {
                if (classLoader instanceof JavaCompilingPluginClassLoader) {
                    synchronized (this) {
                        JavaCompilingPluginClassLoader loader = (JavaCompilingPluginClassLoader) classLoader;
                        classLoaders.remove(loader.getPluginInfo().getKey());
                    }
                }
            }

        });
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if(!"/PluginDeployment.action".equals(req.getServletPath()) && runtimeMode == RuntimeMode.DEVELOPMENT) {
            Set<JavaCompilingPluginClassLoader> copy = new HashSet<JavaCompilingPluginClassLoader>();

            synchronized (this) {
                copy.addAll(classLoaders.values());
            }

            for (JavaCompilingPluginClassLoader loader : copy) {
                if (loader.isStale()) {
                    try {
                        loader.compileJava();

                        provider.undeployPlugin(loader.getPluginInfo());

                        try{
                            pluginLoadingErrors.remove();
                            provider.deploy(loader.getPluginInfo());
                        } finally {
                            Throwable t = pluginLoadingErrors.get();
                            if(t != null) {
                                request.setAttribute("exception", t);
                                request.getRequestDispatcher("/WEB-INF/jsp/plugins/plugin-loading-exception.jsp")
                                        .forward(request, response);
                                return;
                            }
                        }

                    } catch (JavaCompilationException e) {
                        request.setAttribute("diagnostics", e.getDiagnostics());

                        request.getRequestDispatcher("/WEB-INF/jsp/plugins/java-compilation-error.jsp")
                                .forward(request, response);
                        return;
                    }

                }
            }
        }

        chain.doFilter(request, response);

    }

    public void destroy() {

    }
}
