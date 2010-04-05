package no.kantega.publishing.admin.dwr;

import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.admin.AdminSessionAttributes;
import org.directwebremoting.annotations.RemoteProxy;

/**
 *  Handles cut and copy of Multimedia objects to the clipboard
 */
@RemoteProxy(name="MultimediaClipboardHandler")
public class MultimediaClipboardHandler extends AbstractClipboardHandler {
    public BaseObject getBaseObjectFromId(String id) {
        MultimediaService mediaService = new MultimediaService(getRequest());
        return mediaService.getMultimedia(Integer.parseInt(id));
    }

    public String getClipboardType() {
        return AdminSessionAttributes.CLIPBOARD_MEDIA;
    }
}
