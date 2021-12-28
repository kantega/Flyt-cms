<%@ page import="no.kantega.publishing.common.Aksess" %>
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
<!DOCTYPE HTML>
<html>
<head>
    <title>Flyt CMS</title>

    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-core.css"/>">
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/jquery-all.js"/>"></script>

    <style type="text/css">
        #contentWrapper {
            width: 600px;
            padding: 20px;
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

