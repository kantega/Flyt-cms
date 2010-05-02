<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<script type="text/javascript">
    function drawPerMonthStatsChart() {
        var chart;
        var data = new google.visualization.DataTable();
        data.addColumn('string', '<kantega:label key="aksess.googleanalytics.month"/>');
        data.addColumn('number', '<kantega:label key="aksess.googleanalytics.visits"/>');
        data.addColumn('number', '<kantega:label key="aksess.googleanalytics.pageviews"/>');
        data.addRows(${fn:length(usage.perMonthStats)});
        <c:forEach items="${usage.perMonthStats}" var="entry" varStatus="status">
            data.setValue(<c:out value="${status.index}"/>, 0, getMonthName('<c:out value="${entry.month}"/>'));
            data.setValue(<c:out value="${status.index}"/>, 1, <c:out value="${entry.visits}"/>);
            data.setValue(<c:out value="${status.index}"/>, 2, <c:out value="${entry.pageviews}"/>);
        </c:forEach>

        chart = new google.visualization.AreaChart(document.getElementById('permonthstats_div'));
        chart.draw(data, {width: 350, height: 200, legend: 'bottom', title: '<kantega:label key="aksess.googleanalytics.permonthstats"/>'});
    }
</script>

<c:choose>
    <c:when test="${not empty errorMsg}">
        <p><c:out value="${errorMsg}"/></p>
    </c:when>
    <c:otherwise>
        <h3><c:out value="${profile.name}"/></h3>

        <div id="permonthstats_div"></div>

        <h4><kantega:label key="aksess.googleanalytics.lastmonth"/></h4>
        <ul>
            <li><c:out value="${usage.visits}"/> <kantega:label key="aksess.googleanalytics.visits"/></li>
            <li><c:out value="${usage.pageviews}"/> <kantega:label key="aksess.googleanalytics.pageviews"/></li>
            <li><fmt:formatNumber value="${usage.pageviews/usage.visits}" minFractionDigits="2" maxFractionDigits="2"/> <kantega:label key="aksess.googleanalytics.pages"/>/<kantega:label key="aksess.googleanalytics.visits"/></li>
        </ul>

        <h4><kantega:label key="aksess.googleanalytics.toppages"/></h4>
        <table class="fullWidth">
            <tr>
                <th><strong><kantega:label key="aksess.googleanalytics.path"/></strong></th>
                <th><strong><kantega:label key="aksess.googleanalytics.pagetitle"/></strong></th>
                <th class="number"><strong><kantega:label key="aksess.googleanalytics.pageviews"/></strong></th>
            </tr>

            <c:forEach items="${pageviews}" var="entry">
                <tr class="tableRow${status.index mod 2}">
                    <td>
                        <a href="<aksess:geturl/><c:out value="${entry.path}"/>" target="_top">
                            <c:choose>
                                <c:when test="${fn:length(entry.path) gt 20}">
                                    ${fn:substring(entry.path, 0, 19)}...
                                </c:when>
                                <c:otherwise>
                                    ${entry.path}
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </td>
                    <td><c:out value="${entry.title}"/></td>
                    <td class="number"><c:out value="${entry.views}"/></td>
                </tr>
            </c:forEach>
        </table>

        <h4><kantega:label key="aksess.googleanalytics.topbrowsers"/></h4>
        <table class="fullWidth">
            <tr>
                <th><strong><kantega:label key="aksess.googleanalytics.browser"/></strong></th>
                <th class="number"><strong><kantega:label key="aksess.googleanalytics.visits"/></strong></th>
            </tr>

            <c:forEach items="${usage.browsers}" var="browser">
                <tr>
                    <td><c:out value="${browser.key}"/></td>
                    <td class="number"><c:out value="${browser.value}"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
