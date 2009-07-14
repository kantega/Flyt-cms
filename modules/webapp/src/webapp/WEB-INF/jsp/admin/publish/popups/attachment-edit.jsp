<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
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
        function saveForm() {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="AddAttachmentForm">
    <form name="myform" action="AddAttachment.action" method="post" enctype="multipart/form-data">
        <fieldset>
            <legend>
                <c:choose>
                    <c:when test="${attachmentId != -1}"><kantega:label key="aksess.attachment.add"/></c:when>
                    <c:otherwise><kantega:label key="aksess.attachment.update"/></c:otherwise>
                </c:choose>
            </legend>

            <div class="formElement">
                <div class="inputs">
                    <input type="file" size="20" name="attachment" value="" style="width:300px;">
                </div>
            </div>

        </fieldset>
        <div class="buttonGroup">
            <a href="Javascript:saveForm()" class="button ok"><span><kantega:label key="aksess.button.ok"/><span></a>
            <a href="Javascript:window.close()" class="button cancel"><span><kantega:label key="aksess.button.avbryt"/><span></a>
        </div>
    </form>
   </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>