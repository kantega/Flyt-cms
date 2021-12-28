<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.exception.SystemException"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty"%>
<%@ page import="no.kantega.publishing.security.data.User" %>
<%@ page import="no.kantega.publishing.security.realm.SecurityRealmFactory" %>
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
    Content   content   = (Content)request.getAttribute("content");

    String value = attribute.getValue();
    String name = null;

    if (value == null || value.length() == 0) {
        if (ContentProperty.OWNERPERSON.equalsIgnoreCase(attribute.getField())) {
            value = content.getOwnerPerson();
        }
    }

    if(value != null && !value.trim().equals("")) {
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
<div class="inputs">
    <input type="hidden" name="${fieldName}" id="${fieldName}" value="<%=value%>">
    <input type="text" name="${fieldName}text" id="${fieldName}text" value="<%= name != null && !name.equals("") ? name : value%>" maxlength="512" class="fullWidth" tabindex="<%=attribute.getTabIndex()%>">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#${fieldName}text").oaAutocomplete({
                source: "${pageContext.request.contextPath}/ajax/AutocompleteUsers.action",
                select: openaksess.editcontext.autocompleteInsertIntoFormCallback
            });
        });
    </script>
</div>
<div class="buttonGroup">
    <a href="#" onclick="openaksess.editcontext.selectUser(document.myform.${fieldName})" class="button" tabindex="<%=attribute.getTabIndex()+1%>"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
    <a href="#" onclick="openaksess.editcontext.removeValueAndNameFromForm(document.myform.${fieldName})" class="button" tabindex="<%=attribute.getTabIndex()+2%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
</div>
