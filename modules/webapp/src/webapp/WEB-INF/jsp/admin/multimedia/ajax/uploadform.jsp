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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>

<script type="text/javascript">
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

        <c:if test="${id == -1}">
            <c:if test="${altNameRequired}">
                if (document.uploadForm.elements['altname'].value == "") {
                    alert('<kantega:label key="aksess.multimedia.altname.missing" escapeJavascript="true"/>');
                    return false;
                }
            </c:if>
        </c:if>

        $("#UploadFormButtons").hide();
        $("#UploadStatus").show();

        return true;
    }

    function displayMetadata() {
        $("#UploadMetadata").show();
        $("#UploadFormButtons").show();
    }
</script>
<div id="MultimediaUploadForm">
    <form action="UploadMultimedia.action" name="uploadForm" method="post" enctype="multipart/form-data" onsubmit="return validateUpload()">
        <c:if test="${id != -1}">
            <input type="hidden" name="id" value="${id}">
        </c:if>

        <div class="formElement">
            <div class="heading">
                <label><kantega:label key="aksess.multimedia.uploadfile"/></label>
            </div>
            <div class="inputs">
                <input type="file" class="fullWidth" id="File" name="file" value="" size="45" onchange="displayMetadata()" <c:if test="${id == -1}">multiple</c:if>>
                <c:if test="${allowPreserveImageSize}"><br>
                    <input type="checkbox" id="PreserveImageSize" name="preserveImageSize" value="true"><label for="PreserveImageSize"><kantega:label key="aksess.multimedia.preserveimagesize"/></label>
                </c:if>
            </div>
        </div>


        <c:if test="${id == -1}">
            <input type="hidden" name="parentId" value="${parentId}">
            <div id="UploadMetadata" class="hidden">
                <div class="formElement">
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
            <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.upload"/>"></span>
        </div>

        <div id="UploadStatus" class="ui-state-highlight">
            <span class="progress"><kantega:label key="aksess.multimedia.uploadfile.inprogress"/></span>
        </div>
    </form>
</div>