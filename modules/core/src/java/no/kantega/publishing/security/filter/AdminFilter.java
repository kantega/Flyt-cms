/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.security.filter;

import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.client.filter.CrossSiteRequestForgeryContentRewriter;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.spring.RootContext;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Map;

/**
 *
 */
public class AdminFilter implements Filter {
    private ServletContext servletContext;
    private Logger  log = Logger.getLogger(getClass());

    private static String[] excludedStaticResources = {".png", ".jpg", ".gif", ".jjs", ".js", ".css"};

    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        request.setAttribute(ServletContext.class.getName(), servletContext);

        request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());

        try {
            ContentManagementService aksessService = new ContentManagementService(request);
            SecuritySession securitySession = aksessService.getSecuritySession();

            if (!isStaticResource(request)) {
                // Sjekk at bruker er logget inn
                if (!securitySession.isLoggedIn()) {
                    if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                        // Requested with ajax
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    } else {
                        securitySession.initiateLogin(request, response);
                        return;
                    }
                }

                // Sjekk at bruker er autorisert
                if (!securitySession.isUserInRole(Aksess.getAdminRole()) && !securitySession.isUserInRole(Aksess.getAuthorRoles())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                request.getSession(true).setAttribute("adminMode", "true");

                // Check for cross site request forgery
                if(isForgedPost(request)) {
                    log.info("Possible CSRF detected: by " + securitySession.getIdentity().getUserId() +"@" + securitySession.getIdentity().getDomain() +" from " +request.getRemoteHost() +", posting to " + request.getRequestURL().toString() );
                    if (Aksess.isCsrfCheckEnabled()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF detected");
                        return;
                    } else {
                        log.info("... but CSRF-checking is disabled for this site");
                    }
                }
                response.setDateHeader("Expires", 0);
            }


            filterChain.doFilter(request,  response);
        } catch (Exception e) {
            ExceptionHandler handler = new ExceptionHandler();

            Throwable cause = (Throwable) e;
            if (cause instanceof javax.servlet.jsp.JspException) {
                cause = ((javax.servlet.jsp.JspException) cause).getRootCause();
                if (cause == null) {
                    cause = (Throwable) e;
                }
            }
            if (cause instanceof javax.servlet.ServletException) {
                cause = ((javax.servlet.ServletException) cause).getRootCause();
                if (cause == null) {
                    cause = (Throwable) e;
                }
            }
            if (cause instanceof org.springframework.web.util.NestedServletException) {
                cause = ((org.springframework.web.util.NestedServletException) cause).getRootCause();
                if (cause == null) {
                    cause = (Throwable) e;
                }
            }
            if (cause instanceof java.lang.reflect.InvocationTargetException) {
                cause = ((java.lang.reflect.InvocationTargetException) cause).getTargetException();
                if (cause == null) {
                    cause = (Throwable) e;
                }
            }
            e.printStackTrace();

            handler.setThrowable(cause, request.getRequestURI());
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }

    private boolean isStaticResource(HttpServletRequest request) {
        String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        for (String fileext : excludedStaticResources) {
            if (path.endsWith(fileext)) {
                return true;
            }
        }

        return false;
    }

    private boolean isForgedPost(HttpServletRequest request) {

        // We only care about POST
        if(!request.getMethod().equals("POST")) {
            return false;
        }

        // Regular form posts can't set headers, so if we've got an XHR ajax header, we're good
        if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return false;
        }

        // DWR has it's own CSRF detection
        if("/admin/dwr".equals(request.getServletPath()))  {
            return false;
        }

        // It's impossible to add the X-Requested-With parameter or the csrfkey to the first DWR request, so we must manually skip this URL
        if (request.getPathInfo() != null && request.getPathInfo().endsWith("System.pageLoaded.dwr")) {
            return false;
        }

        if(!HttpHelper.isAdminMode(request)) {
            return false;
        }
        
        final Map map = RootContext.getInstance().getBeansOfType(CrossSiteRequestForgeryContentRewriter.class);

        // CSRF protection not configured
        if(map.size() == 0) {
            return false;
        }

        CrossSiteRequestForgeryContentRewriter rewriter = (CrossSiteRequestForgeryContentRewriter) map.values().iterator().next();

        String param = request.getParameter(CrossSiteRequestForgeryContentRewriter.CSRF_KEY);

        // No param must be present
        if(param == null || param.length()  == 0) {
            return true;
        }

        final BigInteger key;
        try {
            key = new BigInteger(param);
        } catch (Exception e) {
            return true;
        }
        final BigInteger expected;

        try {
            expected = new BigInteger(request.getSession().getId().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if(!key.xor(rewriter.getSecret()).equals(expected)) {
            return true;
        }


        return false;

    }

    public void destroy() {

    }
}
