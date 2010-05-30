package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Request;

import java.util.Date;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavMultimediaHelper;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavSecurityHelper;
import no.kantega.publishing.security.data.enums.Privilege;

/**
 *
 */
public abstract class AbstractAksessMultimediaResource extends AbstractAksessResource {
    protected Multimedia media;
    protected WebDavMultimediaHelper webDavMultimediaHelper;


    public AbstractAksessMultimediaResource(Multimedia media, WebDavSecurityHelper webDavSecurityHelper, WebDavMultimediaHelper webDavMultimediaHelper) {
        super(webDavSecurityHelper);
        this.media = media;
        this.webDavMultimediaHelper = webDavMultimediaHelper;
    }

    public String getUniqueId() {
        return "media-" + media.getId();
    }

    public String getName() {
        return media.getName();
    }

    public boolean authorise(Request request, Request.Method method, Auth auth) {
        if (securitySession == null) {
            return false;
        } else {
            if (method.isWrite) {
                return securitySession.isAuthorized(media, Privilege.UPDATE_CONTENT);
            } else {
                return securitySession.isAuthorized(media, Privilege.VIEW_CONTENT);
            }

        }
    }

    public String getRealm() {
        return "aksess";
    }

    public Date getModifiedDate() {
        return media.getLastModified();
    }

    public String checkRedirect(Request request) {
        return null;
    }

    public Date getCreateDate() {
        return media.getLastModified();
    }
}
