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
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();
%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%></b></td>
                <% if (value != null && value.length() > 0) {%>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><%=value%></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="<%=attribute.getProperty(AttributeProperty.HTML)%>" target="_new"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0"></a></td>
                <td><a href="<%=attribute.getProperty(AttributeProperty.HTML)%>" target="_new"><kantega:label key="aksess.button.visfil"/></a></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a class="mini_remove" href="Javascript:removeAttachment(document.myform.<%=fieldName%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                <td><a href="Javascript:removeAttachment(document.myform.<%=fieldName%>)" class="button"><kantega:label key="aksess.button.slett"/></a></td>
                <%}%>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <input type="file" class="inp" style="width:600px;" name="<%=fieldName%>" value="<%=value%>" tabindex="<%=attribute.getTabIndex()%>">
        <input type="hidden" name="delete_<%=fieldName%>" value="0">
    </td>
</tr>
