<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier,
                 no.kantega.publishing.common.data.enums.Language,
                 no.kantega.publishing.common.data.Content,
                 no.kantega.publishing.common.data.attributes.Attribute,
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
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:selectContent(document.myform.<%=fieldName%>, <%=maxitems%>)"><img src="../bitmaps/common/buttons/mini_legg_til.gif" border="0"></a></td>
                <td><a href="Javascript:selectContent(document.myform.<%=fieldName%>, <%=maxitems%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.leggtil"/></a></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                <td><a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><kantega:label key="aksess.button.slett"/></a></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:moveId(document.myform.<%=fieldName%>, -1)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.button.flyttopp"/></a></td>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:moveId(document.myform.<%=fieldName%>, 1)" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><kantega:label key="aksess.button.flyttned"/></a></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
        <select name="<%=fieldName%>list" class="inp" style="width:600px;" size="10">
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
    </td>
</tr>