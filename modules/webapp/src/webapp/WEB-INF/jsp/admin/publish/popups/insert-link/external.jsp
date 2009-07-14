<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function insertLink() {
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

        if (frm.newwindow.checked) {
            // Cant use CreateLink with target
            var range = getRange(window.opener.focusField);
            if (range) {
                var node = getNodeFromRange(range);
                var selectedText = getHTMLFromRange(range);
                try {
                    // if user has selected a link, remove old link
                    if (node.tagName == "A") {
                        selectedText = node.innerHTML;
                    }
                    pasteHTML(window.opener.focusField, '<a href="' + url + '" onclick="window.open(this.href); return false">' + selectedText  + '</a>');
                } catch (e) {

                }
            }
        } else {
            window.opener.createLink(url);
        }
    }
</script>

<div class="formElement">
    <div class="heading"><label for="url"><kantega:label key="aksess.insertlink.external.url"/></label></div>
    <div class="inputs">
        <input type="text" id="url" class="fullWidth" name="url" value="${url}" maxlength="1024"><br>
        <input type="checkbox" id="newwindow" name="newwindow" <c:if test="openInNewWindow">checked</c:if>><label for="newwindow"><kantega:label key="aksess.insertlink.opennewwindow"/></label>
    </div>
</div>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.anchor.title"/></label></div>
    <div class="inputs"><input type="text" name="anchor" size="32" maxlength="64" value="${anchor}"></div>
</div>
