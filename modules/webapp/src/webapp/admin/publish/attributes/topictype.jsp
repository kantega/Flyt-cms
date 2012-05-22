<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.TopictypeAttribute,
                 no.kantega.publishing.topicmaps.ao.TopicAO,
                 no.kantega.publishing.topicmaps.ao.TopicMapAO,
                 no.kantega.publishing.topicmaps.data.Topic"%>
<%@ page import="no.kantega.publishing.topicmaps.data.TopicMap"%>
<%@ page import="java.util.List"%>
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
    TopictypeAttribute attribute = (TopictypeAttribute) request.getAttribute("attribute");

    String fieldName = (String)request.getAttribute("fieldName");
%>
<div class="inputs">
<%
    String value = attribute.getValue();

    List topicMaps = TopicMapAO.getTopicMaps();
    out.write("<select class=\"fullWidth\"");
    out.write(" tabindex=\"" + attribute.getTabIndex() + "\"");
    out.write(" name=\"" + fieldName + "\">");
    for (int i = 0; i < topicMaps.size(); i++) {
        TopicMap map  = (TopicMap)topicMaps.get(i);
        String optText  = map.getName();

        out.write("<optgroup");
        out.write(" label=\"" +map.getName() +"\"");
        out.write(">");
        List types = TopicAO.getTopicTypes(map.getId());
        for (int j = 0; j < types.size(); j++) {
            Topic topic = (Topic) types.get(j);
            String id = topic.getTopicMapId() +":" + topic.getId();
            %>
            <option value="<%=topic.getTopicMapId() +":" + topic.getId()%>" <%= (value != null && value.equals(id)) ? "selected" : "" %>>    <%=topic.getBaseName()%></option>
            <%

        }
                    out.write("</optgroup>");


    }

    out.write("</select>");
%>
</div>