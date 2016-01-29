<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function getUrlAttributes() {
        var frm = document.linkform;

        var url = frm.url.options[frm.url.selectedIndex].value;
        if (url == "") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            frm.url.focus();
            return;
        }

        return {'href': url};
    }

    /*
     *  Searches textfield for anchors and adds them to select list
     */
    function addAnchors() {
        var editor = getParent().tinymce.EditorManager.activeEditor;
        var elements = getParent().tinymce.grep(
                editor.dom.select("a"),
                function(n) {
                    return editor.dom.getAttrib(n, 'class').indexOf('mce-item-anchor') != -1;
                });

        for (var i = 0; i < elements.length; i++) {
            var name = elements[i].id;
            document.linkform.url.options[document.linkform.url.options.length] = new Option(name, '#' + name);
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
