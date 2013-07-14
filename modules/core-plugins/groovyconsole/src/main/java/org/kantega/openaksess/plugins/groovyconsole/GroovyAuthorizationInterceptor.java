package org.kantega.openaksess.plugins.groovyconsole;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class GroovyAuthorizationInterceptor extends HandlerInterceptorAdapter {
    public final static String AUTHORIZED_KEY = GroovyAuthorizationInterceptor.class.getName() + "_AUTHORIZED";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isAuthenticationUrl = request.getRequestURL().toString().contains("/admin/groovyauth.action");
        if(!isAuthenticationUrl && !Boolean.TRUE.equals(request.getSession().getAttribute(AUTHORIZED_KEY))) {
            response.sendRedirect("groovyauth.action");
            return false;
        }
        return true;
    }
}
