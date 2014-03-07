package no.kantega.publishing.web.servlet;

import no.kantega.commons.filter.*;
import no.kantega.publishing.client.filter.ContentRewriteFilter;
import no.kantega.publishing.plugin.provider.StalePluginClassLoaderFilter;
import no.kantega.publishing.security.PluginDelegatingFilter;
import no.kantega.publishing.security.filter.AdminFilter;
import no.kantega.publishing.security.filter.RoleFilter;
import no.kantega.publishing.spring.DataDirectoryContextListener;
import no.kantega.publishing.spring.DatabaseDriversContextListener;
import no.kantega.publishing.spring.LogInitListener;
import no.kantega.publishing.spring.OpenAksessContextLoaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * ServletContainerInitializer that adds OpenAksess' Filters such that they are run before the Filters
 * declared in the project.
 * onStartup is run before ContextLoaderListener.contextInitialized is called, so the filters that rely on
 * the applicationContext are registered with class such that the container instantiates the instance later.
 */
public class OpenAksessServletContainerInitializer implements ServletContainerInitializer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        log.info("Registering OpenAksess filters");

        ctx.addListener(DatabaseDriversContextListener.class);
        ctx.addListener(DataDirectoryContextListener.class);
        ctx.addListener(LogInitListener.class);
        ctx.addListener(OpenAksessContextLoaderListener.class);
        ctx.addListener(RequestContextListener.class);

        ctx.addFilter("ResponseHeaderFilter", getAdminResponseHeaderFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/admin/*");

        ctx.addFilter("ParamEncodingFilter", new ParamEncodingFilter("utf-8"))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        ctx.addFilter("AksessRequestFilter", new AksessRequestFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        ctx.addFilter("StalePluginClassLoaderFilter", StalePluginClassLoaderFilter.class)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        ctx.addFilter("NoSpringContextCapableMultipartFilter", new NoSpringContextCapableMultipartFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        ctx.addFilter("AdminFilter", new AdminFilter(ctx))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/admin/*");

        ctx.addFilter("AdminRoleFilter", new RoleFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/admin/tools/*");

        ctx.addFilter("ContentRewriteFilter", new ContentRewriteFilter(ctx))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        ctx.addFilter("PluginDelegatingFilter", PluginDelegatingFilter.class)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        ctx.addFilter("FakeDirectoryExpires", new FarFutureExpiresDirectoryFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/expires/*");

    }

    private ResponseHeaderFilter getAdminResponseHeaderFilter() {
        return new ResponseHeaderFilter(new HashSet<>(asList("jpg", "png", "gif")), Collections.<String, String>emptyMap(), 365);
    }
}
