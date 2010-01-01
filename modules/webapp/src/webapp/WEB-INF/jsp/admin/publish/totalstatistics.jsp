<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
<a href="#" id="PageStatistics"><kantega:label key="aksess.pagestatistics.title"/></a>
<div class="fieldset">
    <fieldset>
        <h1><kantega:label key="aksess.statistics.summary.title"/></h1>

        <table class="fullWidth">
            <tr class="tableRow0">
                <td><kantega:label key="aksess.statistics.summary.hitsnow"/></td>
                <td class="number">${sumHitsNow}</td>
            </tr>
            <tr class="tableRow1">
                <td><kantega:label key="aksess.statistics.summary.sessionsnow"/></td>
                <td class="number">${sumSessionsNow}</td>
            </tr>
            <tr class="tableRow0">
                <td><kantega:label key="aksess.statistics.summary.hits"/></td>
                <td class="number">${sumHits}</td>
            </tr>
            <tr class="tableRow1">
                <td><kantega:label key="aksess.statistics.summary.sessions"/></td>
                <td class="number">${sumHits}</td>
            </tr>
            <tr class="tableRow0">
                <td><kantega:label key="aksess.statistics.summary.avghitspersession"/></td>
                <td class="number"><fmt:formatNumber value="${avgHitsPerSession}" minFractionDigits="2" maxFractionDigits="2"/>%</td>
            </tr>
        </table>
    </fieldset>
</div>
<div class="fieldset">
    <fieldset>
        <h1><kantega:label key="aksess.statistics.pageviews.title"/></h1>
        <table class="fullWidth">
            <thead>
                <tr>
                    <th class="no">&nbsp;</th>
                    <th><kantega:label key="aksess.statistics.pageviews.page"/></th>
                    <th class="views number"><b><kantega:label key="aksess.statistics.pageviews.noviews"/></b></th>
                    <th class="percent number"><b><kantega:label key="aksess.statistics.pageviews.percent"/></b></th>
                </tr>
            </thead>
            <c:forEach var="stat" items="${contentViewStats}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td>${status.index + 1}</td>
                    <td><a href="${stat.url}" target="_new">${stat.title}</a></td>
                    <td class="number">${stat.noPageViews}</td>
                    <td class="number"><fmt:formatNumber value="${(stat.noPageViews*100)/sumHits}" minFractionDigits="2" maxFractionDigits="2"/>%</td>
                </tr>
            </c:forEach>
        </table>
        <table class="fullWidth" style="padding-top:20px">
            <thead>
                <tr>
                    <th class="no">&nbsp;</th>
                    <th><kantega:label key="aksess.statistics.pageviews.day"/></th>
                    <th class="views number"><b><kantega:label key="aksess.statistics.pageviews.noviews"/></b></th>
                    <th class="percent number"><b><kantega:label key="aksess.statistics.pageviews.percent"/></b></th>
                </tr>
            </thead>
            <c:forEach var="stat" items="${dateViewStatistics}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td>${status.index + 1}</td>
                    <td><a href="${stat.url}" target="_new">${stat.title}</a></td>
                    <td class="number">${stat.noPageViews}</td>
                    <td class="number"><fmt:formatNumber value="${(stat.noPageViews*100)/sumHits}" minFractionDigits="2" maxFractionDigits="2"/>%</td>
                </tr>
            </c:forEach>
        </table>
        <table class="fullWidth" style="padding-top:20px">
            <thead>
                <tr>
                    <th class="no">&nbsp;</th>
                    <th><kantega:label key="aksess.statistics.pageviews.hour"/></th>
                    <th class="views number"><b><kantega:label key="aksess.statistics.pageviews.noviews"/></b></th>
                    <th class="percent number"><b><kantega:label key="aksess.statistics.pageviews.percent"/></b></th>
                </tr>
            </thead>
            <c:forEach var="stat" items="${hourViewStatistics}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td>${status.index + 1}</td>
                    <td><a href="${stat.url}" target="_new">${stat.title}</a></td>
                    <td class="number">${stat.noPageViews}</td>
                    <td class="number"><fmt:formatNumber value="${(stat.noPageViews*100)/sumHits}" minFractionDigits="2" maxFractionDigits="2"/>%</td>
                </tr>
            </c:forEach>
        </table>
        <div class="helpText"><kantega:label key="aksess.statistics.hjelp"/></div>
    </fieldset>
</div>
