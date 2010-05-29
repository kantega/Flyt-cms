package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Request;

import java.util.Date;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavMultimediaHelper;

/**
 *
 */
public abstract class AbstractAksessMultimediaResource extends AbstractAksessResource {
    protected Multimedia media;
    protected WebDavMultimediaHelper webDavMultimediaHelper;


    public AbstractAksessMultimediaResource(Multimedia media, WebDavMultimediaHelper webDavMultimediaHelper) {
        this.media = media;
        this.webDavMultimediaHelper = webDavMultimediaHelper;
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
