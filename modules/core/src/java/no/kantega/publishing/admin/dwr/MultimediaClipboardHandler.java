package no.kantega.publishing.admin.dwr;

import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.service.MultimediaService;

/**
 *  Handles cut and copy of Multimedia objects to the clipboard
 */
public class MultimediaClipboardHandler extends AbstractClipboardHandler {
    @Override
    public BaseObject getBaseObjectFromId(int id) {
        MultimediaService mediaService = new MultimediaService(getRequest());
        return mediaService.getMultimedia(id);
    }
}
