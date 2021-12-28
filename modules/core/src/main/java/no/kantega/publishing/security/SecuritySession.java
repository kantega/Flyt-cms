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

package no.kantega.publishing.security;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.security.RememberMeHandler;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.security.data.CachedBaseObject;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.login.PostLoginHandlerFactory;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.security.service.SecurityService;
import no.kantega.publishing.security.util.SecurityHelper;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.security.api.identity.*;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class SecuritySession {
    private static final Logger log = LoggerFactory.getLogger(SecuritySession.class);

    private User user = null;
    private Identity identity = null;
    private SecurityRealm realm = null;
    private SiteCache siteCache;
    private RememberMeHandler rememberMeHandler;

    // Husker sist tilgangssjekk
    private CachedBaseObject prevObject = null;
    private int prevPrivilege = -1;
    private boolean prevResult = false;

    private SecuritySession() { }

    public SecuritySession(SecurityRealm realm) {
        this.realm = realm;
    }

    /**
     * Henter en sikkerhetssesjon - Sjekker om en bruker er innlogget, setter i såfall User og Identity objektet
     * @param request
     * @return
     * @throws SystemException
     */
    public static SecuritySession getInstance(HttpServletRequest request) throws SystemException {

        HttpSession httpSession = request.getSession(true);
        SecuritySession session = (SecuritySession) httpSession.getAttribute("aksess.securitySession");
        if (session == null) {
            session = createNewInstance();
            httpSession.setAttribute("aksess.securitySession", session);
        }

        session.setRememberMeHandlerIfNull(request.getServletContext());

        Identity identity = session.rememberMeHandler.getRememberedIdentity(request);
        if (identity == null) {

            IdentityResolver resolver = session.realm.getIdentityResolver();
            try {
                Identity fakeIdentity = getFakeIdentity(request.getServletContext());
                if(fakeIdentity != null) {
                    identity = fakeIdentity;

                } else {
                    identity = resolver.getIdentity(request);
                    log.debug("Got identity {} from resolver {}", identity, resolver.getClass());
                }
            } catch (IdentificationFailedException e) {
                throw new SystemException("IdentificationFailedException", e);
            }
        }
        Identity currentIdentity = session.identity;

        // If your is now logged in or has changed identity, must create new session
        if (identity != null && (currentIdentity == null || !identity.getUserId().equals(currentIdentity.getUserId()) || !identity.getDomain().equals(currentIdentity.getDomain()))) {
            session = createNewUserInstance(identity);
            try {
                session.handlePostLogin(request);
            } catch (ConfigurationException e) {
                throw new SystemException("Konfigurasjonsfeil", e);
            }
            // Innloggede brukere har lengre sesjonstimeout
            if (request.getSession().getMaxInactiveInterval() < Aksess.getSecuritySessionTimeout()) {
                request.getSession().setMaxInactiveInterval(Aksess.getSecuritySessionTimeout());
            }
            httpSession.setAttribute("aksess.securitySession", session);

        } else if (identity == null && currentIdentity != null) {
            log.debug("User logged out by external service - create blank session");
            session = createNewInstance();
            httpSession.setAttribute("aksess.securitySession", session);
            httpSession.removeAttribute("adminMode");
        }

        return session;
    }

    private static Identity getFakeIdentity(ServletContext servletContext) {
        final String fakeUsername = servletContext.getInitParameter("fakeUsername");
        final String fakeUserDomain = servletContext.getInitParameter("fakeUserDomain");
        if(fakeUsername != null && fakeUserDomain != null) {

            return DefaultIdentity.withDomainAndUserId(fakeUserDomain, fakeUsername);
        } else {
            return null;
        }
    }

    /**
     * Create a SecuritySession for a given identity
     * @param identity
     * @return
     */
    public static SecuritySession createNewUserInstance(Identity identity) {
        SecuritySession session = createNewInstance();

        ProfileManager manager = session.realm.getProfileManager();
        Profile p = null;
        try {
            p = manager.getProfileForUser(identity);
        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException("Feil ved henting av profil", e);
        }

        String userId = identity.getDomain() + ":" + identity.getUserId();
        User user = new User();
        if (p != null) {
            user = SecurityHelper.createAksessUser(p);
        } else {
            user.setId(userId);
            user.setGivenName(identity.getUserId());
        }

        List<Role> roles = session.realm.lookupRolesForUser(userId);
        for (Role r : roles) {
            user.addRole(r);
        }

        session.user = user;
        session.identity = identity;
        log.debug("Created new UserInstance for identity {}", userId);
        return session;
    }

    /**
     * Create a SecuritySession for a given identity, checking password
     * @param identity
     * @param password
     * @return SecuritySession if password is correct, else null
     * @throws no.kantega.security.api.common.SystemException
     */
    public static SecuritySession createNewUserInstance(Identity identity, String password) throws no.kantega.security.api.common.SystemException {
        SecuritySession session = createNewUserInstance(identity);
        PasswordManager passwordManager = session.getRealm().getPasswordManager();

        boolean isVerified = passwordManager.verifyPassword(identity, password);
        if (isVerified) {
            return session;
        } else {
            return null;
        }
    }

    public static SecuritySession createNewAdminInstance() throws SystemException {
        SecuritySession session = createNewInstance();

        User admin = new User();
        admin.setGivenName("Aksess");
        admin.setSurname("CMS");
        admin.setId("admin");

        Role role = new Role();
        role.setId(Aksess.getAdminRole());
        role.setName(Aksess.getAdminRole());
        admin.addRole(role);

        session.user = admin;
        log.debug("Creating new Admin instance of SecuritySession");

        return session;
    }

    /**
     * Creates a new SecuritySession pre set with user with no roles except 'everyone'.
     *
     * @return SecuritySession
     */
    public static SecuritySession createNewUnauthenticatedInstance() {
        SecuritySession session = createNewInstance();

        User unauthenticatedUser = new User();
        unauthenticatedUser.setGivenName("Aksess - Unauthenticated");
        unauthenticatedUser.setSurname("CMS");
        unauthenticatedUser.setId("adminUnauthenticated");

        Role role = new Role();
        role.setId(Aksess.getEveryoneRole());
        role.setName(Aksess.getEveryoneRole());
        unauthenticatedUser.addRole(role);

        session.user = unauthenticatedUser;

        return session;
    }

    private static SecuritySession createNewInstance() throws SystemException {
        SecuritySession session = new SecuritySession();
        session.realm = SecurityRealmFactory.getInstance();
        log.debug("Creating new SecuritySession");

        return session;
    }

    /**
     * Håndterer når en ny bruker har blitt logget inn, henter roller, emner, orgenheter osv
     * @param request
     * @throws SystemException
     * @throws ConfigurationException
     */
    public void handlePostLogin(HttpServletRequest request) throws SystemException, ConfigurationException {
        log.debug("handlePostLogin for user {}", user.getId());
        List<Role> roles = realm.lookupRolesForUser(user.getId());
        for (Role role : roles) {
            user.addRole(role);
        }
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());

        if (Aksess.isTopicMapsEnabled()) {
            TopicDao topicDao = ctx.getBean(TopicDao.class);
            user.setTopics(topicDao.getTopicsForSecurityIdentifier(user));

            // Og for roller brukeren har tilgang til
            if (user.getRoles() != null) {
                for (Role role : roles) {
                    List<Topic> topicsForRole = topicDao.getTopicsForSecurityIdentifier(role);
                    for (Topic aTopicsForRole : topicsForRole) {
                        user.addTopic( aTopicsForRole );
                    }
                }
            }
        }

        Map<String, OrganizationManager> orgManagers = ctx.getBeansOfType(OrganizationManager.class);
        if(orgManagers.size() > 0) {
            OrganizationManager<OrgUnit> manager = (OrganizationManager<OrgUnit>) orgManagers.values().iterator().next();
            List<OrgUnit> orgunits = manager.getOrgUnitsAboveUser(user.getId());
            user.setOrgUnits(orgunits);
        }

        PostLoginHandlerFactory plh = ctx.getBean(PostLoginHandlerFactory.class);
        plh.newInstance().handlePostLogin(user, request);
    }

    public boolean isLoggedIn() {
        return getUser() != null;
    }

    public void initiateLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirect = request.getRequestURL().toString();
        String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");
        if (originalUri != null) {
            // Call via 404
            redirect = originalUri;
        }

        String query = request.getQueryString();
        if (query != null && query.length() > 0) {
            redirect += "?" + query;
        }

        IdentityResolver resolver = realm.getIdentityResolver();

        DefaultLoginContext loginContext = new DefaultLoginContext();
        loginContext.setRequest(request);
        loginContext.setResponse(response);
        try {
            loginContext.setTargetUri(new URI(redirect));
        } catch (URISyntaxException e) {
            log.error("Error in url " + redirect, e);
        }
        log.debug( "Initiating login in authentication context: {}", resolver.getAuthenticationContext());
        resolver.initateLogin(loginContext);
    }


    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String redirect = request.getParameter("redirect");
        if (redirect == null || redirect.length() == 0) {
            redirect = Aksess.getContextPath();
        }

        IdentityResolver resolver = realm.getIdentityResolver();
        DefaultLogoutContext logoutContext = new DefaultLogoutContext();
        logoutContext.setRequest(request);
        logoutContext.setResponse(response);
        try {
            logoutContext.setTargetUri(new URI(redirect));
        } catch (URISyntaxException e) {
            log.error("Error in url " + redirect, e);
        }

        setRememberMeHandlerIfNull(request.getServletContext());
        rememberMeHandler.forgetUser(request, response);

        try {
            resolver.initiateLogout(logoutContext);
        } catch (Exception e) {
            // Ikke alle IdentityResolvers håndterer utlogging, f.eks NTLM
        }


        // Nullstill
        user = null;
        identity = null;

        // Reset cachet verdi, bruker har logget ut, andre privilegier
        prevObject = null;
    }

    public User getUser() {
        return user;
    }

    public Identity getIdentity() {
        return identity;
    }

    public boolean isUserInRole(String[] roles) {
        if (roles != null) {
            for (String role : roles) {
                if (SecurityService.isUserInRole(user, role.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isUserInRole(String role) {
        return SecurityService.isUserInRole(user, role.trim());
    }

    public boolean isAuthorized(BaseObject object, int privilege) throws SystemException {
        if (object instanceof Content) {
            // Sjekker om det ikke skal vises sider fra dette nettstedet
            Content c = (Content)object;
            setSiteCacheIfNull();
            Site site = siteCache.getSiteById(c.getAssociation().getSiteId());
            if (site == null || site.isDisabled()) {
                return false;
            }
        }

        if (prevObject != null && prevObject.isSameAs(object) && privilege == prevPrivilege) {
            log.debug("Match on prevObject.isSameAs(object) && privilege == prevPrivilege");
            return prevResult;
        }

        prevResult = SecurityService.isAuthorized(user, object, privilege);
        prevObject = new CachedBaseObject(object);
        prevPrivilege = privilege;

        return prevResult;
    }

    public boolean isApprover(Content c) throws SystemException {
        return SecurityService.isApprover(user, c);
    }

    public List<Role> getAllRoles() throws SystemException {
        return realm.getAllRoles();
    }

    /**
     * Henter userId for alle brukere som har den gitte rollen.
     * @param role roleId
     * @return List med userIds (String)
     * @throws SystemException
     */
    public List<String> getUsersWithRole(String role) throws SystemException {
        return realm.lookupUsersWithRole(role);
    }

    public List<User> searchUsers(String name) throws SystemException {
        return realm.searchUsers(name);
    }


    public SecurityRealm getRealm() {
        return realm;
    }

    private void setSiteCacheIfNull() {
        if(siteCache == null){
            siteCache = RootContext.getInstance().getBean(SiteCache.class);
        }
    }

    private void setRememberMeHandlerIfNull(ServletContext servletContext) {
        if (rememberMeHandler == null) {
            rememberMeHandler = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getBean(RememberMeHandler.class);
        }
    }



}
