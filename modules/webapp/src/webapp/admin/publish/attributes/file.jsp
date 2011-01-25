<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute,
                 no.kantega.publishing.common.data.enums.AttributeProperty"%>
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
    String value = attribute.getValue();
%>
<div class="inputs">
    <input type="file" class="fullWidth" name="${fieldName}" id="${fieldName}" value="<%=value%>" size="60" tabindex="${attribute.tabIndex}">
    <input type="hidden" name="delete_${fieldName}" value="0">
</div>
<% if (value != null && value.length() > 0) {%>
<div class="buttonGroup">
    <a href="<%=attribute.getProperty(AttributeProperty.URL)%>" target="_new" class="button"><span class="show"><kantega:label key="aksess.button.showfile"/></span></a>
    <a href="#" onclick="openaksess.editcontext.removeAttachment(document.myform.${fieldName})" class="button"><span class="delete"><kantega:label key="aksess.button.delete"/></span></a>
</div>
<%}%>