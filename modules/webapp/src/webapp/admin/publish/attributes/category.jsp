<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="no.kantega.publishing.admin.category.CategoryManager"%>
<%@ page import="no.kantega.publishing.common.data.ContentCategory"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
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
    String name = null;


    ApplicationContext context  = RootContext.getInstance();
    Iterator i = context.getBeansOfType(CategoryManager.class).values().iterator();
    CategoryManager manager = null;
    if (i.hasNext()) {
        manager = (CategoryManager) i.next();
    }
    int categoryId = -1;
    if (value != null && !value.trim().equals("")) {
        try {
            categoryId = Integer.parseInt(value);
        } catch (NumberFormatException e) {

        }
    } else {
        if (manager != null) {
            ContentCategory category = manager.getDefaultCategory(content);
            if (category != null) {
                categoryId = category.getId();
            }
        }
    }

    // Try to get the name by looking it up in the OrganizationManager
    if (manager != null) {
        if(categoryId != -1) {
            ContentCategory category = manager.getCategoryById(categoryId);
            if(category != null) {
                name = category.getName();
            }
        }
    }

    if ("-1".equals(value)) {
        value = "";
    }    
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonGroup">
    <a href="Javascript:selectCategory(document.myform.<%=fieldName%>)" class="button select"><kantega:label key="aksess.button.velg"/></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete"><kantega:label key="aksess.button.slett"/></a>
</div>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=categoryId%>">
    <input type="text" class="disabled fullWidth" readonly onFocus="this.blur()"  name="<%=fieldName%>text" value="<%= name != null && !name.equals("") ? name : value%>" maxlength="512" tabindex="<%=attribute.getTabIndex()%>">
</div>