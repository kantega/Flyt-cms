package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.util.Date;

/**
 *
 */
public abstract class AbstractAksessResource implements Resource, PropFindableResource, LockableResource {
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
        return null;
    }

    public LockResult refreshLock(java.lang.String s) {
        return null;
    }

    public void unlock(java.lang.String s) {

    }

    public LockToken getCurrentLock() {
        return null;
    }

}
