package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import no.kantega.commons.log.Log;

/**
 *
 */
public abstract class AbstractAksessResource implements Resource, PropFindableResource, LockableResource {
    private static Map<String, LockToken> locks = new HashMap<String, LockToken>();

    public String getUniqueId() {
        return null;
    }

    public String getName() {
        return null;
    }

    public Object authenticate(String user, String password) {
        return user;
    }

    public boolean authorise(Request request, Request.Method method, Auth auth) {
        return true;
    }

    public String getRealm() {
        return "aksess";
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
        Log.debug(this.getClass().getName(), "lock():" + lockTimeout + ", " + lockInfo + " on " + getName());
        LockToken token = new LockToken();
        token.info = lockInfo;
        token.timeout = LockTimeout.parseTimeout("30");
        token.tokenId = getUniqueId();
        locks.put(getUniqueId(), token);
        return LockResult.success(token);
    }

    public LockResult refreshLock(String tokenId) {
        LockToken currentLock = locks.get(getUniqueId());
        Log.debug(this.getClass().getName(), "refreshLock():" + tokenId + " on " + getName());
        LockToken token = new LockToken();
        token.info = null;
        token.timeout = LockTimeout.parseTimeout("30");
        token.tokenId = currentLock.tokenId;
        currentLock = token;
        return LockResult.success(token);
    }

    public void unlock(String tokenId) {
        Log.debug(this.getClass().getName(), "Unlock on " + getName());
        locks.remove(getUniqueId());
    }

    public LockToken getCurrentLock() {
        Log.debug(this.getClass().getName(), "getCurrentLock on" + getName());
        return locks.get(getUniqueId());
    }

}
