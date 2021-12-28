<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="static no.kantega.publishing.api.ContentUtil.tryGetFromRequest" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%
    String url = "";
    String title = "";
    Content content = tryGetFromRequest(request);
    if (content != null) {
        url = content.getLocation();
        title = content.getTitle();
    }

    request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());
%>
<!DOCTYPE HTML>

<html>
<head>
    <title>Untitled</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/default.css">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/jquery-all.css"/>">
</head>

<body style="margin: 10px">
<div class="ui-state-highlight">
    <p><kantega:label key="aksess.showcontentinframe.file"/></p>
    <p><kantega:label key="aksess.showcontentinframe.showfile"/> <a href="${pageContext.request.contextPath}/attachment.ap?id=<%=url%>" target="_new" class="textlink"><%=title%></a></p>
</div>
</body>
</html>
