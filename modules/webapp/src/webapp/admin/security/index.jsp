<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.security.service.SecurityService,
                 no.kantega.publishing.security.data.ObjectPermissionsOverview,
                 no.kantega.publishing.security.data.Permission"%>
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
    List permissionsOverview = null;

    RequestParameters param = new RequestParameters(request, "utf-8");
    int objectType = param.getInt("objectType");
    if (objectType != -1) {
        permissionsOverview = SecurityService.getPermissionsOverview(objectType);
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>security/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr>
            <td colspan="3">
                <form name="myform" action="index.jsp" method="get">
                <b><kantega:label key="aksess.viewpermissions.velgtype"/>:</b>
                <select name="objectType" onchange="document.myform.submit()">
                    <option value="-1"></option>
                    <option value="<%=ObjectType.ASSOCIATION%>" <% if (objectType == ObjectType.ASSOCIATION) out.write("selected");%>><kantega:label key="aksess.objecttype.content"/></option>
                    <option value="<%=ObjectType.MULTIMEDIA%>" <% if (objectType == ObjectType.MULTIMEDIA) out.write("selected");%>><kantega:label key="aksess.objecttype.multimedia"/></option>
                    <option value="<%=ObjectType.TOPICMAP%>" <% if (objectType == ObjectType.TOPICMAP) out.write("selected");%>><kantega:label key="aksess.objecttype.topicmap"/></option>
                </select>
                </form>
            </td>
        </tr>
        <%
            if (permissionsOverview != null) {
        %>
        <tr class="tableHeading">
            <td><b><kantega:label key="aksess.viewpermissions.objekt"/></b></td>
            <td><b><kantega:label key="aksess.viewpermissions.rolle"/></b></td>
            <td><b><kantega:label key="aksess.viewpermissions.rettighet"/></b></td>
        </tr>
        <%
                for (int i = 0; i < permissionsOverview.size(); i++) {
                    ObjectPermissionsOverview opo = (ObjectPermissionsOverview)permissionsOverview.get(i);
                    List permissions = opo.getPermissions();
        %>
                    <tr class="tableRow<%=(i%2)%>">
                        <td colspan="3"><b><%=opo.getName()%></b></td>
                    </tr>
        <%
                    for (int j = 0; j < permissions.size(); j++) {
                        Permission p = (Permission)permissions.get(j);
                        String role = p.getSecurityIdentifier().getId();
                        String key = "aksess.editpermissions.priv" + p.getPrivilege();
        %>
                        <tr class="tableRow<%=(i%2)%>">
                            <td>&nbsp;</td>
                            <td><%=role%></td>
                            <td><kantega:label key="<%=key%>"/></td>
                        </tr>
        <%
                    }
                }
            }
        %>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>