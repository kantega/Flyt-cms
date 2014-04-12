package no.kantega.publishing.admin.ajax;

import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.service.MultimediaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 *  Handles cut and copy of Multimedia objects to the clipboard
 */
@Controller
@RequestMapping("/admin/publish/MultimediaClipboard")
public class MultimediaClipboardHandler extends AbstractClipboardHandler{
    public BaseObject getBaseObjectFromId(String id, HttpServletRequest request) {
        MultimediaService mediaService = new MultimediaService(request);
        return mediaService.getMultimedia(Integer.parseInt(id));
    }

    public String getClipboardType() {
        return AdminSessionAttributes.CLIPBOARD_MEDIA;
    }
}
