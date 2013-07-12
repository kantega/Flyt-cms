<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.admin.content.util.AttachmentBlacklistHelper,
                 no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeProperty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
<%
    Attribute attribute = (Attribute)request.getAttribute("attribute");
    String value = attribute.getValue();
    pageContext.setAttribute("blacklistedFileTypes", AttachmentBlacklistHelper.getBlacklistedFileTypes());
    pageContext.setAttribute("blacklistedErrorMessage", AttachmentBlacklistHelper.getErrorMessage());
%>
<c:if test="${not empty blacklistedFileTypes}">
    <script type="text/javascript">
        var blacklistedFileTypes = new Array();
        <c:forEach var="fileType" items="${blacklistedFileTypes}" varStatus="status">
        blacklistedFileTypes[${status.index}] = ".${fileType}";
        </c:forEach>
        function validateFileAgainstBlackList_${fieldName}(field) {
            var fileName = field.value;
            for (i = 0; i < blacklistedFileTypes.length; i++) {
                var indexOfMatch = fileName.search(blacklistedFileTypes[i]);
                var expectedIndexOfMatch = fileName.length - blacklistedFileTypes[i].length;
                if ((indexOfMatch != -1) && (indexOfMatch == expectedIndexOfMatch)) {
                    alert('<kantega:label key="${blacklistedErrorMessage}" escapeJavascript="true"/>');
                    return false;
                }
            }
        }
    </script>
</c:if>

<div class="inputs">
    <input type="file" class="fullWidth" name="${fieldName}" id="${fieldName}" value="<%=value%>" size="60" tabindex="${attribute.tabIndex}" onchange="validateFileAgainstBlackList_${fieldName}(this)">
    <input type="hidden" name="delete_${fieldName}" value="0">
    <% if (value != null && value.length() > 0) {%>
    <br><a href="<%=attribute.getProperty(AttributeProperty.URL)%>" class="textlink" target="_new"><%=attribute.getProperty(AttributeProperty.NAME)%></a>
    <%}%>
</div>
<% if (value != null && value.length() > 0) {%>
<div class="buttonGroup">
    <a href="#" onclick="openaksess.editcontext.removeAttachment(document.myform.${fieldName})" class="button"><span class="delete"><kantega:label key="aksess.button.delete"/></span></a>
</div>
<%}%>