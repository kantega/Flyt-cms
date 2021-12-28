<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<!DOCTYPE HTML>
<html>
<head>
    <title><kantega:getsection id="title"/></title>
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-searchlayout.css"/>">
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/admin-searchlayout.js"/>"></script>
    <script type="text/javascript" src='<kantega:expireurl url="/admin/js/jquery-ui-i18n.min.js"/>'></script>

    <kantega:getsection id="head"/>
</head>

<body>
    <div id="Content" class="search">
        <div class="searchicon"></div>
        <kantega:getsection id="body"/>
    </div>
</body>
</html>
