package no.kantega.publishing.webdav;

import com.bradmcevoy.http.*;

import java.util.Date;
import java.util.List;

import no.kantega.publishing.common.data.Multimedia;

/**
 *
 */
public class AksessMediaFolderResource extends AbstractAksessMultimediaResource implements PropFindableResource, CollectionResource {
    private Multimedia folder;

    public AksessMediaFolderResource(Multimedia media) {
        super(media);
    }    

    @Override
    public Resource child(String s) {
        return null;
    }

    @Override
    public List<? extends Resource> getChildren() {
        return null;
    }
}
