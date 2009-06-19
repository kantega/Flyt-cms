<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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
    List topics = null;
    TopicMap topicMap = (TopicMap)session.getAttribute("currentTopicMap");
    Topic instanceOf  = (Topic)session.getAttribute("currentTopic");
    if (instanceOf == null || topicMap == null) {
        response.sendRedirect("start.jsp");
    }

    topics = topicService.getTopicsByInstance(instanceOf);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>instances.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/topicmap.js"></script>
<body class="bodyWithMargin">
<table border="0" cellspacing="0" cellpadding="0" width="90%">
    <tr class="tableHeading">
        <td><b><%=instanceOf.getBaseName()%></b></td>
    </tr>
    <%
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = (Topic)topics.get(i);
    %>
            <tr class="tableRow<%=(i%2)%>">
                <td><a href="Javascript:gotoTopic(<%=topic.getTopicMapId()%>, '<%=topic.getId()%>')"><%=topic.getBaseName()%></a></td>
            </tr>
    <%
        }
    %>
</table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>