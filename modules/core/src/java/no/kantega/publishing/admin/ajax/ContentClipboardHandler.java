package no.kantega.publishing.admin.ajax;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 *  Handles cut and copy of Content objects to the clipboard
 */
@Controller
@RequestMapping("/admin/publish/ContentClipboard")
public class ContentClipboardHandler extends AbstractClipboardHandler{

    @Autowired
    private ContentIdHelper contentIdHelper;

    public BaseObject getBaseObjectFromId(String id, HttpServletRequest request) {
        ContentManagementService cms = new ContentManagementService(request);
        Content content = null;
        try {
            ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, id);
            content = cms.getContent(cid);
        } catch (NotAuthorizedException | ContentNotFoundException e) {
            // Do nothing
        }
        return content;
    }

    public String getClipboardType() {
        return AdminSessionAttributes.CLIPBOARD_CONTENT;
    }
}
