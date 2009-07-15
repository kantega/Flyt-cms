<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier,
                 no.kantega.publishing.common.data.Content,
                 no.kantega.publishing.common.data.attributes.Attribute,
                 no.kantega.publishing.common.service.ContentManagementService"%>
<%@ page import="no.kantega.commons.util.LocaleLabels"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
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

    String contentname = "";
    if (value != null && value.length() > 0) {
        try {
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(Integer.parseInt(value));
            ContentManagementService cms = new ContentManagementService(request);
            Content c = cms.getContent(cid);
            if (c != null) {
                contentname = c.getTitle();
            } else {
                value = "";
            }
        } catch (NumberFormatException e) {
            value = "";
        }
    }
    if (contentname.length() == 0) {
        contentname = LocaleLabels.getLabel("aksess.insertlink.internal.hint", Aksess.getDefaultAdminLocale());
    }

%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonGroup">
    <a href="Javascript:selectContent(document.myform.<%=fieldName%>)" class="button add" tabindex="<%=attribute.getTabIndex()%>"><span><kantega:label key="aksess.button.leggtil"/></span></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete" tabindex="<%=(attribute.getTabIndex()+1)%>"><span><kantega:label key="aksess.button.delete"/></span></a>
</div>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
    <input type="text" name="<%=fieldName%>text" value="<%=contentname%>" onFocus="this.select()" class="fullWidth">
    <script type="text/javascript">
        Autocomplete.setup({'inputField' :'<%=fieldName%>', url:'../../ajax/SearchContentAsXML.action', 'minChars' :3 });
    </script>
</div>
