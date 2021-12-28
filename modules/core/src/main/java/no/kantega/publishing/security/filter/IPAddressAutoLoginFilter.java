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

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.DefaultIdentityResolver;
import no.kantega.security.api.identity.IdentityResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressAutoLoginFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Pattern includedHostsPattern = null;
    private Pattern ipAddressPattern = null;
    private String defaultUserId = null;
    private String defaultDomain = null;
    private boolean isEnabled;

    public void init(FilterConfig filterConfig) throws ServletException {

        String ipPattern = filterConfig.getInitParameter("ip-address");
        if(ipPattern != null) {
            ipAddressPattern = Pattern.compile(ipPattern);
        }

        String hostsPattern = filterConfig.getInitParameter("include-hosts");
        if(hostsPattern != null) {
            includedHostsPattern = Pattern.compile(hostsPattern);
        }

        defaultUserId = filterConfig.getInitParameter("userid");
        defaultDomain = filterConfig.getInitParameter("domain");

        isEnabled = defaultUserId != null;
        if(isEnabled){
            log.warn("IPAddressAutoLoginFilter is enabled");
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isEnabled) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            try {
                SecuritySession securitySession = SecuritySession.getInstance(request);

                // Hvis bruker er logget inn skal vi ikke gjøre noe som helst
                if (!securitySession.isLoggedIn()) {
                    // Sjekk om autoinnlogging gjelder denne hosten
                    boolean includeHost = true;
                    if (includedHostsPattern != null) {
                        Matcher m = includedHostsPattern.matcher(request.getServerName());
                        includeHost = m.matches();
                    }

                    // Sjekk om brukeren sin IP adresse er den som er angitt
                    if (includeHost) {
                        boolean ipIsIncluded = false;
                        if (ipAddressPattern != null) {
                            Matcher m = ipAddressPattern.matcher(request.getRemoteAddr());
                            ipIsIncluded = m.matches();
                        }
                        if (ipIsIncluded) {
                            HttpSession session = request.getSession(true);
                            DefaultIdentity identity = new DefaultIdentity();
                            identity.setUserId(defaultUserId);
                            identity.setDomain(defaultDomain);

                            SecuritySession newUserInstance = SecuritySession.createNewUserInstance(identity);
                            session.setAttribute("aksess.securitySession", newUserInstance);

                            SecurityRealm realm = newUserInstance.getRealm();
                            User user = newUserInstance.getUser();
                            List<Role> roles = realm.lookupRolesForUser(user.getId());
                            for (Role role : roles) {
                                user.addRole(role);
                            }

                            IdentityResolver resolver = realm.getIdentityResolver();

                            session.setAttribute(resolver.getAuthenticationContext() + DefaultIdentityResolver.SESSION_IDENTITY_NAME, defaultUserId);
                            session.setAttribute(resolver.getAuthenticationContext() + DefaultIdentityResolver.SESSION_IDENTITY_DOMAIN, defaultDomain);

                            log.info("Logged in user {} from ip {}", defaultUserId, request.getRemoteAddr());
                        }
                    }
                }

            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
