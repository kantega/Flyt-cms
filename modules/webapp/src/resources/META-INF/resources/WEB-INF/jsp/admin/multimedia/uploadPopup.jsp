<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>


<html>
<title></title>
<head>
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/editcontext.js"/>"></script>
    <script type="text/javascript" src='<kantega:expireurl url="/wro-oa/admin-popuplayout.js"/>'></script>
    <!--script type="text/javascript" src='${pageContext.request.contextPath}/admin/js/jquery-ui-i18n.min.js'></script-->
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-popuplayout.css"/>">
    <link rel="stylesheet" href="<kantega:expireurl url="/admin/css/multimedia.css"/>">
    <!--link rel="stylesheet" type="text/css" href="admin-popuplayout.css">
    <link rel="stylesheet" href="multimedia.css"-->
</head>
<body class="popup">
<script type="text/javascript">
    function selectMultimedia() {
        openaksess.editcontext.doInsertTag = false;
        openaksess.editcontext.doInsertUrl = true;
        var mmwin = window.open("/kantega-intranett-webapp/admin/publish/popups/SelectMediaFolder.action", "openAksessPopup", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
        mmwin.focus();
    }
    function buttonOkPressed() {
        if (validateUpload()) {
//            addToEditor(formToString());
//            tinymce.execCommand('mceInsertRawHTML', false, formToString());
            var parentWin = (!window.frameElement && window.dialogArguments) || opener || parent || top;
            parentWin.my_namespace_tullepaparm = document.uploadForm.file;
            document.uploadForm.submit();
        }
        return false;
    }
    function formToString(){
//        return $("#File")
        return document.uploadForm.file.toString();
    }
    function validateUpload() {
        if (document.uploadForm.elements['file'].value == "") {
            alert('Vennligst velg en fil som skal lastes opp');
            return false;
        } else {

            var fileName = document.uploadForm.elements['file'].value;
            var blacklistedFileTypes = new Array();

            for (i = 0; i < blacklistedFileTypes.length; i++) {
                var indexOfMatch = fileName.search(blacklistedFileTypes[i]);
                var expectedIndexOfMatch = fileName.length - blacklistedFileTypes[i].length;
                if ((indexOfMatch != -1) && (indexOfMatch == expectedIndexOfMatch)) {
                    alert('Denne filtypen er svartelistet. Du kan få en liste over tillatte filtyper av din redaktør eller administrator.');
                    return false;
                }
            }
        }

        var validateText = function validateText(string) {

            // This pattern ensures that the text consists of at least 3 characters
            var atLeastThree = /.{3}.*/;

            // This pattern ensures that there exists at least one character that is not whitespace
            var notAllWhitespace = /[^\s]+/;

            return atLeastThree.test(string) && notAllWhitespace.test(string);
        };


        var mediaNameRequired = false;
        if (mediaNameRequired) {
            var $mediaName = $(document.uploadForm.elements.name);
            var mediaNameText = $mediaName.val();
            if (!mediaNameText || !validateText(mediaNameText)) {
                alert('Vennligst fyll inn et gyldig navn på multimediafilen');
                $mediaName.focus();
                return false;
            }
        }

        var authorRequired = false;
        if (authorRequired) {
            var $author = $(document.uploadForm.elements.author);
            var authorText = $author.val();
            if (!authorText || !validateText(authorText)) {
                alert('Vennligst oppgi et gyldig navn på fotograf\/eier');
                $author.focus();
                return false;
            }
        }

        if ($("#MultimediaAddToArchive").is(":checked") && document.uploadForm.elements['parentId'].value == "") {
            alert('Vennligst velg en bildemappe å lagre filen i');
            return false;
        }


        $("#UploadFormButtons").hide();
        $("#UploadStatus").show();
        return true;
    }
    function displayButtons() {
        $(".uploadMetadata").show();
        $("#UploadFormButtons").show();
    }
    function toggleSelectMediaFolder() {
        $("ParentId").val('');
        $("ParentText").val('');
        if ($("#MultimediaAddToArchive").is(":checked")) {
            $("#MediaFolderContainer").show();
        } else {
            $("#MediaFolderContainer").hide();
        }
    }
    /**
     * Callback from popup where user select an image
     * @param url
     * @param text
     */
    /**alert('We got:'+id+' and '+name);
    openaksess.editcontext.insertValueAndNameIntoForm = function (id, name) {
        if (id == 0) {
            alert('You are not allowed to save files in the root folder. Please select another folder.');
        } else {
            var frm = document.uploadForm;
            frm.parentId.value = id;
            frm.parentName.value = name;
        }
     };**/

    $(document).ready(function() {
        $("#MultimediaAddToArchive").click(toggleSelectMediaFolder);
    });
</script>
<script type="text/javascript">
    if (typeof properties == 'undefined') {
        var properties = { };
    }
    properties.contextPath = '/kantega-intranett-webapp';
    properties.debug = true;
    properties.contentRequestHandler = 'content.ap';
    properties.thisId = 'thisId';
</script>
<script type="text/javascript">
    $(document).ready(function() {
        $("#Content .button .ok, #Content .button .insert").click(function(){
            var close = true;
            if (typeof buttonOkPressed == 'function') {
                close = buttonOkPressed();
            }
            if (close) {
                closeWindow();
            }
        });
        $("#Content .button .cancel").click(function(){
            openaksess.common.debug("popupLayout: close clicked");
            closeWindow();
        });
        var title = $("title").text();
        //Use the iframe page's title as modal window title if set.
        if (!window.opener && $.trim(title).length > 0 && typeof parent.openaksess != "undefined") {
            parent.openaksess.common.modalWindow.setTitle(title);
        }
    });

    function closeWindow() {
        if (window.opener) {
            window.close();
        } else {
            window.setTimeout(parent.openaksess.common.modalWindow.close,300);
        }
    }

    function getParent() {
        if (window.opener) {
            return window.opener;
        } else {
            return window.parent;
        }
    }
</script>
<div id="Content" class="popup">
    <div id="MultimediaUploadForm">
        <form action="UploadMultimedia.action" name="uploadForm" method="post" enctype="multipart/form-data">
            <input type="hidden" name="fileUploadedFromEditor" value="true">
            <div class="formElement">
                <div class="heading">
                    <label>Velg filen(e) som skal lastes opp</label>
                </div>
                <div class="inputs">
                    <input type="file" class="fullWidth" id="File" name="file" value="" size="45" onchange="displayButtons()">
                    <div class="uploadMetadata hidden">
                        <input type="checkbox" name="multimediaAddToArchive" id="MultimediaAddToArchive" value="true">
                        <label for="MultimediaAddToArchive">Legg til i mediaarkiv (dersom du skal gjenbruke bildet i andre sider)</label>


                    </div>
                </div>
            </div>

            <div id="MediaFolderContainer" class="formElement hidden">
                <div class="heading">
                    <label>Velg bildemappen du ønsker å lagre filen i</label>
                </div>
                <div class="inputs">
                    <input type="text" class="fullWidth" name="parentName" id="ParentName" value="" maxlength="128" readonly="readonly">
                    <input type="hidden" name="parentId" id="ParentId" value="">
                </div>
                <div class="buttonGroup">
                    <a href="Javascript:selectMultimedia()" class="button"><span class="choose">Velg</span></a>
                </div>
            </div>

            <div class="hidden uploadMetadata">
                <div class="formElement ">
                    <div class="heading">
                        <label>Navn på multimediafil</label>
                    </div>
                    <div class="inputs">
                        <input type="text" class="fullWidth" name="name" id="MultimediaName" value="" maxlength="255">
                    </div>
                </div>
                <div class="formElement">
                    <div class="heading">
                        <label>Alternativ tekst</label>
                    </div>
                    <div class="inputs">
                        <div class="ui-state-highlight">
                            Alternativ tekst vises istedet for bildet for brukere som har en nettleser som ikke viser bilder.
                        </div>
                        <input type="text" class="fullWidth" name="altname" id="MultimediaAltName" value="" maxlength="255">
                    </div>
                </div>
                <div class="formElement">
                    <div class="heading">
                        <label>Fotograf / opphavsrett</label>
                    </div>
                    <div class="inputs">
                        <input type="text" class="fullWidth" name="author" id="MultimediaAuthor" value="" maxlength="255">
                    </div>
                </div>
            </div>

            <div id="UploadFormButtons" class="buttonGroup hidden">
                <div class="ui-state-highlight hidden">
                    Etter at filene har blitt lastet opp får du muligheten til å endre informasjonen til hvert bilde
                </div>
                <span class="button"><input type="button" class="ok" value="Last opp"></span>
            </div>

            <div id="UploadStatus" class="ui-state-highlight">
                <span class="progress">Vennligst vent mens filen(e) lastes opp</span>
            </div>

            <!--input type="button" value="Click me!"></input-->
        </form>
    </div>
</div>
</body>
</html>


