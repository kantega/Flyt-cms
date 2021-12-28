<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<kantega:section id="title">
    <c:choose>
        <c:when test="${attachmentId != -1}"><kantega:label key="aksess.attachment.add"/></c:when>
        <c:otherwise><kantega:label key="aksess.attachment.update"/></c:otherwise>
    </c:choose>
</kantega:section>

<kantega:section id="head">
    <script type="text/javascript">
        var hasSubmitted = false;
        function buttonOkPressed() {
            if (!hasSubmitted) {
                <%-- Check if the file type is black-listed. If so, cancel the upload and display an error message --%>
                var fileName = document.myform.elements['attachment'].value;
                var blacklistedFileTypes = new Array();
                <c:forEach var="fileType" items="${blacklistedFileTypes}" varStatus="status">
                    blacklistedFileTypes[${status.index}] = ".${fileType}";
                </c:forEach>
                for (var i = 0; i < blacklistedFileTypes.length; i++) {
                    var indexOfMatch = fileName.search(blacklistedFileTypes[i]+"$");
                    if ((indexOfMatch != -1) ) {
                        alert('<kantega:label key="${blacklistedErrorMessage}" escapeJavascript="true"/>');
                        return false;
                    }
                }

                hasSubmitted = true;
                getParent().onbeforeunload = null;
                document.myform.submit();
            }
            return false;
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="AddAttachmentForm">
        <form name="myform" action="${pageContext.request.contextPath}/publish/popups/AddAttachment.action" method="post" enctype="multipart/form-data">
            <input type="hidden" name="insertlink" value="${insertlink}">
            <c:if test="${attachmentId != null}">
                <input type="hidden" name="attachmentId" value="${attachmentId}">
            </c:if>
            <div class="formElement">
                <div class="inputs">
                    <input type="file" size="20" name="attachment" value="" style="width:300px;">
                </div>
            </div>
            <div class="buttonGroup">
                <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </form>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
