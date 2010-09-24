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
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-ui-1.8.2.custom.min.js"></script>
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
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { };
        }
        if (typeof properties.editcontext == 'undefined') {
            properties.editcontext = {};
        }
        properties.debug = <aksess:getconfig key="javascript.debug" default="false"/>;
        properties.contextPath = '${pageContext.request.contextPath}';
        properties.contentRequestHandler = '<%=Aksess.CONTENT_REQUEST_HANDLER%>';
        properties.thisId = '<%=AdminRequestParameters.THIS_ID %>';
        properties.editcontext['labels'] = {
            selecttopic : '<kantega:label key="aksess.selecttopic.title" escapeJavascript="true"/>',
            selectcontent : '<kantega:label key="aksess.popup.selectcontent" escapeJavascript="true"/>',
            selectorgunit : '<kantega:label key="aksess.popup.selectorgunit" escapeJavascript="true"/>',
            warningMaxchoose : '<kantega:label key="aksess.js.advarsel.dukanmaksimaltvelge" escapeJavascript="true"/> ',
            warningElements : '<kantega:label key="aksess.js.advarsel.elementer" escapeJavascript="true"/>',
            adduser : '<kantega:label key="aksess.adduser.title" escapeJavascript="true"/>',
            multimedia : '<kantega:label key="aksess.multimedia.title" escapeJavascript="true"/>',
            addrole : '<kantega:label key="aksess.addrole.title" escapeJavascript="true"/>',
            editablelistValue : '<kantega:label key="aksess.editablelist.value" escapeJavascript="true"/>'
        };
    </script>
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

