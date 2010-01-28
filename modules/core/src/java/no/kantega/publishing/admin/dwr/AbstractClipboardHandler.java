package no.kantega.publishing.admin.dwr;

import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.model.ClipboardStatus;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.common.data.BaseObject;
import org.directwebremoting.annotations.RemoteMethod;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public abstract class AbstractClipboardHandler extends AbstractDwrController {
    @RemoteMethod
    public void copy(int id) {
        copyOrCut(id, ClipboardStatus.COPIED);
    }

    @RemoteMethod
    public void cut(int id) {
        copyOrCut(id, ClipboardStatus.CLIPPED);
    }

    private boolean copyOrCut(int id, ClipboardStatus status) {
        List<BaseObject> items = new ArrayList<BaseObject>();

        BaseObject current = getBaseObjectFromId(id);
        if (current != null) {
            items.add(current);
        }
        Clipboard clipboard = getClipboard();
        clipboard.setItems(items);
        clipboard.setStatus(status);
        return clipboard.isEmpty();
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

    public abstract BaseObject getBaseObjectFromId(int id);
}