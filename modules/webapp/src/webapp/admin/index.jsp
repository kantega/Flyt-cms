<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.util.HttpHelper" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier" %>
<%@ page import="no.kantega.publishing.common.exception.ExceptionHandler" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
    RequestParameters param = new RequestParameters(request);
    String activetab = param.getString("activetab");
    if (activetab == null) {
        activetab = "publish";
    }

    try {        
        session.setAttribute("adminMode", "true");

        String thisId = request.getParameter("thisId");
        String contentId = request.getParameter("contentId");
        if (thisId != null || contentId != null) {
            ContentManagementService cms = new ContentManagementService(request);
            ContentIdentifier cid = new ContentIdentifier(request);
            Content current = cms.getContent(cid);
            if (current != null) {
                session.setAttribute("showContent", current);
                session.setAttribute("currentContent", current);
            }
        }
    } catch (Exception e) {
        ExceptionHandler handler = new ExceptionHandler();

        Throwable cause = (Throwable)e;
        handler.setThrowable((Exception)cause, request.getRequestURI());
        request.getSession(true).setAttribute("handler", handler);
        request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title><kantega:label key="aksess.title"/></title>
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
</head>
<script type="text/javascript">
    function endAdminSession() {
        location.href = "<%=Aksess.getContextPath()%>/admin/EndAdminMode.action";
    }
</script>

<frameset rows="69,*" frameborder="0" border="0" onbeforeunload="endAdminSession()">
    <frame name="topmenu" src="topmenu.jsp?activetab=<%=activetab%>" marginwidth="0" marginheight="0" scrolling="no">
    <frame name="main" src="<%=activetab%>/index.jsp" marginwidth="0" marginheight="0" scrolling="no">
</frameset>
</html>
