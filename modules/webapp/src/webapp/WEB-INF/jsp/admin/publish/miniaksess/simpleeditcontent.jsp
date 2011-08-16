<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.admin.AdminRequestParameters" %>
<%@ taglib prefix="miniaksess" uri="http://www.kantega.no/aksess/tags/miniaksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>OpenAksess7</title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/reset.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/default.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/jquery-1.5.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-ui-1.8.14.custom.min.js"></script>
    <style type="text/css">
        #contentWrapper {
            width: 600px;
        }

        form {
            padding-top: 2em;
            padding-bottom: 2em;
        }
    </style>

    <% request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale()); %>
    <miniaksess:headerdependencies/>
</head>
<body class="miniedit">

<div id="TopMenu">
    <a class="logo" href="http://opensource.kantega.no/aksess/" id="OpenAksessInfoButton" title="<kantega:label key="aksess.title"/>">&nbsp;</a>
</div>

<div id="contentWrapper">
    <div class="body">
        <kantega:getsection id="body"/>
        <miniaksess:form>
            <miniaksess:inputscreen/>
        </miniaksess:form>
    </div>
</div>

</body>
</html>

