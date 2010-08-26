<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.data.attributes.Attribute,
                 no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty"%>
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
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();
    value = StringHelper.escapeQuotes(value);

    if (ContentProperty.TITLE.equalsIgnoreCase(attribute.getField()) && !content.isNew()) {
        value = content.getTitle();
    }
%>
<div class="inputs">
    <input type="email" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField()" name="<%=fieldName%>" id="<%=fieldName%>" value="<%=value%>" tabindex="<%=attribute.getTabIndex()%>"
</div>