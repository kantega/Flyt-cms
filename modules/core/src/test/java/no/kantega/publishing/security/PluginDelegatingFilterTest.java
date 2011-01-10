package no.kantega.publishing.security;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 *
 */
public class PluginDelegatingFilterTest {

    private List<OpenAksessPlugin> plugins = new ArrayList<OpenAksessPlugin>();
    private PluginDelegatingFilter filter;
    private ServletRequest request;
    private ServletResponse response;
    private FilterChain filterChain;
    private OpenAksessPlugin blockPlugin;
    private OpenAksessPlugin nestingPlugin;

    @Before
    public void setup() {
        filter = new PluginDelegatingFilter() {
            @Override
            protected List<OpenAksessPlugin> getPlugins() {
                return plugins;
            }
        };
        request = mock(ServletRequest.class);
        response = mock(ServletResponse.class);
        filterChain = mock(FilterChain.class);
        blockPlugin = mock(OpenAksessPlugin.class);
        final Filter emptyFilter = mock(Filter.class);
        when(blockPlugin.getRequestFilters()).thenReturn(Collections.singletonList(emptyFilter));
        final Filter identityFilter = new Filter() {
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
            }

            public void destroy() {

            }
        };

        nestingPlugin = mock(OpenAksessPlugin.class);
        when(nestingPlugin.getRequestFilters()).thenReturn(Collections.singletonList(identityFilter));
    }
    @Test
    public void shouldPassThroughWithNoFilters() throws IOException, ServletException {
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void blockingFilterShouldBlock() throws IOException, ServletException {

        plugins.add(blockPlugin);
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(0)).doFilter(request, response);
    }


    @Test
    public void nestingFiltersShouldWork() throws IOException, ServletException {

        plugins.add(nestingPlugin);
        plugins.add(nestingPlugin);
        plugins.add(nestingPlugin);
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void blockingInsideNestingFilterShouldBlock() throws IOException, ServletException {

        plugins.add(nestingPlugin);
        plugins.add(blockPlugin);
        plugins.add(nestingPlugin);
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(0)).doFilter(request, response);
    }
}
