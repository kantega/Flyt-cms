<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
  ~ Copyright 2010 Kantega AS
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

<c:if test="${orgUnit != null}">
    <p>
        <kantega:label key="aksess.orgunitstatistics.orgunit" />: <b>${orgUnit.name}</b>
    </p>
</c:if>

 <c:choose>
    <c:when test="${not empty sumHitsNow}">
        <table class="fullWidth">
            <thead>
                <tr>
                    <th></th>
                    <th class="number"><kantega:label key="aksess.contentstatistics.count" /></th>
                </tr>
            </thead>
            <tbody>
                <tr class="tableRow0">
                    <td><kantega:label key="aksess.statistics.summary.hitsnow" /></td>
                    <td class="number">${sumHitsNow}</td>
                </tr>
                <tr class="tableRow1">
                    <td><kantega:label key="aksess.statistics.summary.sessionsnow" /></td>
                    <td class="number">${sumSessionsNow}</td>
                </tr>
                <tr class="tableRow0">
                    <td><kantega:label key="aksess.statistics.summary.hits" /></td>
                    <td class="number">${sumHits}</td>
                </tr>
                <tr class="tableRow1">
                    <td><kantega:label key="aksess.statistics.summary.sessions" /></td>
                    <td class="number">${sumHits}</td>
                </tr>
                <tr class="tableRow0">
                    <td><kantega:label key="aksess.statistics.summary.avghitspersession" /></td>
                    <td class="number"><fmt:formatNumber value="${avgHitsPerSession}" minFractionDigits="2" maxFractionDigits="2" /></td>
                </tr>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <kantega:label key="aksess.orgunitstatistics.notfound" />
    </c:otherwise>
</c:choose>
