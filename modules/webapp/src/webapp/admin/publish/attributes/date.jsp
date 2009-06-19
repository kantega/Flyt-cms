<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.attributes.DateAttribute"%>
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
    String df = Aksess.getDefaultDateFormat();
    int len = df.length();

    DateAttribute attribute = (DateAttribute)request.getAttribute("attribute");
    String    fieldName = (String)request.getAttribute("fieldName");

    attribute.setFormat(df);
    String value = attribute.getValue();
%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="../bitmaps/common/textseparator.gif" alt=""></td>
                <td><a href="#" id="velgdato<%=fieldName%>0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                <td><a href="#" id="velgdato<%=fieldName%>1" class="button"><kantega:label key="aksess.button.velg"/></a></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
</tr>
<tr>
    <td>
        <input type="text" class="inp" id="<%=fieldName%>" size="<%=len%>" maxlength="<%=len%>" name="<%=fieldName%>" value="<%=value%>" tabindex="<%=attribute.getTabIndex()%>">&nbsp;(<%=df%>)<br>
        <script type="text/javascript">
            Calendar.setup( { inputField  : "<%=fieldName%>", ifFormat : "%d.%m.%Y", button : "velgdato<%=fieldName%>0", firstDay: 1} );
            Calendar.setup( { inputField  : "<%=fieldName%>", ifFormat : "%d.%m.%Y", button : "velgdato<%=fieldName%>1", firstDay: 1} );
        </script>
    </td>
</tr>