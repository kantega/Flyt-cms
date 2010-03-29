<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function buttonOkPressed() {
        var frm = document.linkform;

        var url = frm.url.options[frm.url.selectedIndex].value;
        if (url == "") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            frm.url.focus();
            return;
        }

        if (url.charAt(0) == '/') {
            url = "<%=URLHelper.getRootURL(request)%>" + url.substring(1, url.length);
        }

        var attribs = {'href': url};
        var editor = getParent().tinymce.EditorManager.activeEditor;
        editor.execCommand("mceBeginUndoLevel");
        var elements = getSelectedElements(editor);
        for (var i = 0, n = elements.length; i < n; i++) {
            setAttributes(editor, elements[i], attribs);
        }
        editor.execCommand("mceEndUndoLevel");
        getParent().openaksess.common.modalWindow.close();
    }

    /*
     *  Searches textfield for anchors and adds them to select list
     */
    function addAnchors() {
        var editor = getParent().tinymce.EditorManager.activeEditor;
        var elements = getParent().tinymce.grep(
                editor.dom.select("img"),
                function(n) {
                    return editor.dom.getAttrib(n, 'src').indexOf('placeholder/anchor.gif') != -1;
                });

        for (var i = 0; i < elements.length; i++) {
            var name = elements[i].name;
            document.linkform.url.options[document.linkform.url.options.length] = new Option(name, '#' + name);
        }
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

    $(document).ready(function() {
        addAnchors();
    });

</script>

<div class="formElement">
    <div class="heading">
        <label><kantega:label key="aksess.insertlink.anchor.title"/></label>
    </div>
    <div class="inputs">
        <select name="url" class="fullWidth">
            <option value=""><kantega:label key="aksess.insertlink.anchor.select"/></option>
        </select>
    </div>
</div>
