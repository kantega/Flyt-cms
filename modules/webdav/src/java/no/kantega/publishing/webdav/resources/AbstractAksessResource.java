package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class AbstractAksessResource implements Resource, PropFindableResource, LockableResource {
    private static final Logger log = LoggerFactory.getLogger(AbstractAksessResource.class);
    private static Map<String, LockToken> locks = new HashMap<>();

    WebDavSecurityHelper webDavSecurityHelper;
    SecuritySession securitySession;

    protected AbstractAksessResource(WebDavSecurityHelper webDavSecurityHelper) {
        this.webDavSecurityHelper = webDavSecurityHelper;
    }

    public String getUniqueId() {
        return null;
    }

    public String getName() {
        return null;
    }

    public Object authenticate(String user, String password) {
        securitySession = webDavSecurityHelper.authenticate(user, password);
        return securitySession; 
    }

    public boolean authorise(Request request, Request.Method method, Auth auth) {
        return securitySession == null;
    }

    public String getRealm() {
        return "OpenAksess";
    }

    public Date getModifiedDate() {
        return null;
    }

    public String checkRedirect(Request request) {
        return null;
    }

    public Date getCreateDate() {
        return null;
    }

    public LockResult lock(LockTimeout lockTimeout, LockInfo lockInfo) {
        log.debug( "lock():" + lockTimeout + ", " + lockInfo + " on " + getName());
        LockToken token = new LockToken();
        token.info = lockInfo;
        token.timeout = LockTimeout.parseTimeout("30");
        token.tokenId = getUniqueId();
        locks.put(getUniqueId(), token);
        return LockResult.success(token);
    }

    public LockResult refreshLock(String tokenId) {
        LockToken currentLock = locks.get(getUniqueId());
        log.debug( "refreshLock():" + tokenId + " on " + getName());
        LockToken token = new LockToken();
        token.info = null;
        token.timeout = LockTimeout.parseTimeout("30");
        token.tokenId = currentLock.tokenId;
        return LockResult.success(token);
    }

    public void unlock(String tokenId) {
        log.debug( "Unlock on " + getName());
        locks.remove(getUniqueId());
    }

    public LockToken getCurrentLock() {
        log.debug( "getCurrentLock on" + getName());
        return locks.get(getUniqueId());
    }


}
