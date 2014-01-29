<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function getUrlAttributes() {
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

        var attribs = {'href': url};
        if (frm.newwindow.checked) {
            attribs['onclick'] = 'window.open(this.href); return false';
        }

        return attribs;
    }
</script>

<div class="formElement">
    <div class="heading"><label for="url"><kantega:label key="aksess.insertlink.external"/></label></div>
    <div class="inputs">
        <input type="url" id="url" class="fullWidth" name="url" value="${url}" maxlength="1024"><br>
        <input type="checkbox" id="newwindow" name="newwindow" <c:if test="${isOpenInNewWindow}">checked</c:if>><label for="newwindow"><kantega:label key="aksess.insertlink.opennewwindow"/></label>
    </div>
</div>
