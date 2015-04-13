<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.data.attributes.TopictypeAttribute,
                 no.kantega.publishing.topicmaps.ao.TopicDao"%>
<%@ page import="no.kantega.publishing.topicmaps.ao.TopicMapDao"%>
<%@ page import="no.kantega.publishing.topicmaps.data.Topic"%>
<%@ page import="no.kantega.publishing.topicmaps.data.TopicMap" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
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
    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
    TopicDao topicDao = ctx.getBean(TopicDao.class);
    TopicMapDao topicMapDao = ctx.getBean(TopicMapDao.class);

    TopictypeAttribute attribute = (TopictypeAttribute) request.getAttribute("attribute");

    String fieldName = (String)request.getAttribute("fieldName");
%>
<div class="inputs">
<%
    String value = attribute.getValue();

    List<TopicMap> topicMaps = topicMapDao.getTopicMaps();
    out.write("<select class=\"fullWidth\"");
    out.write(" tabindex=\"" + attribute.getTabIndex() + "\"");
    out.write(" name=\"" + fieldName + "\">");
    for (TopicMap topicMap : topicMaps) {
        out.write("<optgroup");
        out.write(" label=\"" + topicMap.getName() + "\"");
        out.write(">");
        List<Topic> types = topicDao.getTopicTypesForTopicMapId(topicMap.getId());
        for (Topic topic : types) {
            String id = topic.getTopicMapId() + ":" + topic.getId();
%>
    <option value="<%=topic.getTopicMapId() +":" + topic.getId()%>" <%= (value != null && value.equals(id)) ? "selected" : "" %>><%=topic.getBaseName()%></option>
    <%

            }
            out.write("</optgroup>");


        }

    out.write("</select>");
%>
</div>
