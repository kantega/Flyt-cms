<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function getUrlAttributes() {
        var frm = document.linkform;
        var url = frm.url.value;
        if (url == "") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            return;
        }

        url = "<%=URLHelper.getRootURL(request)%>multimedia.ap?id=" + url;


        return {'href': url};
    }

    function selectMultimedia() {
        openaksess.editcontext.doInsertTag = false;
        var mmwin = window.open("${pageContext.request.contextPath}/admin/multimedia/Navigate.action", "mmWindow", "toolbar=no,width=800,height=500,resizable=yes,scrollbars=yes");
        mmwin.focus();
    }

    /**
     * Callback from popup where user select an image
     * @param id
     * @param text
     */
    openaksess.editcontext.insertIdAndValueIntoForm = function insertIdAndValueIntoForm(id, text) {
        var frm = document.linkform;
        frm.url.value = id;
        frm.urltext.value = text;
    }

</script>

<div class="formElement">
    <div class="heading">
        <label><kantega:label key="aksess.insertlink.multimedia.file"/></label>
    </div>
    <div class="inputs">
        <input type="hidden" name="url" id="url" value=""><input type="text" class="fullWidth" name="urltext" id="urltext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.multimedia.hint"/>" maxlength="128">
    </div>
    <div class="buttonGroup">
        <a href="Javascript:selectMultimedia()" class="button"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
    </div>
    <script type="text/javascript">
        $("#urltext").autocomplete("${pageContext.request.contextPath}/ajax/AutocompleteMultimedia.action").result(openaksess.editcontext.autocompleteInsertMediaIntoFormCallback);
    </script>
</div>
