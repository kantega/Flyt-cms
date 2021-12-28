<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.EditablelistAttribute" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%
    EditablelistAttribute attribute = (EditablelistAttribute) request.getAttribute("attribute");
    request.setAttribute("alwaysUseSelectList", Boolean.TRUE);
%>
<div class="inputs">
<%@include file="listoptions.jsf"%>
</div>
<%
    request.removeAttribute("alwaysUseSelectList");

    if (SecuritySession.getInstance(request).isUserInRole(attribute.getEditableBy())) {
%>
<div class="buttonGroup">
    <a href="#" onclick="openaksess.editcontext.addListOption(document.myform.${fieldName}, '${attribute.key}', ${content.language})" class="button" tabindex="${attribute.tabIndex}"><span class="add"><kantega:label key="aksess.button.add"/></span></a>
    <a href="#" onclick="openaksess.editcontext.removeOptionFromList(document.myform.${fieldName}, '${attribute.key}', ${content.language})" class="button" tabindex="${attribute.tabIndex + 1}"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
</div>
<%
    }
%>
