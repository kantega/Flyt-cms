package no.kantega.publishing.webdav.resourcehandlers;

import com.bradmcevoy.http.Resource;

/**
 *
 */
public interface AksessWebDavResourceHandler {
    public Resource getRootFolder();
    public Resource getResourceFromPath(String path);
    public boolean canHandlePath(String path);
}
