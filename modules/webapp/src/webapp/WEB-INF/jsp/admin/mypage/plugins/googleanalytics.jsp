<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
    function googleAnalyticsCallback() {
        drawPerMonthStatsChart();
    }

    function getMonthName(month) {
        var monthNames = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
        var monthIdx = Number(month.substr(0, 2));
        return monthNames[monthIdx-1];
    }

    function drawPerMonthStatsChart() {
        var chart;
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Month');
        data.addColumn('number', 'Visits');
        data.addColumn('number', 'Pageviews');
        data.addRows(12);
        <c:forEach items="${usage.perMonthStats}" var="entry" varStatus="status">
            data.setValue(<c:out value="${status.index}"/>, 0, getMonthName('<c:out value="${entry.month}${entry.year}"/>'));
            data.setValue(<c:out value="${status.index}"/>, 1, <c:out value="${entry.visits}"/>);
            data.setValue(<c:out value="${status.index}"/>, 2, <c:out value="${entry.pageviews}"/>);
        </c:forEach>

        chart = new google.visualization.AreaChart(document.getElementById('permonthstats_div'));
        chart.draw(data, {width: 450, height: 280, legend: 'bottom', title: 'Last 12 months'});
    }
</script>


<c:choose>
    <c:when test="${not empty errorMsg}">
        <p><c:out value="${errorMsg}"/></p>
    </c:when>
    <c:otherwise>
        <div id="permonthstats_div"></div>

        <h2>Last month</h2>
        <ul>
            <li><c:out value="${usage.visits}"/> Visits</li>
            <li><c:out value="${usage.pageviews}"/> Pageviews</li>
            <li><c:out value="${usage.pageviews / usage.visits}"/> Pages/Visit</li>
        </ul>

        <h2>Top 10 pages</h2>
        <table class="fullWidth">
            <tr>
                <th><strong>Path</strong></th>
                <th><strong>Title</strong></th>
                <th class="number"><strong>Pageviews</strong></th>
            </tr>

            <c:forEach items="${pageviews}" var="entry">
                <tr class="tableRow${status.index mod 2}">
                    <td><a href="<aksess:geturl/><c:out value="${entry.path}"/>" target="_top"><c:out value="${entry.path}"/></a></td>
                    <td><c:out value="${entry.title}"/></td>
                    <td class="number"><c:out value="${entry.views}"/></td>
                </tr>
            </c:forEach>
        </table>

        <h2>Top 5 browsers</h2>
        <table class="fullWidth">
            <tr>
                <th><strong>Browser</strong></th>
                <th class="number"><strong>Visits</strong></th>
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
