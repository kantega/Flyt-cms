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
	<title><kantega:label key="aksess.displaytemplates.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <c:if test="${!empty templates}">
        <table border="0" cellspacing="0" cellpadding="0" width="600">
            <tr class="tableHeading">
                <td><kantega:label key="aksess.templates.id"/></td>
                <td><kantega:label key="aksess.templates.template"/></td>
                <td><kantega:label key="aksess.templates.view"/></td>
                <td><kantega:label key="aksess.templates.publicid"/></td>
                <td>&nbsp;</td>
            </tr>
            <c:forEach var="template" items="${templates}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td>${template.id}</td>
                    <td>${template.name}</td>
                    <td>${template.view}</td>
                    <td>${template.publicId}</td>
                    <td align="right">
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td><a href="ListDisplayTemplateUsages.action?templateId=${template.id}"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0" alt="<kantega:label key="aksess.button.vis"/>"></a></td>
                                <td><a href="ListDisplayTemplateUsages.action?templateId=${template.id}" class="button"><kantega:label key="aksess.button.vis"/></a></td>
                            </tr>
                        </table>
                    </td>
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