<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.util.LocaleLabels,
                 no.kantega.publishing.api.content.ContentIdentifier,
                 no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.attributes.ContentidAttribute"%>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
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
    ContentidAttribute attribute = (ContentidAttribute)request.getAttribute("attribute");
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();

    String contentname = "";
    if (value != null && value.length() > 0) {
        try {
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(Integer.parseInt(value));
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

    Content content = (Content)request.getAttribute("content");
%>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>" id="<%=fieldName%>">
    <input type="text" name="<%=fieldName%>text" id="<%=fieldName%>text" value="<%=contentname%>" onFocus="this.select()" class="fullWidth">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#<%=fieldName%>text").oaAutocomplete({
                defaultValue: '<kantega:label key="aksess.insertlink.internal.hint"/>',
                source: "${pageContext.request.contextPath}/ajax/AutocompleteContent.action",
                select: openaksess.editcontext.autocompleteInsertIntoFormCallback
            });
        });
    </script>
</div>
<div class="buttonGroup">
    <a href="#" onclick="openaksess.editcontext.selectContent(document.myform.<%=fieldName%>, 1, '<%=attribute.getStartId(content)%>', 'false', '<%=attribute.getContentTemplate()%>')" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
    <a href="#" onclick="openaksess.editcontext.removeValueAndNameFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
</div>
