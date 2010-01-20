package no.kantega.publishing.admin.dwr;

import org.directwebremoting.annotations.RemoteProxy;

import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.commons.exception.NotAuthorizedException;

/**
 *  Handles cut and copy of Content objects to the clipboard
 */
@RemoteProxy(name="ContentClipboardHandler")
public class ContentClipboardHandler extends AbstractClipboardHandler {

    @Override
    public BaseObject getBaseObjectFromId(int id) {
        ContentManagementService cms = new ContentManagementService(getRequest());
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(id);
        Content content = null;
        try {
            content = cms.getContent(cid);
        } catch (NotAuthorizedException e) {
            // Do nothing
        }
        return content;
    }
}
