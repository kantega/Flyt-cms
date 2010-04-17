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
    String statusmessage = (String)request.getAttribute("statusmessage");
    if (statusmessage != null) {
        statusmessage = "&statusmessage=" + statusmessage;
    } else {
        statusmessage = "";
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.copypaste.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>

<script language="Javascript">
    if (window.opener) {
        window.opener.top.main.content.location.href = 'content.jsp?activetab=previewcontent<%=statusmessage%>&updatetree=true';
    }
</script>

<body class="bodyWithMargin">
<p>
    <kantega:label key="aksess.copypaste.duplicatealias"/>
</p>

<ul>
    <c:forEach items="${aliases}" var="alias">
        <li><c:out value="${alias}"/></li>
    </c:forEach>
</ul>

<p>
   <a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;
</p>

</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
