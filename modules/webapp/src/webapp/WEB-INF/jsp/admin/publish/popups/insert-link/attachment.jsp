<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier" %>
<%@ page import="no.kantega.publishing.common.data.Attachment" %>
<%@ page import="no.kantega.publishing.admin.AdminSessionAttributes" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function buttonOkPressed() {
        var frm = document.linkform;
        var url = frm.url.options[frm.url.selectedIndex].value;
        if (url == "" || url == "http://") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            frm.url.focus();
            return;
        }

        var anchor = frm.anchor.value;
        if (anchor != "") {
            if (anchor.charAt(0) == '#') {
                anchor = anchor.substring(0, anchor.length);
            }
            // AttachmentRequestHandler will send correct anchor for PDF/Word
            url = url + "&anchor=" + escape(anchor);
            url = url + "#" + anchor;
        }

        var attribs = {'href': url};
        var editor = getParent().tinymce.EditorManager.activeEditor;
        editor.execCommand("mceBeginUndoLevel");
        var elements = getSelectedElements(editor);
        for (var i = 0, n = elements.length; i < n; i++) {
            setAttributes(editor, elements[i], attribs);
        }
        editor.execCommand("mceEndUndoLevel");
        getParent().ModalWindow.close();
    }

    function getSelectedElements(editor) {
        var elements = [];
        var element = editor.selection.getNode();
        element = editor.dom.getParent(element, "A");
        if (element == null) {
            editor.getDoc().execCommand("unlink", false, null);
            editor.execCommand("CreateLink", false, "#insertlink_temp_url#", {skip_undo : 1});
            elements = getParent().tinymce.grep(
                    editor.dom.select("a"),
                    function(n) {
                        return editor.dom.getAttrib(n, 'href') == '#insertlink_temp_url#';
                    });
        } else {
            elements.push(element);
        }
        return elements;
    }

    function setAttributes(editor, element, attributes) {
        for (var key in attributes) {
            editor.dom.setAttrib(element, key, attributes[key]);
        }
    }
</script>

<div class="formElement">
    <div class="heading">
        <label><kantega:label key="aksess.insertlink.attachment.title"/></label>
    </div>
    <div class="buttonGroup">
        <a href="AddAttachment.action?insertLink=true" class="button add"><span><kantega:label key="aksess.button.nyfil"/></span></a>
    </div>
    <div class="inputs">
        <select name="url" class="fullWidth">
            <%
                Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
                if (current != null) {
                    List attachments;
                    if (current.getId() > 0) {
                        // Existing page
                        ContentIdentifier cid = new ContentIdentifier();
                        cid.setContentId(current.getId());
                        cid.setLanguage(current.getLanguage());
                        attachments = new ContentManagementService(request).getAttachmentList(cid);
                    } else {
                        // New page, attachments have no content-id yet
                        attachments = current.getAttachments();
                    }
                    if (attachments != null) {
                        for (int i=0; i < attachments.size(); i++) {
                            Attachment a = (Attachment)attachments.get(i);
                            String filename = a.getFilename();
                            if (filename.length() > 40) {
                                filename = filename.substring(0, 37) + "...";
                            }
                            out.write("<option value=\"" + Aksess.getContextPath() + "/attachment.ap?id=" + a.getId() + "\">" + filename + "</option>");
                        }
                    }
                }

            %>
        </select>
    </div>
</div>

<div class="formElement">
    <div class="heading">
        <label><kantega:label key="aksess.insertlink.anchor.title"/></label>
    </div>
    <div class="inputs">
        <input type="text" name="anchor" maxlength="64" size="32" value="${anchor}">
    </div>
</div>
