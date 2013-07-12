package no.kantega.publishing.webdav.resourcehandlers;

import com.bradmcevoy.http.Resource;
import no.kantega.commons.log.Log;
import no.kantega.publishing.webdav.resources.AksessMediaFileResource;
import no.kantega.publishing.webdav.resources.AksessMediaFolderResource;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavMultimediaHelper;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavSecurityHelper;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.Multimedia;

/**
 *
 */
public class AksessWebDavMultimediaResourceHandler implements AksessWebDavResourceHandler {
    private final String MULTIMEDIA_PATH = "/multimedia";
    protected WebDavSecurityHelper webDavSecurityHelper;
    protected WebDavMultimediaHelper webDavMultimediaHelper;

    public Resource getRootFolder() {
        return webDavMultimediaHelper.getRootFolder();
    }

    public Resource getResourceFromPath(String path) {
        path = path.substring(path.indexOf(MULTIMEDIA_PATH) +  + MULTIMEDIA_PATH.length(), path.length());
        Log.debug(this.getClass().getName(), "Get multimedia resource:" + path);
        if (path.equals("/") || path.equals("")) {
            return webDavMultimediaHelper.getRootFolder();
        } else {
            Resource resource = null;
            Multimedia media = webDavMultimediaHelper.getMultimediaByPath(path);
            
            if (media != null) {
                Log.debug(this.getClass().getName(), "Found media object:" + media.getId() + " for path:" + path);
                if (media.getType() == MultimediaType.FOLDER) {
                    return new AksessMediaFolderResource(media, webDavSecurityHelper, webDavMultimediaHelper);
                } else {
                    return new AksessMediaFileResource(media, webDavSecurityHelper, webDavMultimediaHelper);
                }
            } else {
                Log.debug(this.getClass().getName(), "No media object found for path:" + path);
            }
            return resource;
        }
    }

    public boolean canHandlePath(String path) {
        return path.startsWith(MULTIMEDIA_PATH);
    }

    public void setWebDavMultimediaHelper(WebDavMultimediaHelper webDavMultimediaHelper) {
        this.webDavMultimediaHelper = webDavMultimediaHelper;
    }

    public void setWebDavSecurityHelper(WebDavSecurityHelper webDavSecurityHelper) {
        this.webDavSecurityHelper = webDavSecurityHelper;
    }
}
