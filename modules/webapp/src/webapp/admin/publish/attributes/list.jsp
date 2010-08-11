<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.attributes.ListAttribute"%>
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
    ListAttribute attribute = (ListAttribute)request.getAttribute("attribute");
    String fieldName = (String)request.getAttribute("fieldName");
%>
<tr>
    <td class="inpHeading"><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
</tr>
<tr>
    <td class="inpHeadingSpacer"><img src="<%=request.getContextPath()%>/admin/bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <%@include file="listoptions.jsf"%>
    </td>
</tr>