package no.kantega.publishing.webdav;

import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.Resource;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;


/**
 *
 */
public class AksessResourceFactory implements ResourceFactory {

    public AksessResourceFactory() {

    }
    @Override
    public Resource getResource(String host, String path) {
        if (path.startsWith("/content/")) {
            return null;
        } else if (path.startsWith("/multimedia/")) {
            return getMultimediaResource(path);
        }
        return null;
    }

    private Resource getMultimediaResource(String path) {
        Log.debug(this.getClass().getName(), "Get path:" + path);

        Multimedia media = new Multimedia();
        media.setType(MultimediaType.FOLDER);

        AksessMediaFolderResource resource = new AksessMediaFolderResource(media);

        return resource;
    }
}
