<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="no.kantega.publishing.security.realm.SecurityRealmFactory"%>
<%@ page import="no.kantega.publishing.security.data.User"%>
<%@ page import="no.kantega.commons.exception.SystemException"%>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
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
    Content   content   = (Content)request.getAttribute("content");

    String value = attribute.getValue();
    String name = null;

    if (value == null || value.length() == 0) {
        if (ContentProperty.OWNERPERSON.equalsIgnoreCase(attribute.getField())) {
            value = content.getOwnerPerson();
        }
    }

    if (value != null && !value.trim().equals("")) {
        try {
            User user = SecurityRealmFactory.getInstance().lookupUser(value);

            if(user != null) {
                name = user.getName();
                String department = user.getDepartment();
                if (department != null && department.length() > 0) {
                    name += " (" + department + ")";
                }
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }



%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif" alt=""></td>
                <td><a href="Javascript:selectUser(document.myform.<%=fieldName%>)"><img src="<%=request.getContextPath()%>/admin/bitmaps/common/buttons/mini_velg.gif" border="0"></a></td>
                <td><a href="Javascript:selectUser(document.myform.<%=fieldName%>)" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif" alt=""></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)"><img src="<%=request.getContextPath()%>/admin/bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button"><kantega:label key="aksess.button.slett"/></a></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="inpHeadingSpacer"><img src="<%=request.getContextPath()%>/admin/bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <input type="hidden" name="<%=fieldName%>" id="<%=fieldName%>" value="<%=value%>">
        <input type="text" name="<%=fieldName%>text" id="<%=fieldName%>text" value="<%= name != null && !name.equals("") ? name : value%>" maxlength="512" style="width:600px;" tabindex="<%=attribute.getTabIndex()%>">
        <script type="text/javascript">
            Autocomplete.setup({'inputField' :'<%=fieldName%>', url:'../../ajax/SearchUsersAsXML.action', 'minChars' :3 });
        </script>
    </td>
</tr>