<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.ListAttribute" %>
<%@ page import="no.kantega.publishing.common.data.attributes.EditablelistAttribute" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%
    Content content = (Content)request.getAttribute("content");
    EditablelistAttribute attribute = (EditablelistAttribute) request.getAttribute("attribute");
    String fieldName = (String) request.getAttribute("fieldName");
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="inputs">
<%@include file="listoptions.jsf"%>
</div>
<%
    if (SecuritySession.getInstance(request).isUserInRole(attribute.getEditableBy())) {
%>
<div class="buttonGroup">
    <a href="Javascript:addListOption(document.myform.<%=fieldName%>, '<%=attribute.getKey()%>', <%=content.getLanguage()%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="add"><kantega:label key="aksess.button.leggtil"/></span></a>
    <a href="Javascript:removeOptionFromList(document.myform.<%=fieldName%>, '<%=attribute.getKey()%>', <%=content.getLanguage()%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
</div>
<%
    }
%>
