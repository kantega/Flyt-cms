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
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:selectContentUrl(document.myform.<%=fieldName%>)"><img src="<%=request.getContextPath()%>/admin/bitmaps/common/buttons/mini_velg.gif" border="0"></a></td>
                <td><a href="Javascript:selectContentUrl(document.myform.<%=fieldName%>)" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                <td><img src="<%=request.getContextPath()%>/admin/bitmaps/common/textseparator.gif"></td>
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
        <input type="text" class="inp" onFocus="setFocusField(this)" onBlur="blurField()" name="<%=fieldName%>" value="<%=value%>" maxlength="512" style="width:600px;" tabindex="<%=attribute.getTabIndex()%>">
    </td>
</tr>
<%
    if (!Aksess.doOpenLinksInNewWindow() && ContentProperty.URL.equalsIgnoreCase(attribute.getField())) {
        // La bruker velge om den skal åpnes i eget vindu
        Configuration c = Aksess.getConfiguration();
        if (c.getBoolean("openinnewwindow.disabled", true)) {
%>
<tr>
    <td>
        <input type="checkbox" name="<%=fieldName%>_newwindow" value="true" <% if (content.isOpenInNewWindow()) out.write(" checked");%>> <kantega:label key="aksess.insertlink.opennewwindow"/>
    </td>
</tr>
<%
        }
    }
%>
