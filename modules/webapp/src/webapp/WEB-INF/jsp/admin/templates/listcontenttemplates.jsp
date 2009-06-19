<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title><kantega:label key="aksess.contenttemplates.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>

<script language="Javascript" type="text/javascript">
    function showType(field) {
        location = "ListContentTemplates.action?type=" + field.options[field.selectedIndex].value;
    }
</script>
<body class="bodyWithMargin">
<kantega:label key="aksess.text.vis"/>:
<select name="type" onchange="showType(this)">
    <option value="<%=AttributeDataType.CONTENT_DATA%>" <c:if test="${isContentTemplates}">selected="selected"</c:if>><kantega:label key="aksess.contenttemplates.content"/></option>
    <option value="<%=AttributeDataType.META_DATA%>" <c:if test="${!isContentTemplates}">selected="selected"</c:if>><kantega:label key="aksess.contenttemplates.metadata"/></option>
</select><br>

<c:if test="${!empty templates}">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><kantega:label key="aksess.templates.id"/></td>
            <td><kantega:label key="aksess.templates.template"/></td>
            <td><kantega:label key="aksess.templates.templatefile"/></td>
            <td><kantega:label key="aksess.templates.publicid"/></td>
        </tr>
        <c:forEach var="template" items="${templates}" varStatus="status">
            <tr class="tableRow${status.index mod 2}">
                <td>${template.id}</td>
                <td>${template.name}</td>
                <td>${template.templateFile}</td>
                <td>${template.publicId}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>
<c:if test="${empty templates}">
    <kantega:label key="aksess.templates.notemplates"/>
</c:if>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>