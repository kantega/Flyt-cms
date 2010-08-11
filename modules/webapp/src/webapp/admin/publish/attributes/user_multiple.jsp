<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier,
                 no.kantega.publishing.common.data.enums.Language,
                 no.kantega.publishing.common.data.Content,
                 no.kantega.publishing.common.data.attributes.Attribute,
                 no.kantega.publishing.common.service.ContentManagementService,
                 no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.org.OrganizationManager"%>
<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="no.kantega.publishing.security.realm.SecurityRealmFactory"%>
<%@ page import="no.kantega.publishing.security.data.User"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
    Attribute attribute = (Attribute)request.getAttribute("attribute");
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();

%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:selectUser(document.myform.<%=fieldName%>)"><img src="<%=request.getContextPath()%>/admin/bitmaps/common/buttons/mini_legg_til.gif" border="0"></a></td>
                <td><a href="Javascript:selectUser(document.myform.<%=fieldName%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.leggtil"/></a></td>
                <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)"><img src="<%=request.getContextPath()%>/admin/bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><kantega:label key="aksess.button.slett"/></a></td>
                <c:if test="${attribute.moveable}">
                    <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif"></td>
                    <td><a href="Javascript:moveId(document.myform.<%=fieldName%>, -1)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.flyttopp"/></a></td>
                    <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif"></td>
                    <td><a href="Javascript:moveId(document.myform.<%=fieldName%>, 1)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><kantega:label key="aksess.button.flyttned"/></a></td>
                </c:if>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="inpHeadingSpacer"><img src="<%=request.getContextPath()%>/admin/bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
        <select name="<%=fieldName%>list" class="inp" style="width:600px;" size="6">
        <%
            if (value != null && value.length() > 0) {
                String[] ids = value.split(",");

                for (int i = 0; i < ids.length; i++) {
                    String name = ids[i];
                    User user = SecurityRealmFactory.getInstance().lookupUser(ids[i]);

                    if(user != null) {
                        name = user.getName();
                        String department = user.getDepartment();
                        if (department != null && department.length() > 0) {
                            name += " (" + department + ")";
                        }                        
                    }
                    out.write("<option value=\"" + ids[i] + "\">" + name + "</option>");
                }
            }
        %>
        </select>
    </td>
</tr>