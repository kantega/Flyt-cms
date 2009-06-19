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

<%--
  User: Kristian Lier Selnæs, Kantega AS
  Date: Jun 11, 2007
  Time: 11:16:06 AM
--%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%
    Content content = (Content)request.getAttribute("content");
    EditablelistAttribute attribute = (EditablelistAttribute) request.getAttribute("attribute");
    String fieldName = (String) request.getAttribute("fieldName");
%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <%
                    if (SecuritySession.getInstance(request).isUserInRole(attribute.getEditableBy())) {
                %>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:addListOption(document.myform.<%=fieldName%>, '<%=attribute.getKey()%>', <%=content.getLanguage()%>)"><img src="../bitmaps/common/buttons/mini_legg_til.gif" border="0"></a></td>
                <td><a href="Javascript:addListOption(document.myform.<%=fieldName%>, '<%=attribute.getKey()%>', <%=content.getLanguage()%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.leggtil"/></a></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:removeOptionFromList(document.myform.<%=fieldName%>, '<%=attribute.getKey()%>', <%=content.getLanguage()%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                <td><a href="Javascript:removeOptionFromList(document.myform.<%=fieldName%>, '<%=attribute.getKey()%>', <%=content.getLanguage()%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><kantega:label key="aksess.button.slett"/></a></td>
                <%
                    } 
                %>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <%@include file="listoptions.jsf"%>
    </td>
</tr>
