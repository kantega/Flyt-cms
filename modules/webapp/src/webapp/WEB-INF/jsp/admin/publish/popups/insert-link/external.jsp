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

        var anchor = frm.anchor.value;
        if (anchor != "") {
            if (anchor.charAt(0) == '#') {
                anchor = anchor.substring(0, anchor.length);
            }
        }
        if (url.charAt(0) == '/') {
            url = "<%=URLHelper.getRootURL(request)%>" + url.substring(1, url.length);
        }

        var editor = getParent().tinymce.EditorManager.activeEditor;
        var element = editor.selection.getNode();
        element = editor.dom.getParent(element, "A");
        editor.execCommand("mceBeginUndoLevel");
        if (element == null) {
            editor.getDoc().execCommand("unlink", false, null);
            editor.execCommand("CreateLink", false, "#insertlink_temp_url#", {skip_undo : 1});

            var elements = getParent().tinymce.grep(
                    editor.dom.select("a"),
                    function(n) {
                        return editor.dom.getAttrib(n, 'href') == '#insertlink_temp_url#';
                    });
            
            for (i = 0; i < elements.length; i++) {
                setAttributes(elements[i], url, anchor);
            }
        } else {
            setAttributes(element, url, anchor);
        }
        editor.execCommand("mceEndUndoLevel");
        getParent().ModalWindow.close();
    }

    function setAttributes(element, url, anchor) {
        var tinydom = getParent().tinymce.EditorManager.activeEditor.dom;
        var form = document.linkform;
        var href = url;
        if (anchor != "") {
            href = url + "#" + anchor;
        }
        tinydom.setAttrib(element, 'href', href);
        if (form.newwindow.checked) {
            tinydom.setAttrib(element, 'onclick', 'window.open(this.href); return false');
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
