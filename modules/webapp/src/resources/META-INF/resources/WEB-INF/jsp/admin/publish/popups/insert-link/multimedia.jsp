<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div class="formElement">
    <div class="heading">
        <label for="urltext"><kantega:label key="aksess.insertlink.multimedia.file"/></label>
    </div>
    <div class="inputs">
        <input type="hidden" name="url" id="url" value="">
        <input type="hidden" name="mimeType" id="mimeType" value="">
        <input type="hidden" name="fileExtension" id="fileExtension" value="">
        <input type="text" class="fullWidth" name="urltext" id="urltext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.multimedia.hint"/>" maxlength="128">
    </div>
    <div class="buttonGroup">
        <a href="Javascript:selectMultimedia()" class="button"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
    </div>
    <script type="text/javascript">

        function getUrlAttributes() {
            var frm = document.linkform;
            var url = frm.url.value,
            clz = 'file ' + frm.fileExtension.value + ' ' + frm.mimeType.value;

            if (url == "") {
                alert("<kantega:label key="aksess.insertlink.nourl"/>");
                return;
            }

            return {
                'href': url,
                'class': clz
            };
        }

        function selectMultimedia() {

            openaksess.editcontext.doInsertTag = false;
            openaksess.editcontext.doInsertUrl = true;
            openaksess.editcontext.insertMultimediaLink = function (metadata) {
                var frm = document.linkform;
                frm.url.value = metadata.url;
                frm.urltext.value = metadata.name;
                frm.mimeType.value = metadata.mimeType.replace(/(\.|\/)/g, '-');
                frm.fileExtension.value = metadata.fileExtension;
            };
            var mmwin = window.open("${pageContext.request.contextPath}/admin/multimedia/Navigate.action", "openAksessPopup", "toolbar=no,width=800,height=500,resizable=yes,scrollbars=yes");

            mmwin.focus();
        }

        /**
         * Callback from popup where user select an image
         * @param url
         * @param text
         */
        openaksess.editcontext.insertMultimedia = insertMultimedia;

        function insertMultimedia(metadata) {
            var frm = document.linkform;
            frm.url.value = metadata.url;
            frm.urltext.value = metadata.name;
            frm.mimeType.value = metadata.mimeType.replace(/(\.|\/)/g, '-');
            frm.fileExtension.value = metadata.fileExtension;
        }

    </script>
</div>
