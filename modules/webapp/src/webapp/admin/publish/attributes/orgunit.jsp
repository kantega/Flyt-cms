<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="no.kantega.publishing.org.OrganizationManager"%>
<%@ page import="no.kantega.publishing.org.OrgUnit"%>
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
    String name = null;

    OrganizationManager manager = null;

    // Try to get the name by looking it up in the OrganizationManager
    ApplicationContext context = RootContext.getInstance();
    Iterator i = context.getBeansOfType(OrganizationManager.class).values().iterator();
    if(i.hasNext()) {
        manager = (OrganizationManager) i.next();
        if (value != null && !value.trim().equals("")) {
            OrgUnit unit = manager.getUnitByExternalId(value);
            if (unit != null) {
                name = unit.getName();
            }
        }
    }
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<%
    if (manager != null) {
%>
<div class="buttonGroup">
    <a href="Javascript:selectOrgunit(document.myform.<%=fieldName%>)" class="button choose"><kantega:label key="aksess.button.choose"/></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete"><kantega:label key="aksess.button.delete"/></a>
</div>
<%
    }
%>
<div class="inputs">
    <%
        if (manager != null) {
    %>
            <input type="hidden" name="<%=fieldName%>" id="<%=fieldName%>" value="<%=value%>">
            <input type="text" name="<%=fieldName%>text" id="<%=fieldName%>text" value="<%= name != null && !name.equals("") ? name : value%>" maxlength="512" class="fullWidth" tabindex="<%=attribute.getTabIndex()%>">
            <script type="text/javascript">
                Autocomplete.setup({'inputField' :'<%=fieldName%>', url:'../../ajax/SearchOrgUnitsAsXML.action', 'minChars' :3 });
            </script>
    <%
        } else {
    %>
            <input type="text" name="<%=fieldName%>" id="<%=fieldName%>" value="<%=value%>" maxlength="512" class="fullWidth" tabindex="<%=attribute.getTabIndex()%>">
    <%
        }
    %>
</div>
