package no.kantega.publishing.admin.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

import java.util.List;
import java.util.ArrayList;

import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.admin.model.ClipboardStatus;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.NotAuthorizedException;

import javax.servlet.http.HttpSession;

/**
 *  Handles cut and copy of Content objects to the clipboard
 */
@RemoteProxy(name="ContentClipboardHandler")
public class ContentClipboardHandler extends AbstractDwrController {
    @RemoteMethod
    public void copy(String url) {
        copyOrCut(url, ClipboardStatus.COPIED);
    }

    @RemoteMethod
    public void cut(String url) {
        copyOrCut(url, ClipboardStatus.CLIPPED);
    }

    private boolean copyOrCut(String url, ClipboardStatus status) {
        ContentManagementService cms = new ContentManagementService(getRequest());
        try {
            List<BaseObject> items = new ArrayList<BaseObject>();

            ContentIdentifier cid = new ContentIdentifier(url);
            Content current = cms.getContent(cid);
            if (current != null) {
                items.add(current);
            }
            Clipboard clipboard = getClipboard();
            clipboard.setItems(items);
            clipboard.setStatus(status);
            return clipboard.isEmpty();
        } catch (ContentNotFoundException e) {
            Log.error(this.getClass().getName(), e, null, null);
        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e, null, null);
        }
        return false;
    }

    private Clipboard getClipboard() {
        Clipboard clipboard = null;
        HttpSession session = getSession();
        if (session != null) {
            clipboard = (Clipboard) session.getAttribute(AdminSessionAttributes.CLIPBOARD_CONTENT) ;
            if (clipboard == null) {
                clipboard = new Clipboard();
                session.setAttribute(AdminSessionAttributes.CLIPBOARD_CONTENT, clipboard);
            }
        }
        return clipboard;
    }
}
