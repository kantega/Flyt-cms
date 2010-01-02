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

    function setAttributes(element, url) {
        var tinydom = getParent().tinymce.EditorManager.activeEditor.dom;
        tinydom.setAttrib(element, 'href', url);
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
