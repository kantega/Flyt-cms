<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License
  --%>

<kantega:section id="head">
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/editcontext.js"/>"></script>
    <link rel="stylesheet" href="<kantega:expireurl url="/admin/css/multimedia.css"/>">
</kantega:section>

<kantega:section id="body">

    <script type="text/javascript">
        function selectMultimedia() {
            openaksess.editcontext.doInsertTag = false;
            openaksess.editcontext.doInsertUrl = true;
            var mmwin = window.open("${pageContext.request.contextPath}/admin/publish/popups/SelectMediaFolder.action", "openAksessPopup", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
            mmwin.focus();
        }
        function buttonOkPressed() {
            if (validateUpload()) {
                document.uploadForm.submit();
            }
            return false;
        }
        function validateUpload() {
            if (document.uploadForm.elements['file'].value == "") {
                alert('<kantega:label key="aksess.multimedia.uploadfile.missing" escapeJavascript="true"/>');
                return false;
            } else {
                <%-- Check if the file type is black-listed. If so, cancel the upload and display an error message --%>
                var fileName = document.uploadForm.elements['file'].value;
                var blacklistedFileTypes = new Array();
                <c:forEach var="fileType" items="${blacklistedFileTypes}" varStatus="status">
                blacklistedFileTypes[${status.index}] = ".${fileType}";
                </c:forEach>
                for (i = 0; i < blacklistedFileTypes.length; i++) {
                    var indexOfMatch = fileName.search(blacklistedFileTypes[i]);
                    var expectedIndexOfMatch = fileName.length - blacklistedFileTypes[i].length;
                    if ((indexOfMatch != -1) && (indexOfMatch == expectedIndexOfMatch)) {
                        alert('<kantega:label key="${blacklistedErrorMessage}" escapeJavascript="true"/>');
                        return false;
                    }
                }
            }

            var validateText = function validateText(string) {

                // This pattern ensures that the text consists of at least 5 characters
                var atLeastFive = /.{5}.*/;

                // This pattern ensures that there exists at least one character that is not whitespace
                var notAllWhitespace = /[^\s]+/;

                return atLeastFive.test(string) && notAllWhitespace.test(string);
            };

            <c:if test="${id == -1}">
            var mediaNameRequired = ${mediaNameRequired};
            if (mediaNameRequired) {
                var mediaName = document.uploadForm.elements.name.value;
                if (!mediaName || !validateText(mediaName)) {
                    alert('<kantega:label key="aksess.multimedia.mediaName.missing" escapeJavascript="true"/>');
                    return false;
                }
            }
            <c:if test="${altNameRequired}">
            var altName = document.uploadForm.elements.altname.value;
            if (!altName || !validateText(altName)) {
                alert('<kantega:label key="aksess.multimedia.altname.missing" escapeJavascript="true"/>');
                return false;
            }
            </c:if>
            var authorRequired = ${authorRequired};
            if (authorRequired) {
                var author = document.uploadForm.elements.name.value;
                if (!author || !validateText(author)) {
                    alert('<kantega:label key="aksess.multimedia.author.missing" escapeJavascript="true"/>');
                    return false;
                }
            }
            <c:if test="${fileUploadedFromEditor}">
            if ($("#MultimediaAddToArchive").is(":checked") && document.uploadForm.elements['parentId'].value == "") {
                alert('<kantega:label key="aksess.multimedia.selectfolder.missing" escapeJavascript="true"/>');
                return false;
            }
            </c:if>
            </c:if>
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
        openaksess.editcontext.insertValueAndNameIntoForm = function (id, name) {
            if (id == 0) {
                alert('<kantega:label key="aksess.multimedia.selectfolder.root.forbidden" escapeJavascript="true"/>');
            } else {
                var frm = document.uploadForm;
                frm.parentId.value = id;
                frm.parentName.value = name;
            }
        };

        $(document).ready(function() {
            $("#MultimediaAddToArchive").click(toggleSelectMediaFolder);
        });

    </script>
    <div id="MultimediaUploadForm">
        <form action="UploadMultimedia.action" name="uploadForm" <c:if test="${!fileUploadedFromEditor}">target="_parent"</c:if> method="post" enctype="multipart/form-data">
            <c:if test="${id != -1}">
                <input type="hidden" name="id" value="${id}">
            </c:if>

            <input type="hidden" name="fileUploadedFromEditor" value="${fileUploadedFromEditor}">

            <div class="formElement">
                <div class="heading">
                    <label><kantega:label key="aksess.multimedia.uploadfile"/></label>
                </div>
                <div class="inputs">
                    <input type="file" class="fullWidth" id="File" name="file" value="" size="45" onchange="displayButtons()" <c:if test="${id == -1 && !fileUploadedFromEditor}"> multiple </c:if>>
                    <c:if test="${allowPreserveImageSize}">
                        <div>
                            <input type="checkbox" id="PreserveImageSize" name="preserveImageSize" value="true"><label for="PreserveImageSize"><kantega:label key="aksess.multimedia.preserveimagesize"/></label>
                        </div>
                    </c:if>
                    <c:if test="${id == -1 && fileUploadedFromEditor}">
                        <div class="uploadMetadata hidden">
                            <input type="checkbox" name="multimediaAddToArchive" id="MultimediaAddToArchive" value="true"><label for="MultimediaAddToArchive"><kantega:label key="aksess.multimedia.addtoarchive"/></label>
                        </div>
                    </c:if>
                </div>
            </div>

            <c:if test="${id == -1}">
                <c:choose>
                    <c:when test="${fileUploadedFromEditor}">
                        <div id="MediaFolderContainer" class="formElement hidden">
                            <div class="heading">
                                <label><kantega:label key="aksess.multimedia.selectfolder"/></label>
                            </div>
                            <div class="inputs">
                                <input type="text" class="fullWidth" name="parentName" id="ParentName" value="" maxlength="128" readonly="readonly">
                                <input type="hidden" name="parentId" id="ParentId" value="">
                            </div>
                            <div class="buttonGroup">
                                <a href="Javascript:selectMultimedia()" class="button"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="parentId" value="${parentId}">
                    </c:otherwise>
                </c:choose>
                <div class="hidden uploadMetadata">
                    <div class="formElement ">
                        <div class="heading">
                            <label><kantega:label key="aksess.multimedia.medianame"/></label>
                        </div>
                        <div class="inputs">
                            <input type="text" class="fullWidth" name="name" id="MultimediaName" value="" maxlength="255">
                        </div>
                    </div>
                    <div class="formElement">
                        <div class="heading">
                            <label><kantega:label key="aksess.multimedia.altname"/></label>
                        </div>
                        <div class="inputs">
                            <input type="text" class="fullWidth" name="altname" id="MultimediaAltName" value="" maxlength="255">
                            <div class="ui-state-highlight">
                                <kantega:label key="aksess.multimedia.altinfo"/>
                            </div>
                        </div>
                    </div>
                    <div class="formElement">
                        <div class="heading">
                            <label><kantega:label key="aksess.multimedia.author"/></label>
                        </div>
                        <div class="inputs">
                            <input type="text" class="fullWidth" name="author" id="MultimediaAuthor" value="" maxlength="255">
                        </div>
                    </div>
                </div>
            </c:if>

            <div id="UploadFormButtons" class="buttonGroup hidden">
                <div class="ui-state-highlight <c:if test="${fileUploadedFromEditor}">hidden</c:if>">
                    <kantega:label key="aksess.multimedia.uploadfile.label"/>
                </div>
                <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.upload"/>"></span>
            </div>

            <div id="UploadStatus" class="ui-state-highlight">
                <span class="progress"><kantega:label key="aksess.multimedia.uploadfile.inprogress"/></span>
            </div>
        </form>
    </div>
</kantega:section>

<%@ include file="../layout/popupLayout.jsp" %>