package no.kantega.publishing.security;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.kantega.jexmec.PluginManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PluginDelegatingFilter implements Filter {
    PluginManager<OpenAksessPlugin> pluginManager;

    public PluginDelegatingFilter() {

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        final WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
         pluginManager = (PluginManager<OpenAksessPlugin>) context.getBean("pluginManager");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain wrappedChain) throws IOException, ServletException {
        buildFilterChain(wrappedChain).doFilter(request, response);
    }

    private FilterChain buildFilterChain(FilterChain wrappedChain) {
        List<Filter> filters = new ArrayList<Filter>();
        for(OpenAksessPlugin plugin : getPlugins()) {
            for(Filter filter : plugin.getRequestFilters()) {
                filters.add(filter);
            }
        }
        return new PluginFilterChain(filters, wrappedChain);
    }

    protected List<OpenAksessPlugin> getPlugins() {
        return pluginManager.getPlugins();
    }

    public void destroy() {

    }

    private static class PluginFilterChain implements FilterChain {
        private final List<Filter> filters;
        private FilterChain filterChain;
        private int filterIndex;

        public PluginFilterChain(List<Filter> filters, FilterChain filterChain) {
            this.filters = filters;
            this.filterChain = filterChain;
        }
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if(filterIndex == filters.size()) {
                filterChain.doFilter(request, response);
            } else {
                filters.get(filterIndex++).doFilter(request, response, this);
            }
        }
    }
}
