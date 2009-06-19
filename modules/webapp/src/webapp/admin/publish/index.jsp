<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
<%@ page import="java.util.Date"%><%--
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>Untitled</title>
</head>
<frameset cols="214,*" frameborder="0" border="0">
    <frameset rows="38,24,*,4">
        <frame src="search.jsp" marginwidth="0" marginheight="0" scrolling="no">
        <frame src="sort.jsp" marginwidth="0" marginheight="0" scrolling="no">
        <frame name="navtree" src="navigator.jsp" marginwidth="0" marginheight="0" scrolling="auto">
        <frame src="../navigatorbottom.jsp" marginwidth="0" marginheight="0" scrolling="no">
    </frameset>
    <frame name="content" src="content.jsp?activetab=previewcontent&dummy=<%=new Date().getTime()%>" marginwidth="0" marginheight="0" scrolling="no">
</frameset>
</html>
<%@ include file="../include/jsp_footer.jsf" %>