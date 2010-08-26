<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title><kantega:hassection id="title"><kantega:getsection id="title"/> - </kantega:hassection><kantega:label key="aksess.title"/></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/wro-oa/admin-loginlayout.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/wro-oa/admin-loginlayout.js"></script>
    <kantega:getsection id="head"/>
</head>
<body class="<kantega:getsection id="bodyclass"/>">

<div id="TopMenu">
    <a class="logo" href="http://opensource.kantega.no/aksess/" id="OpenAksessInfoButton" title="<kantega:label key="aksess.title"/>">&nbsp;</a>
</div>

<div id="contentWrapper">
    <div class="body">
        <div id="version">
            <img src="${pageContext.request.contextPath}/login/bitmaps/openaksess.png" alt="">
            <br>
            Versjon <%=Aksess.getVersion()%>
        </div>

        <kantega:getsection id="body"/>

    </div>

</div>

</body>
</html>
