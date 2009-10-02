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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><kantega:hassection id="title"><kantega:getsection id="title"/> - </kantega:hassection><kantega:label key="aksess.title"/></title>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/admin/favicon.ico" type="image/x-icon">

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/css/reset.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/css/base.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/css/default.css">
    <!--[if lt IE 8]>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/css/default_ie7.css">
    <![endif]-->

    <script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/jquery.dimensions.pack.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/jquery.interface.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/jquery.contextMenu.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/jquery.roundcorners.js"></script>
    <script type="text/javascript" src='<%=request.getContextPath()%>/admin/dwr/interface/ContentStateHandler.js'></script>
    <script type="text/javascript" src='<%=request.getContextPath()%>/admin/dwr/engine.js'></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/common.jjs"></script>
    <kantega:getsection id="head"/>
    <script type="text/javascript">
        $(document).ready(function(){
            $("div.fieldset").roundCorners();
        });
    </script>
</head>
<body>

<div id="Top">
    <kantega:hassection id="topMenu">
        <div id="TopMenu">
            <kantega:getsection id="topMenu"/>
        </div>
    </kantega:hassection>

    <kantega:hassection id="modesMenu">
        <div id="ModesMenu">
            <kantega:getsection id="modesMenu"/>
        </div>
    </kantega:hassection>

    <kantega:hassection id="toolsMenu">
        <div id="ToolsMenu">
            <kantega:getsection id="toolsMenu"/>
        </div>
    </kantega:hassection>
    <kantega:hassection id="tabToolsMenu">
        <div id="TabToolsMenu">
            <kantega:getsection id="tabToolsMenu"/>
        </div>
    </kantega:hassection>
</div>

<kantega:getsection id="body"/>


</body>
</html>
