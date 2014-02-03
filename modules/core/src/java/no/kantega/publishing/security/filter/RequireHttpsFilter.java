package no.kantega.publishing.security.filter;

import no.kantega.publishing.common.Aksess;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.kantega.commons.util.URLHelper.getUrlWithHttps;

public class RequireHttpsFilter implements Filter {
    private boolean enabled;
    private Pattern excludedFilesPattern = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        enabled = Aksess.getConfiguration().getBoolean("requirehttps.enabled", false);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (enabled) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if(!request.isSecure()){
                response.sendRedirect(getUrlWithHttps(request));
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean notExcluded(HttpServletRequest request) {
        String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        Matcher m = excludedFilesPattern.matcher(path);
        boolean isExcluded = m.matches();
        return !isExcluded;
    }

    @Override
    public void destroy() {

    }
}
