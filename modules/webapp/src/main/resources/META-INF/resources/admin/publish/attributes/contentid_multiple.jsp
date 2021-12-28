<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.util.StringHelper,
                 no.kantega.publishing.api.content.ContentIdentifier,
                 no.kantega.publishing.common.data.Content,
                 no.kantega.publishing.common.data.attributes.ContentidAttribute"%>
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
    ContentidAttribute attribute = (ContentidAttribute) request.getAttribute("attribute");
    int maxitems = attribute.getMaxitems();
    String fieldName = (String) request.getAttribute("fieldName");

    String value = attribute.getValue();

    ContentManagementService cms = new ContentManagementService(request);

    Content content = (Content)request.getAttribute("content");
%>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
    <select name="<%=fieldName%>list" size="10" class="fullWidth">
        <%
            if (value != null && value.length() > 0) {
                int ids[] = StringHelper.getInts(value, ",");

                for (int i = 0; i < ids.length; i++) {
                    ContentIdentifier cid =  ContentIdentifier.fromAssociationId(ids[i]);
                    Content c = cms.getContent(cid);
                    if (c != null) {
                        out.write("<option value=\"" + c.getAssociation().getId() + "\">" + c.getTitle() + "</option>");
                    }
                }
            }
        %>
    </select>
</div>
<div class="buttonGroup">
    <a href="#" onclick="openaksess.editcontext.selectContent(document.myform.<%=fieldName%>, '<%=maxitems%>', '<%=attribute.getStartId(content)%>','true', '<%=attribute.getContentTemplate()%>')" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="add"><kantega:label key="aksess.button.add"/></span></a>
    <a href="#" onclick="openaksess.editcontext.removeValueAndNameFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+2)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
    <a href="#" onclick="openaksess.editcontext.moveId(document.myform.<%=fieldName%>, -1)" class="button" tabindex="<%=attribute.getTabIndex()+3%>"><span class="moveUp"><kantega:label key="aksess.button.moveup"/></span></a>
    <a href="#" onclick="openaksess.editcontext.moveId(document.myform.<%=fieldName%>, 1)" class="button" tabindex="<%=(attribute.getTabIndex()+4)%>"><span class="moveDown"><kantega:label key="aksess.button.movedown"/></span></a>
</div>
