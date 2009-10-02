<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer" %>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<c:set var="contentActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.edit.title"/>
</kantega:section>

<kantega:section id="content">
<%
    InputScreenRenderer screen = new InputScreenRenderer(pageContext, (Content)session.getAttribute("currentContent"), AttributeDataType.CONTENT_DATA);
%>
<script language="Javascript" type="text/javascript">
    var hasSubmitted = false;

    function initialize() {
    <%
        screen.generatePreJavascript();
    %>
        try {
            document.myform.elements[0].focus();
        } catch (e) {
            // Invisble field, can't get focus
        }
    }

    function saveContent(status) {
    <%
        screen.generatePostJavascript();
    %>
        if (validatePublishProperties()) {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.status.value = status;
                document.myform.submit();
            }
        }
    }

    $(document).ready(function() {
        initialize();
    });
</script>
    <%@ include file="../../../../admin/include/infobox.jsf" %>
    <%
        screen.generateInputScreen();
    %>
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="${currentContent.id}">
    <input type="hidden" name="isModified" value="${currentContent.modified}">
</kantega:section>
<%@ include file="../layout/publishLayout.jsp" %>