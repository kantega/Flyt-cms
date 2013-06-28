package no.kantega.publishing.api.security;

import no.kantega.security.api.identity.Identity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Implementers of this interface determine how a logged in user should be remembered between successive visits
 * to the site. Users are typically remembered through a cookie, though another approach could be to remember user logins
 * using a database.
 */
public interface RememberMeHandler {

    /**
     * Called after a successful login with the authenticated username and domain.
     * The user must be remembered securely and in a format that can be retrieved by {@link #getRememberedIdentity(HttpServletRequest)}.
     * @param response successful login response
     * @param username authenticated user's username
     * @param domain authenticated user's domain
     */
    public void rememberUser(HttpServletResponse response, String username, String domain);


    /**
     * Retrieves a remembered user from remember-me-storage based on the http request.
     * @param request request for an arbitrary page.
     * @return identity containing a username/domain pair.
     */
    public Identity getRememberedIdentity(HttpServletRequest request);

    /**
     * Removes any remember-me-tokens or similar, i.e. logging out the user.
     * @param request logout request
     * @param response
     *
     */
    void forgetUser(HttpServletRequest request, HttpServletResponse response);
}
