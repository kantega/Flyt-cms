<%@ page import="no.kantega.publishing.common.data.attributes.RoleAttribute" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.security.data.Role" %>
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

<%--
  User: Kristian Lier Selnæs, Kantega AS
  Date: Jul 6, 2007
  Time: 10:28:56 AM
--%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%
    RoleAttribute attribute = (RoleAttribute) request.getAttribute("attribute");
    String fieldName = (String) request.getAttribute("fieldName");

    SecuritySession secSession = SecuritySession.getInstance(request);

    String roleId = "";
    String roleName = "";
    if(secSession.getRealm() != null && attribute.getValue() != null && attribute.getValue().trim().length() > 0) {
        Role role = secSession.getRealm().lookupRole(attribute.getValue());
        roleId = role.getId();
        roleName = role.getName();
    }

%>

<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:selectRole(document.myform.<%=fieldName%>)"><img src="../bitmaps/common/buttons/mini_legg_til.gif" border="0" alt=""></a></td>
                <td><a href="Javascript:selectRole(document.myform.<%=fieldName%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.leggtil"/></a></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0" alt=""></a></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><kantega:label key="aksess.button.slett"/></a></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="inpHeadingSpacer"><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <input type="hidden" name="<%=fieldName%>" value="<%=roleId%>">
        <input type="text" name="<%=fieldName%>text" value="<%=roleName%>" onFocus="this.select()" style="width: 600px;" tabindex="<%=attribute.getTabIndex()%>">
    </td>
</tr>