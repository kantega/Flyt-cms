package no.kantega.publishing.web.servlet;

import no.kantega.commons.filter.*;
import no.kantega.publishing.client.filter.ContentRewriteFilter;
import no.kantega.publishing.client.filter.OpenAksessConfiguredFilter;
import no.kantega.publishing.plugin.provider.StalePluginClassLoaderFilter;
import no.kantega.publishing.security.PluginDelegatingFilter;
import no.kantega.publishing.security.filter.AdminFilter;
import no.kantega.publishing.security.filter.RoleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ServletContainerInitializer that adds OpenAksess' Filters such that they are run before the Filters
 * declared in the project.
 */
public class OpenAksessServletContainerInitializer implements ServletContainerInitializer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        log.info("Registering OpenAksess filters");

        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST);

        FilterRegistration.Dynamic responseHeaderFilter = ctx.addFilter("ResponseHeaderFilter", ResponseHeaderFilter.class);
        responseHeaderFilter.setInitParameters(getAdminResponseHeaderFilterParams());
        responseHeaderFilter.addMappingForUrlPatterns(dispatcherTypes, false, "/admin/*");

        FilterRegistration.Dynamic paramEncodingFilter = ctx.addFilter("ParamEncodingFilter", ParamEncodingFilter.class);
        paramEncodingFilter.setInitParameter("encoding", "utf-8");
        paramEncodingFilter
                .addMappingForUrlPatterns(dispatcherTypes, false, "/*");

        ctx.addFilter("StalePluginClassLoaderFilter", StalePluginClassLoaderFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/*");

        ctx.addFilter("NoSpringContextCapableMultipartFilter", NoSpringContextCapableMultipartFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/*");

        ctx.addFilter("AdminFilter", AdminFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/admin/*");

        ctx.addFilter("AdminRoleFilter", RoleFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/admin/tools/*");

        ctx.addFilter("ContentRewriteFilter", ContentRewriteFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/*");

        ctx.addFilter("AksessRequestFilter", AksessRequestFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/*");

        ctx.addFilter("PluginDelegatingFilter", PluginDelegatingFilter.class)
                .addMappingForUrlPatterns(dispatcherTypes, false, "/*");

        FilterRegistration.Dynamic aksesswro = ctx.addFilter("aksesswro", OpenAksessConfiguredFilter.class);
        aksesswro.setInitParameter("wrappedFilterClass", "ro.isdc.wro.http.WroFilter");
        aksesswro.addMappingForServletNames(dispatcherTypes, false, "/wro-oa/*");

        ctx.addFilter("FakeDirectoryExpires", FarFutureExpiresDirectoryFilter.class)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE), false, "/expires/*");
    }

    private Map<String, String> getAdminResponseHeaderFilterParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("extensions", "jpg,png,gif");
        params.put("expiresInDays", "365");

        return params;
    }
}
