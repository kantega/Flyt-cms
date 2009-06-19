<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
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
    RequestParameters param = new RequestParameters(request, "utf-8");

    String activetab = param.getString("activetab");
    String infomessage = param.getString("infomessage");
    if (infomessage != null) {
        // Vis alerts i hovedfelt
        infomessage = "&infomessage=" + infomessage;
    } else {
        infomessage = "";
    }

    String task = param.getString("task");
    if (task != null) {
        task = "&task=" + task;
    } else {
        task = "";
    }

    String type = param.getString("type");
    if (type != null) {
        type = "&type=" + type;
    } else {
        type = "";
    }

    int topicMapId = param.getInt("topicMapId");
    if (topicMapId != -1) {
        TopicMap topicmap = topicService.getTopicMap(topicMapId);
        session.setAttribute("currentTopicMap", topicmap);
    }

    Topic topic = (Topic)session.getAttribute("currentTopic");

    if (activetab == null) {
        if (topic != null) {
            activetab = "topic";
        } else {
            activetab = "start";
        }
    }

    String topicId = param.getString("topicId");
    if (topicId != null) {
        topic = topicService.getTopic(topicMapId, topicId);
    }



    if (topic != null) {
        session.setAttribute("currentTopic", topic);
    } else {
        session.removeAttribute("currentTopic");
    }

    long refresh = new Date().getTime();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.title"/></title>
</head>
    <frameset name="myframeset" rows="38,*,28"  frameborder="0" border="0">
        <frame name="contenttop" src="titleframe.jsp?activetab=<%=activetab%>&dummy=<%=refresh%>" scrolling="no" marginwidth="0" marginheight="0" noresize>
        <frameset name="myframeset2" cols="22,*"  frameborder="0" border="0">
            <frame name="navigatorsplit" src="../navigatorsplit.jsp" scrolling="no" marginwidth="0" marginheight="0" noresize>
            <frame name="contentmain" src="<%=activetab%>.jsp?dummy=<%=refresh%><%=task%><%=type%>" scrolling="auto" marginwidth="0" marginheight="0" noresize>
        </frameset>
        <frame name="contentbottom" src="statusframe.jsp?activetab=<%=activetab%>&dummy=<%=refresh%>" scrolling="no" marginwidth="0" marginheight="0" noresize>
    </frameset>
</html>

<%@ include file="../include/jsp_footer.jsf" %>