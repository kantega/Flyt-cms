package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.util.List;
import java.util.ArrayList;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.ao.MultimediaAO;

/**
 *
 */
public class AksessMediaFolderResource extends AbstractAksessMultimediaResource implements CollectionResource {

    public AksessMediaFolderResource(Multimedia media) {
        super(media);
    }    

    @Override
    public Resource child(String s) {
        return null;
    }

    @Override
    public List<? extends Resource> getChildren() {
        List<Resource> children = new ArrayList<Resource>();

        List<Multimedia> multimedia = MultimediaAO.getMultimediaList(media.getId());
        for (Multimedia m : multimedia) {
            if (m.getType() == MultimediaType.FOLDER) {
                children.add(new AksessMediaFolderResource(m));
            } else {
                children.add(new AksessMediaFileResource(m));
            }

        }
        return children;
    }
}
