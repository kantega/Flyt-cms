package no.kantega.publishing.admin.dwr;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.directwebremoting.annotations.RemoteProxy;

/**
 *  Handles cut and copy of Content objects to the clipboard
 */
@RemoteProxy(name="ContentClipboardHandler")
public class ContentClipboardHandler extends AbstractClipboardHandler {

    public BaseObject getBaseObjectFromId(String id) {
        ContentManagementService cms = new ContentManagementService(getRequest());
        Content content = null;
        try {
            ContentIdentifier cid = ContentIdHelper.fromRequestAndUrl(getRequest(), id);
            content = cms.getContent(cid);
        } catch (NotAuthorizedException e) {
            // Do nothing
        } catch (ContentNotFoundException e) {
            // Do nothing
        }
        return content;
    }

    public String getClipboardType() {
        return AdminSessionAttributes.CLIPBOARD_CONTENT;
    }
}
