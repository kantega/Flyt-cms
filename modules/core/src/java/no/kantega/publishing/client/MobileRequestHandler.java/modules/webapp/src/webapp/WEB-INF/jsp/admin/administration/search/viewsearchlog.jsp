<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <kantega:label key="aksess.search.log.title"/>
</kantega:section>

<kantega:section id="content">
    <admin:box>
        <h1><kantega:label key="aksess.search.log.title"/></h1>

        <form name="myform" action="" method="get" style="margin-bottom: 20px">
            <strong><kantega:label key="aksess.search.log.site"/>:</strong>
            <select name="siteId" onchange="document.myform.submit()">
                <c:forEach items="${sites}" var="site">
                    <option value="${site.id}" <c:if test="${site.id == selectedSiteId}"> selected</c:if>>${site.name}</option>
                </c:forEach>
            </select>
        </form>

        <table class="fullWidth">
            <thead>
            <tr>
                <th colspan="2"><kantega:label key="aksess.search.log.summary.title"/></th>
            </tr>
            </thead>
            <tbody>
            <tr class="tableRow0">
                <td><kantega:label key="aksess.search.log.summary.now"/></td>
                <td class="number">${last30min}</td>
            </tr>
            <tr class="tableRow0">
                <td><kantega:label key="aksess.search.log.summary.lastmonth"/></td>
                <td class="number">${sumAllTime}</td>
            </tr>
            </tbody>
        </table>

        <table class="fullWidth">
            <thead>
            <tr>
                <th>&nbsp;</th>
                <th><kantega:label key="aksess.search.log.query.mostpopular"/></th>
                <th class="number"><kantega:label key="aksess.search.log.query.hits"/></th>
                <th class="number"><kantega:label key="aksess.search.log.query.searches"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${most}" var="q" varStatus="status">
                <tr  class="tableRow<c:out value="${status.index mod 2}"/>">
                    <td><c:out value="${status.index + 1}"/></td>
                    <td><c:out value="${q.query}"/></td>
                    <td class="number"><fmt:formatNumber value="${q.numberOfHits}" maxFractionDigits="2"/></td>
                    <td class="number"><c:out value="${q.numberOfSearches}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <table class="fullWidth">
            <thead>
            <tr>
                <th>&nbsp;</th>
                <th><kantega:label key="aksess.search.log.query.leasthits"/></th>
                <th class="number"><kantega:label key="aksess.search.log.query.hits"/></th>
                <th class="number"><kantega:label key="aksess.search.log.query.searches"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${least}" var="q" varStatus="status">
                <tr class="tableRow<c:out value="${status.index mod 2}"/>">
                    <td><c:out value="${status.index + 1}"/></td>
                    <td><c:out value="${q.query}"/></td>
                    <td class="number"><fmt:formatNumber value="${q.numberOfHits}" maxFractionDigits="2"/></td>
                    <td class="number"><c:out value="${q.numberOfSearches}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="ui-state-highlight"><kantega:label key="aksess.search.log.help"/></div>

    </admin:box>
</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>