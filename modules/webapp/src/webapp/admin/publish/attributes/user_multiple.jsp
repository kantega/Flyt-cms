<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
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
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
    <select name="<%=fieldName%>list" class="fullWidth" size="6">
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
</div>
<div class="buttonGroup">
    <a href="Javascript:openaksess.editcontext.selectUser(document.myform.<%=fieldName%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="add"><kantega:label key="aksess.button.add"/></span></a>
    <a href="Javascript:openaksess.editcontext.removeValueAndNameFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><span class="delete"><kantega:label key="aksess.button.delete"/></span></a>
    <c:if test="${attribute.moveable}">
        <a href="Javascript:openaksess.editcontext.moveId(document.myform.<%=fieldName%>, -1)" class="button moveUp" tabindex="<%=attribute.getTabIndex()%>"><span><kantega:label key="aksess.button.moveup"/></span></a>
        <a href="Javascript:openaksess.editcontext.moveId(document.myform.<%=fieldName%>, 1)" class="button moveDown" tabindex="<%=(attribute.getTabIndex()+1)%>"><span><kantega:label key="aksess.button.movedown"/></span></a>
    </c:if>
</div>
