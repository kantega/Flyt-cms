<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="java.net.URLEncoder"%>
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

<%
    List mailSubscriptions = (List)request.getAttribute("subscriptions");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>mailsubscription/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <%
            if (mailSubscriptions != null) {
        %>
        <tr class="tableHeading">
            <td><strong><kantega:label key="aksess.mailsubscription.email"/></strong></td>
            <td>&nbsp;</td>
        </tr>
        <%
            for (int i = 0; i < mailSubscriptions.size(); i++) {
                String email = (String)mailSubscriptions.get(i);
                request.setAttribute("email", email);
                String emailEnc = URLEncoder.encode(email, "iso-8859-1");
        %>
            <tr class="tableRow<%=(i%2)%>">
                <td><a href="mailto:<c:out value="${email}"/>"><c:out value="${email}" escapeXml="true"/></a></td>
                <td align="right">
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td><a href="ViewMailSubscribers.action?delete=<%=emailEnc%>"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                            <td><a href="ViewMailSubscribers.action?delete=<%=emailEnc%>" class="button"><kantega:label key="aksess.mailsubscription.slett"/></a></td>
                        </tr>
                    </table>
                </td>
            </tr>
        <%
                }
            }
        %>
    </table>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>