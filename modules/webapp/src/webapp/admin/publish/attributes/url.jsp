<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.commons.configuration.Configuration"%>
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
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonGroup">
    <a href="Javascript:selectContentUrl(document.myform.<%=fieldName%>)" class="button select"><kantega:label key="aksess.button.velg"/></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete"><kantega:label key="aksess.button.slett"/></a>
</div>
<div class="inputs">
    <input type="text" class="fullWidth" onFocus="setFocusField(this)" onBlur="blurField()" name="<%=fieldName%>" value="<%=value%>" maxlength="512" tabindex="<%=attribute.getTabIndex()%>">

<%
    if (!Aksess.doOpenLinksInNewWindow() && ContentProperty.URL.equalsIgnoreCase(attribute.getField())) {
        // Let user choose if link should open in new window
        Configuration c = Aksess.getConfiguration();
        if (c.getBoolean("openinnewwindow.disabled", true)) {
%>
        <br><input type="checkbox" name="<%=fieldName%>_newwindow" value="true" <% if (content.isOpenInNewWindow()) out.write(" checked");%>> <kantega:label key="aksess.insertlink.opennewwindow"/>
<%
        }
    }
%>
</div>