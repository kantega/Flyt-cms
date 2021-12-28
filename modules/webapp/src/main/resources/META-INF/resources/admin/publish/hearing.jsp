<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%
    RequestParameters param = new RequestParameters(request);
%>
<html>
<head>
	<title>Send side til høring</title>
</head>
<frameset rows="*,40" frameborder="0" border="0">
    <frame name="main" src="hearing_body.jsp?contentId=<%=param.getInt("contentId")%>" marginwidth="0" marginheight="0" scrolling="auto">
    <frame src="hearing_bottomframe.jsp" marginwidth="0" marginheight="0" scrolling="no">
</frameset>
</html>
