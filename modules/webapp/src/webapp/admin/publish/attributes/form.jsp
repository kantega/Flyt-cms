<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.Content,
                 no.kantega.publishing.common.data.attributes.FormAttribute"%>
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
    Content content = (Content)request.getAttribute("content");
    FormAttribute attribute = (FormAttribute)request.getAttribute("attribute");
    String fieldName = (String)request.getAttribute("fieldName");
%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:editForm(document.myform.<%=fieldName%>)"><img src="../bitmaps/common/buttons/mini_rediger.gif" border="0"></a></td>
                <td><a href="Javascript:editForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.redigerskjema"/></a></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="inpHeadingSpacer"><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <%@include file="listoptions.jsf"%>
    </td>
</tr>