<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 no.kantega.publishing.common.data.attributes.FormAttribute"%>
<%@ page import="no.kantega.publishing.common.data.attributes.TopicmapAttribute"%>
<%@ page import="java.util.List"%>
<%@ page import="no.kantega.publishing.topicmaps.data.TopicMap"%>
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
    TopicmapAttribute attribute = (TopicmapAttribute) request.getAttribute("attribute");
    String fieldName = (String)request.getAttribute("fieldName");
%>
<div class="inputs">
<%
    String value = attribute.getValue();

    List topicMaps = attribute.getTopicMaps();
    out.write("<select class=\"fullWidth\"");
    out.write(" tabindex=\"" + attribute.getTabIndex() + "\"");
    out.write(" name=\"" + fieldName + "\">");
    for (int i = 0; i < topicMaps.size(); i++) {
        TopicMap map  = (TopicMap)topicMaps.get(i);
        String optVal = Integer.toString(map.getId());
        String optText  = map.getName();

        boolean selected = false;
        if (value != null && (value.equals(optVal))) {
            selected = true;
        }

        out.write("<option");
        out.write(" value=\"" + optVal + "\"");
        if (selected) {
            out.write(" selected");
        }
        out.write(">");
        out.write(optText);
        out.write("</option>");
    }

    out.write("</select>");
%>
</div>