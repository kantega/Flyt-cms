<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    // Used by popup when sending data back to this form
    
    openaksess.editcontext.doInsertTag = false;

    openaksess.editcontext.insertIdAndValueIntoForm = function (id, text) {
        var frm = document.linkform;
        if (frm.smartlink.checked) {
            frm.url_contentId.value = id;
            frm.url_contentIdtext.value = text;
        } else {
            frm.url_associationId.value = id;
            frm.url_associationIdtext.value = text;
        }
    };


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

        var attribs = {'href': url}; // TODO: "smartlink"??
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

    function selectPage() {
        var frm = document.linkform;

        var url = "SelectContent.action";
        if (frm.smartlink.checked) {
            url += "?useContentId=true";
        }
        var contentwin = window.open(url, "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
        contentwin.focus();
    }

    function updateVisibleFields() {
        var frm = document.linkform;
        var associationIdForm = document.getElementById("AssociationId");
        var contentIdForm = document.getElementById("ContentId");

        if (frm.smartlink.checked) {
            associationIdForm.style.display = "none";
            contentIdForm.style.display = "block";
        } else {
            associationIdForm.style.display = "block";
            contentIdForm.style.display = "none";
        }
    }

</script>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.internal.url"/></label></div>
    <div class="inputs">
        <div id="AssociationId" <c:if test="${smartlink}">style="display:none"</c:if>>
            <input type="hidden" name="url_associationId" id="url_associationId" value=""><input type="text" name="url_associationIdtext" id="url_associationIdtext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.internal.hint"/>" class="fullWidth" maxlength="128">
            <script type="text/javascript">
                $("#url_associationIdtext").autocomplete("${pageContext.request.contextPath}/ajax/AutocompleteContent.action").result(openaksess.editcontext.autocompleteInsertIntoFormCallback);
            </script>
        </div>
        <div id="ContentId" <c:if test="${!smartlink}">style="display:none"</c:if>>
            <input type="hidden" name="url_contentId" id="url_contentId" value=""><input type="text" name="url_contentIdtext" id="url_contentIdtext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.internal.hint"/>" class="fullWidth" maxlength="128">
            <script type="text/javascript">
                $("#url_contentIdtext").autocomplete("${pageContext.request.contextPath}/ajax/AutocompleteContent.action?useContentId=true").result(openaksess.editcontext.autocompleteInsertIntoFormCallback);
            </script>
        </div>
        <div>
            <input type="checkbox" name="smartlink" onclick="updateVisibleFields()" <c:if test="${smartlink}">checked="checked"</c:if>><kantega:label key="aksess.insertlink.smart"/>
            <div class="ui-state-highlight"><kantega:label key="aksess.insertlink.smart.hint"/></div>
        </div>
    </div>
    <div class="buttonGroup">
        <a href="#" onclick="selectPage()" class="button"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
    </div>
</div>


<div class="formElement">
    <div class="heading"><label for="anchor"><kantega:label key="aksess.insertlink.anchor.title"/></label></dvi>
        <div class="inputs"><input type="text" id="anchor" name="anchor" maxlength="64" value="${anchor}"></div>
    </div>
</div>