<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
    function getUrlAttributes() {
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

        return {'href': url};
    }
</script>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.email.recipient"/></label></div>
    <div class="inputs"><input type="email" class="fullWidth" name="url" value="${url}" maxlength="128"></div>
</div>

<div class="formElement">
    <div class="heading"><label><kantega:label key="aksess.insertlink.email.subject"/></label></div>
    <div class="inputs"><input type="text" class="fullWidth" name="subject" value="" maxlength="128"></div>
</div>
</fieldset>

