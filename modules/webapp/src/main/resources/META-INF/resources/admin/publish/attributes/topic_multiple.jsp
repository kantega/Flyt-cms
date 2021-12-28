<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.TopicAttribute,
                 no.kantega.publishing.common.service.TopicMapService,
                 no.kantega.publishing.topicmaps.data.Topic"%>
<%@ page import="java.util.List" %>
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
    TopicAttribute attribute = (TopicAttribute) request.getAttribute("attribute");
    String fieldName = (String) request.getAttribute("fieldName");
    TopicMapService topicMapService = new TopicMapService(request);
    String value = attribute.getValue();
%>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
    <select name="<%=fieldName%>list" class="inp fullWidth" size="10">
    <%
        if (value != null && value.length() > 0) {
            List<Topic> topics = attribute.getValueAsTopics();

            for (Topic topic : topics) {
                out.write("<option value=\"" + topic.getTopicMapId() + ":" + topic.getId() + "\">" + topic.getBaseName() + " (" + topicMapService.getTopicMap(topic.getTopicMapId()).getName() + ")" + "</option>");
            }
        }
    %>
    </select>
</div>
<div class="buttonGroup">
    <a href="#" onclick="openaksess.editcontext.selectTopic(document.myform.<%=fieldName%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="add"><kantega:label key="aksess.button.add"/></span></a>
    <a href="#" onclick="openaksess.editcontext.removeValueAndNameFromForm(document.myform.<%=fieldName%>)" class="button" tabindex="<%=(attribute.getTabIndex()+2)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
    <a href="#" onclick="openaksess.editcontext.moveId(document.myform.<%=fieldName%>, -1)" class="button" tabindex="<%=attribute.getTabIndex()+3%>"><span class="moveUp"><kantega:label key="aksess.button.moveup"/></span></a>
    <a href="#" onclick="openaksess.editcontext.moveId(document.myform.<%=fieldName%>, 1)" class="button" tabindex="<%=(attribute.getTabIndex()+4)%>"><span class="moveDown"><kantega:label key="aksess.button.movedown"/></span></a>
</div>
