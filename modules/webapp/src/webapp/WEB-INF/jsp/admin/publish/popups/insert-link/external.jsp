<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function buttonOkPressed() {
        var frm = document.linkform;

        var url = frm.url.value;
        if (url == "" || url == "http://") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            frm.url.focus();
            return;
        }

        if (url.charAt(0) == '/') {
            url = "<%=URLHelper.getRootURL(request)%>" + url.substring(1, url.length);
        }

        var anchor = frm.anchor.value;
        if (anchor != "") {
            if (anchor.charAt(0) == '#') {
                anchor = anchor.substring(0, anchor.length);
            }
            url = url + "#" + anchor;
        }

        var attribs = {'href': url};
        if (frm.newwindow.checked) {
            attribs['onclick'] = 'window.open(this.href); return false';
        }

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
    <div class="heading"><label for="url"><kantega:label key="aksess.insertlink.external.url"/></label></div>
    <div class="inputs">
        <input type="text" id="url" class="fullWidth" name="url" value="${url}" maxlength="1024"><br>
        <input type="checkbox" id="newwindow" name="newwindow" <c:if test="${isOpenInNewWindow}">checked</c:if>><label for="newwindow"><kantega:label key="aksess.insertlink.opennewwindow"/></label>
    </div>
</div>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.anchor.title"/></label></div>
    <div class="inputs"><input type="text" name="anchor" size="32" maxlength="64" value="${anchor}"></div>
</div>
