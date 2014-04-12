package no.kantega.publishing.admin.ajax;

import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.admin.model.ClipboardStatus;
import no.kantega.publishing.api.model.BaseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Common Clipboard controller
 */
public abstract class AbstractClipboardHandler {

    @RequestMapping("/copy.action")
    public ResponseEntity copy(@RequestParam String id, HttpServletRequest request) {
        copyOrCut(id, ClipboardStatus.COPIED, request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping("/cut.action")
    public ResponseEntity cut(@RequestParam String id, HttpServletRequest request) {
        copyOrCut(id, ClipboardStatus.CLIPPED, request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping("/isEmpty.action")
    public @ResponseBody boolean isClipboardEmpty(HttpServletRequest request) {
        return getClipboard(request.getSession()).isEmpty();
    }

    private boolean copyOrCut(String id, ClipboardStatus status, HttpServletRequest request) {
        List<BaseObject> items = new ArrayList<>();

        BaseObject current = getBaseObjectFromId(id, request);
        if (current != null) {
            items.add(current);
        }
        Clipboard clipboard = getClipboard(request.getSession());
        clipboard.setItems(items);
        clipboard.setStatus(status);
        return clipboard.isEmpty();
    }

    private Clipboard getClipboard(HttpSession session) {
        Clipboard clipboard = null;
        if (session != null) {
            clipboard = (Clipboard) session.getAttribute(getClipboardType()) ;
            if (clipboard == null) {
                clipboard = new Clipboard();
                session.setAttribute(getClipboardType(), clipboard);
            }
        }
        return clipboard;
    }

    public abstract BaseObject getBaseObjectFromId(String id, HttpServletRequest request);

    public abstract String getClipboardType();
}