<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.util.List,
                 no.kantega.publishing.common.data.attributes.DocumenttypeAttribute,
                 no.kantega.publishing.common.data.DocumentType"%>
<%@ page import="no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.common.cache.DocumentTypeCache" %>
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
    DocumenttypeAttribute attribute = (DocumenttypeAttribute)request.getAttribute("attribute");
    String    fieldName = (String)request.getAttribute("fieldName");
%>
<tr>
    <td class="inpHeading"><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
<%
    String value = attribute.getValue();
    int ids[] = StringHelper.getInts(value, ",");
    out.write("<select class=inp style=\"width:600px;\"");
    out.write(" name=\"" + fieldName + "\"");
    out.write(" tabindex=\"" + attribute.getTabIndex() + "\"");
    if (attribute.getMultiple()) {
        out.write(" size=\"10\" multiple");
    }
    out.write(">");
    List all  = DocumentTypeCache.getDocumentTypes();

    for (int i = 0; i < all.size(); i++) {
        DocumentType dt = (DocumentType)all.get(i);
        int id = dt.getId();

        String selected = "";
        if (value != null && value.length() > 0) {
            for (int j = 0; j < ids.length; j++) {
                if (ids[j] == id) {
                    selected = "selected";
                    break;
                }

            }
        }
        out.write("<option value=\"" + id + "\" " + selected + ">" + dt.getName() +"</option>");
    }
    out.write("</select>");
%>
    </td>
</tr>