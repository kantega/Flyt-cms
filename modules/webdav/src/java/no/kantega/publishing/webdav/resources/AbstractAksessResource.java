package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Auth;

import java.util.Date;

/**
 *
 */
public abstract class AbstractAksessResource implements Resource, PropFindableResource {
    @Override
    public String getUniqueId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object authenticate(String user, String password) {
        return user;
    }

    @Override
    public boolean authorise(Request request, Request.Method method, Auth auth) {
        return true;
    }

    @Override
    public String getRealm() {
        return "aksess";
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }

    @Override
    public String checkRedirect(Request request) {
        return null;
    }

    @Override
    public Date getCreateDate() {
        return null;
    }
}
