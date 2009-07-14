<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier,
                 no.kantega.publishing.common.data.Content,
                 no.kantega.publishing.common.service.ContentManagementService,
                 no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.common.data.attributes.ContentidAttribute" %>
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
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonGroup">
    <a href="Javascript:selectContent(document.myform.<%=fieldName%>, <%=maxitems%>)" class="button add" tabindex="<%=attribute.getTabIndex()%>"><span><kantega:label key="aksess.button.leggtil"/></span></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete" tabindex="<%=(attribute.getTabIndex()+2)%>"><span><kantega:label key="aksess.button.slett"/></span></a>
    <a href="Javascript:moveId(document.myform.<%=fieldName%>, -1)" class="button moveUp" tabindex="<%=attribute.getTabIndex()+3%>"><span><kantega:label key="aksess.button.flyttopp"/></span></a>
    <a href="Javascript:moveId(document.myform.<%=fieldName%>, 1)" class="button moveDown" tabindex="<%=(attribute.getTabIndex()+4)%>"><span><kantega:label key="aksess.button.flyttned"/></span></a>
</div>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
    <select name="<%=fieldName%>list" size="10" class="fullWidth">
        <%
            if (value != null && value.length() > 0) {
                int ids[] = StringHelper.getInts(value, ",");

                for (int i = 0; i < ids.length; i++) {
                    ContentIdentifier cid = new ContentIdentifier();
                    cid.setAssociationId(ids[i]);
                    Content c = cms.getContent(cid);
                    if (c != null) {
                        out.write("<option value=\"" + c.getAssociation().getId() + "\">" + c.getTitle() + "</option>");
                    }
                }
            }
        %>
    </select>
</div>