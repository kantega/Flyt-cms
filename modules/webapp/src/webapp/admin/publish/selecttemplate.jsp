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
    Content parent = (Content)session.getAttribute("currentContent");
    if (parent == null) {
        response.sendRedirect("content.jsp?action=previewcontent");
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.title"/></title>
</head>
    <frameset name="myframeset" rows="38,*,28"  frameborder="0" border="0">
        <frame name="contenttop" src="selecttemplate_title.jsp" scrolling="no" marginwidth="0" marginheight="0" noresize>
        <frameset name="myframeset2" cols="22,*"  frameborder="0" border="0">
            <frame name="navigatorsplit" src="../navigatorsplit.jsp" scrolling="no" marginwidth="0" marginheight="0" noresize>
            <frame name="contentmain" src="AddContent.action" scrolling="auto" marginwidth="0" marginheight="0" noresize>
        </frameset>
        <frame name="contentbottom" src="../statusframe.jsp" scrolling="no" marginwidth="0" marginheight="0" noresize>
    </frameset>
</html>
<%@ include file="../include/jsp_footer.jsf" %>