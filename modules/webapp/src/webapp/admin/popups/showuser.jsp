<%@ page import="no.kantega.publishing.security.data.User"%>
<%@ page import="no.kantega.publishing.security.realm.SecurityRealm"%>
<%@ page import="no.kantega.publishing.security.realm.SecurityRealmFactory"%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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
    User user = new User();
    String userid = request.getParameter("userid");
    if (userid != null) {
        SecurityRealm realm = SecurityRealmFactory.getInstance();
        user = realm.lookupUser(userid);
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.userinfo.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.userinfo.title"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="2">
                    <tr>
                        <td><b><kantega:label key="aksess.userinfo.userid"/></b></td>
                        <td><%=userid%></td>
                    </tr>
                    <%
                        if (user != null) {
                    %>
                    <tr>
                        <td><b><kantega:label key="aksess.userinfo.name"/></b></td>
                        <td><%=user.getName()%></td>
                    </tr>
                    <%
                            if (user.getEmail() != null && user.getEmail().length() > 0) {
                    %>
                        <tr>
                            <td><b><kantega:label key="aksess.userinfo.email"/></b></td>
                            <td><a href="mailto:<%=user.getEmail()%>"><%=user.getEmail()%></a></td>
                        </tr>
                    <%
                            }
                    %>
                    <%
                            if (user.getDepartment() != null && user.getDepartment().length() > 0) {
                    %>
                        <tr>
                            <td><b><kantega:label key="aksess.userinfo.department"/></b></td>
                            <td><%=user.getDepartment()%></td>
                        </tr>
                    <%
                            }
                        }
                    %>
                </table>
            </td>
        </tr>
    </table><br>
    <a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>

</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
