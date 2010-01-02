<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function buttonOkPressed() {
        var frm = document.linkform;

        var url = frm.url.value;
        if (url == "") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            frm.url.focus();
            return;
        }

        url = "mailto:" + url;
        var subject = frm.subject.value;
        if (subject != "") {
            url = url + "?subject=" + escape(subject);
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
                setAttributes(elements[i], url);
            }
        } else {
            setAttributes(element, url);
        }
        editor.execCommand("mceEndUndoLevel");
        getParent().ModalWindow.close();
    }

    function setAttributes(element, url) {
        var tinydom = getParent().tinymce.EditorManager.activeEditor.dom;
        tinydom.setAttrib(element, 'href', url);
    }
</script>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.email.recipient"/></label></div>
    <div class="inputs"><input type="text" class="fullWidth" name="url" value="${url}" maxlength="128"></div>
</div>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.email.subject"/></label></div>
    <div class="inputs"><input type="text" class="fullWidth" name="subject" value="" maxlength="128"></div>
</div>
</fieldset>

