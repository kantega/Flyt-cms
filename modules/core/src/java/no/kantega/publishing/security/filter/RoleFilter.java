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

import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.common.exception.ExceptionHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 */
public class RoleFilter implements Filter {
    private ServletContext servletContext;
    private String specifiedRole;
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        specifiedRole = filterConfig.getInitParameter("specifiedRole");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        request.setAttribute(ServletContext.class.getName(), servletContext);



        try {
            ContentManagementService aksessService = new ContentManagementService(request);
            SecuritySession securitySession = aksessService.getSecuritySession();

            // Sjekk at bruker er logget inn
            if (!securitySession.isLoggedIn()) {
                securitySession.initiateLogin(request, response);
                return;
            }

            // Sjekk at bruker er autorisert
            if (specifiedRole!=null && !securitySession.isUserInRole(specifiedRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            response.setDateHeader("Expires", 0);

            filterChain.doFilter(request,  response);
        } catch (Exception e) {
            ExceptionHandler handler = new ExceptionHandler();

            Throwable cause = e;
            if (cause instanceof javax.servlet.jsp.JspException) {
                cause = ((javax.servlet.jsp.JspException) cause).getRootCause();
                if (cause == null) {
                    cause = e;
                }
            }
            if (cause instanceof ServletException) {
                cause = ((ServletException) cause).getRootCause();
                if (cause == null) {
                    cause = e;
                }
            }

            if (cause instanceof java.lang.reflect.InvocationTargetException) {
                cause = ((java.lang.reflect.InvocationTargetException) cause).getTargetException();
                if (cause == null) {
                    cause = e;
                }
            }

            if (cause instanceof java.lang.reflect.UndeclaredThrowableException) {
                cause = ((java.lang.reflect.UndeclaredThrowableException) cause).getUndeclaredThrowable();
                if (cause == null) {
                    cause = e;
                }
            }

            e.printStackTrace();

            handler.setThrowable(cause, request.getRequestURI());
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }

    public void destroy() {
    }
}
