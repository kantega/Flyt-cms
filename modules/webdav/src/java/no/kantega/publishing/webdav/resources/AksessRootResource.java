package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.util.List;
import java.util.ArrayList;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavSecurityHelper;

/**
 *
 */
public class AksessRootResource extends AbstractAksessResource implements CollectionResource {
    List<Resource> children;

    public AksessRootResource(WebDavSecurityHelper webDavSecurityHelper, List<Resource> children) {
        super(webDavSecurityHelper);
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

    public List<? extends Resource> getChildren() {        
        return children;
    }
}
