package no.kantega.publishing.webdav.resourcehandlers.util;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.DefaultIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class WebDavSecurityHelper {
    private static final Logger log = LoggerFactory.getLogger(WebDavSecurityHelper.class);
    public SecuritySession authenticate(String user, String password) {
        String userId = user;
        String domain = Aksess.getDefaultSecurityDomain();
        if (user.contains("\\")) {
            userId = user.substring(0, user.indexOf("\\"));
            domain = user.substring(user.indexOf("\\") + 1, user.length() - 1);
        } else if (user.contains("@")) {
            userId = user.substring(0, user.indexOf("@"));
            domain = user.substring(user.indexOf("@") + 1, user.length() - 1);            
        }

        DefaultIdentity identity = new DefaultIdentity();
        identity.setUserId(userId);
        identity.setDomain(domain);

        SecuritySession session = null;

        try {
            session = SecuritySession.createNewUserInstance(identity, password);
            if (session != null) {
                log.info( "Authentication OK for user:" + user);
            } else {
                log.info( "Authentication FAILED for user:" + user);
            }
        } catch (SystemException e) {
            log.error("", e);
        }

        return session;
    }
}
