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
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><kantega:hassection id="title"><kantega:getsection id="title"/> - </kantega:hassection><kantega:label key="aksess.title"/></title>
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-loginlayout.css"/>">
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/admin-loginlayout.js"/>"></script>
    <kantega:getsection id="head"/>
    <!--[if lte IE 9]>
    <style>
        form label {
            text-indent: 0;
        }
    </style>
    <![endif]-->
</head>
<body class="<kantega:getsection id="bodyclass"/>">

<div id="TopMenu">
    <a class="logo" href="http://opensource.kantega.no/aksess/" id="OpenAksessInfoButton" title="<kantega:label key="aksess.title"/>">&nbsp;</a>
</div>

<div id="contentWrapper">
    <div class="body">
        <div id="version">

            Versjon <%=Aksess.getVersion()%>
        </div>

        <kantega:getsection id="body"/>

    </div>

</div>

</body>
</html>
