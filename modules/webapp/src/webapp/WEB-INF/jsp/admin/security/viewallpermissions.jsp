<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.publishing.common.data.EventLogEntry" %>
<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
<%@ page import="no.kantega.publishing.common.data.enums.Event" %>
<%@ page import="no.kantega.publishing.security.service.SecurityService" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.security.data.ObjectPermissionsOverview" %>
<%@ page import="no.kantega.publishing.security.data.Permission" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.viewpermissions.title"/>
</kantega:section>

<kantega:section id="content">
    <form name="permissions" action="" method="post">
        <div class="fieldset">
            <fieldset>
                <h1><kantega:label key="aksess.viewpermissions.title"/></h1>
                <strong><kantega:label key="aksess.viewpermissions.selecttype"/>:</strong>
                <select name="objectType" onchange="document.permissions.submit()">
                    <option value="-1"></option>
                    <option value="<%=ObjectType.ASSOCIATION%>" ${associationSelected}><kantega:label key="aksess.objecttype.content"/></option>
                    <option value="<%=ObjectType.MULTIMEDIA%>" ${multimediaSelected} ><kantega:label key="aksess.objecttype.multimedia"/></option>
                    <option value="<%=ObjectType.TOPICMAP%> ${topicMapSelected}"><kantega:label key="aksess.objecttype.topicmap"/></option>
                </select>

                <table>
                    <%
                        List permissionsOverview = (List)request.getAttribute("permissionsOverview");
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
                                    <td colspan="3"><strong><%=opo.getName()%></strong></td>
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
            </fieldset>
        </div>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
