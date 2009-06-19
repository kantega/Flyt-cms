<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.security.data.SecurityIdentifier,
                 no.kantega.publishing.security.data.User,
                 no.kantega.publishing.security.data.Role,
                 no.kantega.publishing.security.data.enums.RoleType,
                 java.util.ArrayList"%>
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
    RequestParameters param = new RequestParameters(request);
    String roletype = param.getString("roletype");
    String action   = param.getString("action");
    boolean select = param.getBoolean("select", false);
    String name = param.getString("name");
    boolean notFound = false;
    List securityIdentifiers;
    if (RoleType.USER.equalsIgnoreCase(roletype)) {
        if(name != null && name.length() > 0) {
            securityIdentifiers = securitySession.searchUsers(name);
            if (securityIdentifiers.size() == 0) {
                notFound = true;
            }
        } else {
            securityIdentifiers = new ArrayList();
        }
    } else {
        securityIdentifiers = securitySession.getAllRoles();
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>addroleoruser_list.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <script type="text/javascript">
        function selectUser(user, name) {
            if (window.parent.opener) {
                window.parent.opener.insertIdAndValueIntoForm(user, name);
            }
            window.parent.close();
        }
    </script>
</head>
<body onload="if (document.search) document.search.name.focus();">
     <%
         if (RoleType.USER.equalsIgnoreCase(roletype)) {
     %>
               <form name="search" action="addroleoruser_list.jsp" method="post">
                    <input type="hidden" name="action" value="<%=action%>">
                    <input type="hidden" name="select" value="<%=select%>">
                    <input type="hidden" name="roletype" value="<%=roletype%>">
                    <b><kantega:label key="aksess.editpermissions.search"/>:</b> <input type="text" name="name" value="<%=name == null ? "" : name%>" size="10" maxlength="30">
                    <a href="Javascript:document.search.submit()"><img src="../bitmaps/<%=skin%>/buttons/sok.gif" border="0" alt="Søk"></a>
                    <%
                        if (notFound) {
                    %>
                        <p><kantega:label key="aksess.editpermissions.search.notfound"/></p>
                    <%
                        }
                    %>
               </form>
     <%
         }
     %>
    <form name="roles" action="<%=action%>.action" method="post" target="_top">
    <input type="hidden" name="roletype" value="<%=roletype%>">
    <table border="0" width="370" cellspacing="0" cellpadding="0">
        <!-- Rettigheter -->
        <tr>
            <td width="20"></td>
            <td width="350"></td>
        </tr>
        <%
            if (securityIdentifiers != null) {
                for (int i = 0; i < securityIdentifiers.size(); i++) {
                    SecurityIdentifier s = (SecurityIdentifier)securityIdentifiers.get(i);
                    String displayName = s.getName();
                    if (s instanceof User) {
                        User u = (User)s;
                        String department = u.getDepartment();
                        if (department != null && department.length() > 0) {
                            displayName += " (" + department + ")";
                        }
                    }
        %>
                    <tr class="tableRow<%=(i%2)%>">
                        <%if(select) { %>
                            <td><input type="checkbox" name="role" value="<%=s.getId()%>"></td>
                            <% if (s instanceof User) { %>
                                <td title="<%=s.getId()%>"><%=displayName%></td>
                            <% } else {%>
                                <td><%=displayName%></td>
                            <% } %>
                        <% } else { %>
                            <td>&nbsp;</td>
                            <% if (s instanceof User) { %>
                                <td><a title="<%=s.getId()%>" href="javascript:selectUser('<%=s.getId()%>', '<%=displayName%>')"><%=displayName%></a></td>
                            <% } else {%>
                                <td><a href="javascript:selectUser('<%=s.getId()%>', '<%=displayName%>')"><%=displayName%></a></td>
                            <% } %>
                        <% } %>
                    </tr>
        <%
                }
            }
        %>
    </table>
    </form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
