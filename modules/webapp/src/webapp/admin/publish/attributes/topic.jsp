<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.TopicAttribute,
                 no.kantega.publishing.topicmaps.data.Topic"%>
<%@ page import="no.kantega.publishing.common.service.TopicMapService"%>
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
    TopicAttribute attribute = (TopicAttribute)request.getAttribute("attribute");
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();
    String topicname = "";

    if (value != null && value.length() > 0) {
        TopicMapService topicMapService = new TopicMapService(request);
        Topic topic = topicMapService.getTopic(attribute.getTopicMapId(), attribute.getTopicId());
        if (topic != null) {
            topicname = topic.getBaseName();
        } else {
            topicname = "";
            value = "";
        }
    }
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonGroup">
    <a href="Javascript:selectTopic(document.myform.<%=fieldName%>)" class="button choose" tabindex="<%=attribute.getTabIndex()%>"><span><kantega:label key="aksess.button.choose"/></span></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete" tabindex="<%=(attribute.getTabIndex()+1)%>"><span><kantega:label key="aksess.button.delete"/></span></a>
</div>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>">
    <input type="text" class="disabled fullWidth" name="<%=fieldName%>text" value="<%=topicname%>" readonly onFocus="this.blur()">
</div>