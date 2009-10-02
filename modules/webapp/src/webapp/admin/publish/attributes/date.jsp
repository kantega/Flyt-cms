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
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="inputs">
    <input type="text" id="<%=fieldName%>" size="<%=len%>" maxlength="<%=len%>" name="<%=fieldName%>" value="<%=value%>" tabindex="<%=attribute.getTabIndex()%>">&nbsp;(<%=df%>)<br>
    <script type="text/javascript">
        Calendar.setup( { inputField  : "<%=fieldName%>", ifFormat : "%d.%m.%Y", button : "velgdato<%=fieldName%>", firstDay: 1} );
    </script>
</div>
<div class="buttonGroup">
    <a href="#" id="velgdato<%=fieldName%>" class="button"><span class="calendar"><kantega:label key="aksess.button.choose"/></span></a>
</div>