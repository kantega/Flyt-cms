package no.kantega.publishing.rating.util;

import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;

public class RatingUtil {

    /**
     * Returns the userid for which a rating is saved.
     * If the user is logged in, the username is returned, otherwise the session id is used as userid.
     *
     * @param request
     * @return userId
     */
    public static String getUserId(HttpServletRequest request) {
        SecuritySession secSession = SecuritySession.getInstance(request);
        if(secSession.isLoggedIn()) {
            return secSession.getUser().getId();
        }
        return request.getSession().getId();
    }

    public static String getUserDisplayName(HttpServletRequest request) {
        SecuritySession secSession = SecuritySession.getInstance(request);
        if(secSession.isLoggedIn()) {
            return secSession.getUser().getName();
        }
        return null;
    }
}
