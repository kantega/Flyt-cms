package no.kantega.publishing.webdav.resourcehandlers;

import com.bradmcevoy.http.Resource;
import no.kantega.commons.log.Log;
import no.kantega.publishing.webdav.resources.AksessMediaFileResource;
import no.kantega.publishing.webdav.resources.AksessMediaFolderResource;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.ao.MultimediaAO;

/**
 *
 */
public class AksessWebDavMultimediaResourceHandler implements AksessWebDavResourceHandler {
    private final String MULTIMEDIA_PATH = "/multimedia";

    public Resource getRootFolder() {
        Multimedia media = new Multimedia();
        media.setId(0);
        media.setName("multimedia");
        return  new AksessMediaFolderResource(media);
    }

    public Resource getResourceFromPath(String path) {
        path = path.substring(path.indexOf(MULTIMEDIA_PATH) +  + MULTIMEDIA_PATH.length(), path.length());
        Log.debug(this.getClass().getName(), "Get multimedia resource:" + path);

        if (path.equals("/")) {
            return getRootFolder();
        } else {
            Resource resource = null;

            Multimedia media = null;

            int parentId = 0;

            String pathElements[] = path.split("/");
            for (int i = 0; i < pathElements.length; i++) {
                String pathElement = pathElements[i];
                if (pathElement.length() > 0) {
                    System.out.println("looking for:" + pathElement);
                    if (pathElement.contains(".")) {
                        // Files / folders which start with . are ignored
                        if (pathElement.startsWith(".")) {
                            return null;
                        }
                        // . is a forbidden value in OpenAksess folder / file names, used only for fileextension
                        // Must remove fileextension before search
                        pathElement = pathElement.substring(0, pathElement.indexOf("."));
                    }
                    // Find child with name
                    media = MultimediaAO.getMultimediaByParentIdAndName(parentId, pathElement);

                    if (media == null) {
                        return null;
                    }
                    parentId = media.getId();
                }
            }

            if (media != null) {
                Log.debug(this.getClass().getName(), "Found media object:" + media.getId() + " for path:" + path);
                if (media.getType() == MultimediaType.FOLDER) {
                    return new AksessMediaFolderResource(media);
                } else {
                    return new AksessMediaFileResource(media);
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
}
