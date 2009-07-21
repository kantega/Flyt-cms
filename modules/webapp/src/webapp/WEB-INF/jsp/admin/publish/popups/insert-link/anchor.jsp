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

        window.opener.createLink(url);
    }

    /*
     *  Searches textfield for anchors and adds them to select list
     */
    function addAnchors() {
        if (window.opener) {
            var fld = window.opener.focusField;
            var imgs = fld.contentWindow.document.getElementsByTagName("img");
            for (var j = 0; imgs && j < imgs.length; j++) {
                var img = imgs[j];
                if (img.src.indexOf("placeholder/anchor.gif") != -1) {
                    var n = img.name;
                    document.myform.url_anchor.options[document.myform.url_anchor.options.length] = new Option(n, '#' + n);
                }
            }
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
