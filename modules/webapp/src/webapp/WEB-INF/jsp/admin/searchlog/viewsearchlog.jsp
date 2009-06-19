<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
	<title>security/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">

    <form name="myform" action="index.jsp" method="get">
        <strong><kantega:label key="aksess.statistics.velgsite"/>:</strong>
        <select name="siteId" onchange="document.myform.submit()">
        <c:forEach items="${sites}" var="site">
            <option value="${site.id}" <c:if test="${site.id == selectedSiteId}"> selected</c:if>>${site.name}</option>
        </c:forEach>
        </select>
    </form>

    <table border="0" cellspacing="0" cellpadding="0" width="475">
        <tr class="tableHeading">
            <td colspan="2"><b><kantega:label key="aksess.statistics.summary.title"/></b></td>
        </tr>
        <tr class="tableRow0">
            <td><kantega:label key="aksess.searchlog.summary.now"/></td>
            <td align="right">${last30min}</td>
        </tr>
        <tr class="tableRow0">
            <td><kantega:label key="aksess.searchlog.summary.lastmonth"/></td>
            <td align="right">${sumAllTime}</td>
        </tr>


    </table>
    <p>&nbsp;</p>
    <table border="0" cellspacing="0" cellpadding="0" width="475">
        <tr class="tableHeading">
            <td width="25">&nbsp;</td>
            <td width="225"><b><kantega:label key="aksess.searchlog.query.mostpopular"/></b></td>
            <td width="150" align="right"><b><kantega:label key="aksess.searchlog.query.hits"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.searchlog.query.searches"/></b></td>
        </tr>
        <c:forEach items="${most}" var="q" varStatus="status">
            <tr  class="tableRow<c:out value="${status.index mod 2}"/>">
                <td><c:out value="${status.index + 1}"/></td>
                <td><c:out value="${q.query}"/></td>
                <td align="right"><fmt:formatNumber value="${q.numberOfHits}" maxFractionDigits="2"/></td>
                <td align="right"><c:out value="${q.numberOfSearches}"/></td>
            </tr>
        </c:forEach>
    </table>
    <p>&nbsp;</p>
    <table border="0" cellspacing="0" cellpadding="0" width="475">
        <tr class="tableHeading">
            <td width="25">&nbsp;</td>
            <td width="225"><b><kantega:label key="aksess.searchlog.query.leasthits"/></b></td>
            <td width="150" align="right"><b><kantega:label key="aksess.searchlog.query.hits"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.searchlog.query.searches"/></b></td>
        </tr>
        <c:forEach items="${least}" var="q" varStatus="status">
            <tr  class="tableRow<c:out value="${status.index mod 2}"/>">
                <td><c:out value="${status.index + 1}"/></td>
                <td><c:out value="${q.query}"/></td>
                <td align="right"><fmt:formatNumber value="${q.numberOfHits}" maxFractionDigits="2"/></td>
                <td align="right"><c:out value="${q.numberOfSearches}"/></td>
            </tr>
        </c:forEach>
    </table>

    <p>&nbsp;</p>

    <table border="0" cellspacing="0" cellpadding="0" width="475">
        <tr>
            <td>
                <div class=helpText><kantega:label key="aksess.searchlog.help"/></div>
            </td>
        </tr>
    </table>

</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>