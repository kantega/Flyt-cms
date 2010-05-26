package no.kantega.publishing.webdav;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.PropFindableResource;

import java.util.Date;

import no.kantega.publishing.common.data.Multimedia;

/**
 *
 */
public class AbstractAksessMultimediaResource implements Resource, PropFindableResource {
    Multimedia media;

    public AbstractAksessMultimediaResource(Multimedia media) {
        this.media = media;
    }

    @Override
    public String getUniqueId() {
        return "media-" + media.getId();
    }

    @Override
    public String getName() {
        return media.getName();
    }

    public Object authenticate(String user, String pwd) {
        return user;
    }

    public boolean authorise(Request request, Request.Method method, Auth auth) {
        return true;
    }

    public String getRealm() {
        return "aksess";
    }

    @Override
    public Date getModifiedDate() {
        return media.getLastModified();
    }

    @Override
    public String checkRedirect(Request request) {
        return null;
    }

    @Override
    public Date getCreateDate() {
        return media.getLastModified();
    }
}
