<%@ page import="no.kantega.publishing.common.Aksess" %>
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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>

<c:forEach items="${menus}" var="menu">
    <ul id="${menu.id}" class="associationCategory">
        <li class="menu">
            <span class="name">${menu.name}</span>
            <span class="status"><kantega:label key="aksess.organizesubpages.status"/></span>
            <span class="lastModified"><kantega:label key="aksess.organizesubpages.lastmodified"/></span>
            <span class="publisher"><kantega:label key="aksess.organizesubpages.publisher"/></span>
            <span class="views"><kantega:label key="aksess.organizesubpages.views"/></span>
        </li>
        <c:forEach var="page" items="${menu.subPages}"><%-- The page variable is an instance of Content --%>
            <li id="${page.association.id}" class="page">
                <span class="name">${page.title}</span>
                <c:choose>
                    <c:when test="${page.status.typeAsInt == 'PUBLISHED'}">
                        <c:set var="statusId" value="${page.status.typeAsInt}_${page.visibilityStatus}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="statusId" value="${page.status.typeAsInt}"/>
                    </c:otherwise>
                </c:choose>
                <span class="contentStatus"><span class="description status${statusId}"><kantega:label key="aksess.versions.status.${statusId}"/></span></span>
                <span class="lastModified"><fmt:formatDate value="${page.lastModified}" pattern="<%=Aksess.getDefaultDatetimeFormat()%>"/></span>
                <aksess:getuser name="publisher" userid="${page.publisher}"/>
                <span class="publisher">${publisher.name}</span>
                <span class="views">${page.association.numberOfViews}</span>
            </li>
        </c:forEach>
    </ul>
</c:forEach>
