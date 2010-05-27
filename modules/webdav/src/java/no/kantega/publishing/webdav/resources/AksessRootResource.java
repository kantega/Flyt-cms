package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.util.List;
import java.util.ArrayList;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;

/**
 *
 */
public class AksessRootResource extends AbstractAksessResource implements CollectionResource {
    List<Resource> children;

    public AksessRootResource(List<Resource> children) {
        this.children = children;
    }

    public String getUniqueId() {
        return null;
    }

    public String getName() {
        return "";
    }

    public Resource child(String s) {
        return null;
    }

    @Override
    public List<? extends Resource> getChildren() {        
        return children;
    }
}
