package no.kantega.publishing.webdav.resourcehandlers;

import com.bradmcevoy.http.Resource;
import no.kantega.publishing.webdav.resources.AksessRootResource;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavSecurityHelper;

import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class AksessWebDavRootResourceHandler implements AksessWebDavResourceHandler {
    private WebDavSecurityHelper webDavSecurityHelper;

    private List<AksessWebDavResourceHandler> resourceHandlers;

    public Resource getRootFolder() {
        return null;
    }

    public Resource getResourceFromPath(String path) {
        List<Resource> children = new ArrayList<Resource>();
        for (AksessWebDavResourceHandler resourceHandler : resourceHandlers) {
            children.add(resourceHandler.getRootFolder());
        }
        return new AksessRootResource(webDavSecurityHelper, children);
    }

    public boolean canHandlePath(String path) {
        return path.equals("/") || path.equals("");
    }

    public void setResourceHandlers(List<AksessWebDavResourceHandler> resourceHandlers) {
        this.resourceHandlers = resourceHandlers;
    }

    public void setWebDavSecurityHelper(WebDavSecurityHelper webDavSecurityHelper) {
        this.webDavSecurityHelper = webDavSecurityHelper;
    }
}
