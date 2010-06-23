<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.common.service.MultimediaService" %>
<%@ page import="no.kantega.publishing.common.data.attributes.MediaidAttribute" %>
<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
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
    MediaidAttribute attribute = (MediaidAttribute) request.getAttribute("attribute");
    String fieldName = (String) request.getAttribute("fieldName");

    String value = attribute.getValue();

    MultimediaService mediaService = new MultimediaService(request);
%>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
        <select name="<%=fieldName%>list" class="inp" style="width:600px;" size="10">
        <%
            if (value != null && value.length() > 0) {
                int ids[] = StringHelper.getInts(value, ",");

                for (int i = 0; i < ids.length; i++) {
                    Multimedia m = mediaService.getMultimedia(ids[i]);
                    if (m != null) {
                        out.write("<option value=\"" + m.getId() + "\">" + m.getName() + "</option>");
                    }
                }
            }
        %>
        </select>
</div>
<div class="buttonGroup">
    <a href="Javascript:openaksess.editcontext.selectMultimedia(document.myform.<%=fieldName%>, '')" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="add"><kantega:label key="aksess.button.add"/></span></a>
    <a href="Javascript:openaksess.editcontext.removeValueAndNameFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+2)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
    <a href="Javascript:openaksess.editcontext.moveId(document.myform.<%=fieldName%>, -1)" class="button" tabindex="<%=attribute.getTabIndex()+3%>"><span class="moveUp"><kantega:label key="aksess.button.moveup"/></span></a>
    <a href="Javascript:openaksess.editcontext.moveId(document.myform.<%=fieldName%>, 1)" class="button" tabindex="<%=(attribute.getTabIndex()+4)%>"><span class="moveDown"><kantega:label key="aksess.button.movedown"/></span></a>
</div>
