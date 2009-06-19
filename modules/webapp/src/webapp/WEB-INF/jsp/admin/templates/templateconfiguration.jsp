<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
    <title><kantega:label key="aksess.templateconfig.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>

<body class="bodyWithMargin">
<%
    TemplateConfiguration tc = (TemplateConfiguration)request.getAttribute("templateConfiguration");
%>

<p><kantega:label key="aksess.templateconfig.info"/></p>

<table border="0" cellspacing="0" cellpadding="0" width="600">
    <tr class="tableHeading">
        <td>&nbsp;</td>
        <td><kantega:label key="aksess.templateconfig.items"/></td>
        <td>&nbsp;</td>
    </tr>
    <tr class="tableRow0">
        <td><kantega:label key="aksess.associationcategories.title"/></td>
        <td><%=tc.getAssociationCategories().size()%></td>
        <td align="right">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><a href="ListAssociationCategories.action"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0"></a></td>
                    <td><a href="ListAssociationCategories.action" class="button"><kantega:label key="aksess.button.vis"/></a></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="tableRow1">
        <td><kantega:label key="aksess.contenttemplates.title"/></td>
        <td><%=(tc.getContentTemplates().size() + tc.getMetadataTemplates().size()) %></td>
        <td align="right">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><a href="ListContentTemplates.action"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0"></a></td>
                    <td><a href="ListContentTemplates.action" class="button"><kantega:label key="aksess.button.vis"/></a></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="tableRow0">
        <td><kantega:label key="aksess.displaytemplates.title"/></td>
        <td><%=tc.getDisplayTemplates().size()%></td>
        <td align="right">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><a href="ListDisplayTemplates.action"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0"></a></td>
                    <td><a href="ListDisplayTemplates.action" class="button"><kantega:label key="aksess.button.vis"/></a></td>
                </tr>
            </table>
        </td>
    </tr>
</table>


<form action="ReloadTemplateConfiguration.action" method="post">
    <input type="submit" name="submit" value="<kantega:label key="aksess.templateconfig.reload"/>">
</form>

<c:if test="${not empty errors}">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td>
                <div class=helpText><kantega:label key="aksess.templateconfig.error"/></div>
            </td>
        </tr>
    </table><br>

    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><kantega:label key="aksess.templateconfig.error.object"/></td>
            <td><kantega:label key="aksess.templateconfig.error.message"/></td>
        </tr>
        <c:forEach var="errorMessage" items="${errors}" varStatus="status">
            <tr class="tableRow${status.index mod 2}">
                    <td>${errorMessage.object}</td>
                <td><kantega:label key="${errorMessage.message}"/>: ${errorMessage.data}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>