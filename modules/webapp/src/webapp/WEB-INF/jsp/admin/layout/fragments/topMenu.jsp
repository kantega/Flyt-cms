<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.security.service.SecurityService" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
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


<a class="logo" href="#" id="OpenAksessInfoButton" title="<kantega:label key="aksess.title"/>">&nbsp;</a>
<a href="${pageContext.request.contextPath}/admin/mypage/ViewMyPage.action" class="menuitem ${mypageSelected}"><kantega:label key="aksess.menu.mypage"/></a>
<a href="${pageContext.request.contextPath}/admin/publish/Navigate.action" class="menuitem ${publishSelected}"><kantega:label key="aksess.menu.publish"/></a>
<a href="${pageContext.request.contextPath}/admin/multimedia/Navigate.action" class="menuitem ${multimediaSelected}"><kantega:label key="aksess.menu.multimedia"/></a>
<a href="${pageContext.request.contextPath}/admin/topicmaps/Topics.action" class="menuitem ${topicMapsSelected}"><kantega:label key="aksess.menu.topicmaps"/></a>
<% if (SecuritySession.getInstance(request).isUserInRole(Aksess.getAdminRole())) { %>
<a href="${pageContext.request.contextPath}/admin/administration/ViewSystemInformation.action" class="menuitem ${administrationSelected}"><kantega:label key="aksess.menu.administration"/></a>
<%}%>

<a href="${pageContext.request.contextPath}/Logout.action" class="menuitem logout"><kantega:label key="aksess.menu.logout"/></a>
<a href="http://opensource.kantega.no/aksess/help/?locale=<%=Aksess.getDefaultAdminLocale().toString()%>" class="menuitem help" onclick="window.open(this.href); return false;"><kantega:label key="aksess.menu.help"/></a>
