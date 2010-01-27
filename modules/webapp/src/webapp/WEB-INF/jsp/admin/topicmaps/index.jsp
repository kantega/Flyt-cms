<%@ page import="no.kantega.publishing.common.data.WorkList" %>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.topicmaps.title"/>
</kantega:section>

<kantega:section id="contentclass">topicmaps</kantega:section>

<kantega:section id="head extras">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/topicmaps.jjs"></script>
</kantega:section>

<kantega:section id="modesMenu">
</kantega:section>

<kantega:section id="toolsMenu">
</kantega:section>

<kantega:section id="content">
    <%-- The content is loaded with ajax by the ListTopicTypesAction --%>
    <div id="TopicTypes"></div>
</kantega:section>
<%@ include file="../layout/fullwidthLayout.jsp" %>