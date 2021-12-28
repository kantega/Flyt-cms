<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    // Used by popup when sending data back to this form

    openaksess.editcontext.doInsertTag = false;

    openaksess.editcontext.insertValueAndNameIntoForm = function (id, text) {
        var frm = document.linkform;
        if (frm.smartlink.checked) {
            frm.url_contentId.value = id;
            frm.url_contentIdtext.value = text;
        } else {
            frm.url_associationId.value = id;
            frm.url_associationIdtext.value = text;
        }
    };


    function getUrlAttributes() {
        var frm = document.linkform;

        var id = "";
        var title = "Tittel";
        if (frm.smartlink.checked) {
            id = frm.url_contentId.value;
            title = frm.url_contentIdtext.value;
        } else {
            id = frm.url_associationId.value;
            title = frm.url_associationIdtext.value;
        }

        if (id == "") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            return;
        }

        var url;
        if (frm.smartlink.checked) {
            url = "<%=URLHelper.getRootURL(request)%>content.ap?contentId=" + id + "&amp;contextId=$contextId$";
        } else {
            url =  "<%=URLHelper.getRootURL(request)%>content/"+ id + "/" + openaksess.common.uglifyTitle(title);
        }

        var anchor = frm.anchor.value;
        if (anchor != "") {
            if (anchor.charAt(0) == '#') {
                anchor = anchor.substring(0, anchor.length);
            }
            url = url + "#" + anchor;
        }

        return {'href': url};
    }

    function selectPage() {
        var frm = document.linkform;

        var url = "${pageContext.request.contextPath}/admin/publish/popups/SelectContent.action";
        if (frm.smartlink.checked) {
            url += "?selectContentId=true";
        }
        var contentwin = window.open(url, "openAksessPopup", "toolbar=no,width=400,height=500,resizable=yes,scrollbars=yes");
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
            <input type="hidden" name="url_associationId" id="url_associationId" value="">
            <input type="text" name="url_associationIdtext" id="url_associationIdtext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.internal.hint"/>" class="fullWidth" maxlength="128">
            <script type="text/javascript">
                $(document).ready(function() {
                    $("#url_associationIdtext").oaAutocomplete({
                        defaultValue: '<kantega:label key="aksess.insertlink.internal.hint"/>',
                        source: "${pageContext.request.contextPath}/ajax/AutocompleteContent.action",
                        select: openaksess.editcontext.autocompleteInsertIntoFormCallback
                    });
                });
            </script>
        </div>
        <div id="ContentId" <c:if test="${!smartlink}">style="display:none"</c:if>>
            <input type="hidden" name="url_contentId" id="url_contentId" value="">
            <input type="text" name="url_contentIdtext" id="url_contentIdtext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.internal.hint"/>" class="fullWidth" maxlength="128">
            <script type="text/javascript">
                $(document).ready(function() {
                    $("#url_contentIdtext").oaAutocomplete({
                        defaultValue: '<kantega:label key="aksess.insertlink.internal.hint"/>',
                        source: "${pageContext.request.contextPath}/ajax/AutocompleteContent.action?useContentId=true",
                        select: openaksess.editcontext.autocompleteInsertIntoFormCallback
                    });
                });
            </script>
        </div>
        <div>
            <input type="checkbox" name="smartlink" onclick="updateVisibleFields()" <c:if test="${smartlink}">checked="checked"</c:if>><kantega:label key="aksess.insertlink.smart"/>
        </div>
    </div>
    <div class="buttonGroup">
        <a href="#" onclick="selectPage()" class="button"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
    </div>
    <div class="inputs">
        <div class="ui-state-highlight"><kantega:label key="aksess.insertlink.smart.hint"/></div>
    </div>

</div>


<div class="formElement">
    <div class="heading"><label for="anchor"><kantega:label key="aksess.insertlink.anchor.title"/></label></dvi>
        <div class="inputs"><input type="text" id="anchor" name="anchor" maxlength="64" value="${anchor}"></div>
    </div>
</div>
