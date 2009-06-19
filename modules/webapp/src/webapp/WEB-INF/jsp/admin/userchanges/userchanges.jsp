<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.security.service.SecurityService,
                 no.kantega.publishing.security.data.ObjectPermissionsOverview,
                 no.kantega.publishing.security.data.Permission"%>
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
	<title>security/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr>
            <td colspan="2">
                <form name="myform" action="index.jsp" method="get">
                <b><kantega:label key="aksess.userchanges.month"/>:</b>
                <select name="months" onchange="document.myform.submit()">
                    <option value="1" <c:if test="${month == 1}">selected</c:if>>1</option>
                    <option value="3" <c:if test="${month == 3}">selected</c:if>>3</option>
                    <option value="6" <c:if test="${month == 6}">selected</c:if>>6</option>
                    <option value="12" <c:if test="${month == 12}">selected</c:if>>12</option>
                </select>
                </form>
            </td>
        </tr>
        <%
            List userchanges = (List)request.getAttribute("userChanges");
            if (userchanges != null) {
                int total = 0;
        %>
        <tr class="tableHeading">
            <td><strong><kantega:label key="aksess.userchanges.username"/></strong></td>
            <td align="right"><strong><kantega:label key="aksess.userchanges.changes"/></strong></td>
        </tr>
        <%
            int i = 0;
            for (i = 0; i < userchanges.size(); i++) {
                UserContentChanges ucc = (UserContentChanges)userchanges.get(i);
                total += ucc.getNoChanges();
        %>
            <tr class="tableRow<%=(i%2)%>">
                <td><a href="ListUserChanges.action?username=<%=ucc.getUserName()%>"><%=ucc.getUserName()%></a></td>
                <td align="right"><%=ucc.getNoChanges()%></td>
            </tr>
        <%

            }
        %>
        <tr class="tableRow<%=(i%2)%>">
            <td><strong><kantega:label key="aksess.userchanges.total"/></strong></td>
            <td align="right"><strong><%=total%></strong></td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.userchanges.help"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <%
            }

        %>
    </table>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>