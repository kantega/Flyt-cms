<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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

<div class="fieldset">
    <fieldset>
        <div id="tabs" class="ui-tabs ui-widget ui-corner-all">
            <ul class="ui-widget-header ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-corner-all">
                <li class="ui-tabs-selected ui-state-active ui-corner-top"><a href="#" id="PageStatistics"><kantega:label key="aksess.statistics.page"/></a></li>
                <li class="ui-state-default ui-corner-top"><a href="#" id="TotalStatistics"><kantega:label key="aksess.statistics.total"/></a></li>
            </ul>
            <div>
                <h1><kantega:label key="aksess.statistics.hits.title"/></h1>
                <table border="0" width="100%">
                    <c:if test="${showInternalAndExternal}">
                        <tr>
                            <td><kantega:label key="aksess.statistics.hits.eksternt"/></td>
                            <td class="number">${extHits}</td>
                        </tr>
                        <tr>
                            <td><kantega:label key="aksess.statistics.hits.internt"/></td>
                            <td class="number">${intHits}</td>

                        </tr>
                    </c:if>
                    <tr>
                        <td><kantega:label key="aksess.statistics.hits.sum"/></td>
                        <td class="number">${sumHits}</td>
                    </tr>
                </table>

                <h1><kantega:label key="aksess.statistics.sessions.title"/></h1>
                <table border="0" width="100%">
                    <c:if test="${showInternalAndExternal}">
                        <tr>
                            <td><kantega:label key="aksess.statistics.sessions.eksternt"/></td>
                            <td class="number">${extSessions}</td>
                        </tr>
                        <tr>
                            <td><kantega:label key="aksess.statistics.sessions.internt"/></td>
                            <td class="number">${intSessions}</td>
                        </tr>
                    </c:if>
                    <tr>
                        <td><kantega:label key="aksess.statistics.sessions.sum"/></td>
                        <td class="number">${sumSessions}</td>
                    </tr>
                </table>
                <div class="ui-state-highlight"><kantega:label key="aksess.statistics.hjelp"/></div>

                <c:if test="${not empty topReferers}">
                    <div class="fieldset">
                        <fieldset>
                            <h1><kantega:label key="aksess.statistics.topreferers"/></h1>
                            <table border="0" width="100%">
                                <c:forEach items="${topReferers}" var="ref">
                                    <tr>
                                        <td>
                                            <a target="refererwindow" href="<c:out value="${ref.referer}"/>"><c:out value="${ref.refererShort}"/></a>
                                        </td>
                                        <td class="number">${ref.occurrences}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </fieldset>
                    </div>
                </c:if>
                <c:if test="${not empty topReferingHosts}">
                    <h1><kantega:label key="aksess.statistics.topreferinghosts"/></h1>
                    <table border="0" width="100%">
                        <c:forEach items="${topReferingHosts}" var="ref">
                            <tr>
                                <td><c:out value="${ref.referer}"/></td>
                                <td class="number">${ref.occurrences}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>
                <c:if test="${not empty topReferingQueries}">
                    <h1><kantega:label key="aksess.statistics.topreferingqueries"/></h1>
                    <table border="0" width="100%">
                        <c:forEach items="${topReferingQueries}" var="ref">
                            <tr>
                                <td><c:out value="${ref.referer}"/></td>
                                <td class="number">${ref.occurrences}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>
            </div>
        </div>

    </fieldset>
</div>
