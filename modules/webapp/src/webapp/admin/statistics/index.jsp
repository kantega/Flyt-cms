<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.ao.TrafficLogAO,
                 no.kantega.publishing.common.cache.SiteCache,
                 no.kantega.publishing.common.data.ContentViewStatistics"%>
<%@ page import="no.kantega.publishing.common.data.PeriodViewStatistics"%>
<%@ page import="no.kantega.publishing.common.data.Site"%>
<%@ page import="no.kantega.publishing.common.data.TrafficLogQuery"%>
<%@ page import="no.kantega.publishing.common.data.enums.TrafficOrigin"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.List" %>
<%@ include file="../include/jsp_header.jsf" %>
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

<%
    List sites = SiteCache.getSites();

    RequestParameters param = new RequestParameters(request, "utf-8");
    int siteId = param.getInt("siteId");
    if (siteId == -1) {
        if (sites.size() > 0) {
            siteId = ((Site)sites.get(0)).getId();
        }
    }
    DecimalFormat myFormatter = new DecimalFormat("###.##");

    int trafficOrigin = param.getInt("origin");
    if (trafficOrigin == -1) {
        trafficOrigin = TrafficOrigin.ALL_USERS;
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>security/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">

    <form name="myform" action="index.jsp" method="get">
        <table border="0" cellspacing="0" cellpadding="2">
            <tr>
                <td>
                    <b><kantega:label key="aksess.statistics.velgsite"/>:</b>
                </td>
                <td>
                    <select name="siteId" onchange="document.myform.submit()">
                    <%
                        for (int i = 0; i < sites.size(); i++) {
                            Site site = (Site)sites.get(i);
                    %>
                        <option value="<%=site.getId()%>" <%if (siteId == site.getId()) out.write(" selected");%>><%=site.getName()%></option>
                    <%
                        }
                    %>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <b><kantega:label key="aksess.statistics.visbesok"/>:</b>
                </td>
                <td>
                    <select name="origin" onchange="document.myform.submit()">
                        <option value="<%=TrafficOrigin.ALL_USERS%>" <%if (trafficOrigin == TrafficOrigin.ALL_USERS) {%> selected<%}%>><kantega:label key="aksess.statistics.allusers"/></option>
                        <%
                            if (Aksess.getInternalIpSegment() != null && Aksess.getInternalIpSegment().length > 0) {
                        %>
                            <option value="<%=TrafficOrigin.INTERNAL%>" <%if (trafficOrigin == TrafficOrigin.INTERNAL) {%> selected<%}%>><kantega:label key="aksess.statistics.internalusers"/></option>
                            <option value="<%=TrafficOrigin.EXTERNAL%>" <%if (trafficOrigin == TrafficOrigin.EXTERNAL) {%> selected<%}%>><kantega:label key="aksess.statistics.externalusers"/></option>
                        <%
                            }
                        %>
                        <option value="<%=TrafficOrigin.SEARCH_ENGINES%>" <%if (trafficOrigin == TrafficOrigin.SEARCH_ENGINES) {%> selected<%}%>"><kantega:label key="aksess.statistics.searchengines"/></option>
                        <option value="<%=TrafficOrigin.ALL%>" <%if (trafficOrigin == TrafficOrigin.ALL) {%> selected<%}%>><kantega:label key="aksess.statistics.all"/></option>
                    </select>
                </td>
            </tr>
        </table>


    </form>

    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr class="tableHeading">
            <td colspan="2"><b><kantega:label key="aksess.statistics.summary.title"/></b></td>
        </tr>
        <%
            Calendar cal = new GregorianCalendar();
            Date now = cal.getTime();
            cal.add(Calendar.MINUTE, -30);
            Date before = cal.getTime();

            TrafficLogQuery queryNow = new TrafficLogQuery();
            queryNow.setSiteId(siteId);
            queryNow.setStart(before);
            queryNow.setEnd(now);
            queryNow.setTrafficOrigin(trafficOrigin);

            int sumSessionsNow = TrafficLogAO.getTotalNumberOfHitsOrSessionsInPeriod(queryNow, true);
            int sumHitsNow = TrafficLogAO.getTotalNumberOfHitsOrSessionsInPeriod(queryNow, false);

            TrafficLogQuery queryAll = new TrafficLogQuery();
            queryAll.setSiteId(siteId);
            queryAll.setTrafficOrigin(trafficOrigin);


            int sumHits = TrafficLogAO.getTotalNumberOfHitsOrSessionsInPeriod(queryAll, false);
            int sumSessions = TrafficLogAO.getTotalNumberOfHitsOrSessionsInPeriod(queryAll, true);

            double avgHitsPerSession = 0;
            if (sumSessions > 0) {
                avgHitsPerSession = ((double)sumHits)/((double)sumSessions);
            }
        %>
        <tr class="tableRow0">
            <td><kantega:label key="aksess.statistics.summary.hitsnow"/></td>
            <td align="right"><%=sumHitsNow%></td>
        </tr>
        <tr class="tableRow1">
            <td><kantega:label key="aksess.statistics.summary.sessionsnow"/></td>
            <td align="right"><%=sumSessionsNow%></td>
        </tr>
        <tr class="tableRow0">
            <td><kantega:label key="aksess.statistics.summary.hits"/></td>
            <td align="right"><%=sumHits%></td>
        </tr>
        <tr class="tableRow1">
            <td><kantega:label key="aksess.statistics.summary.sessions"/></td>
            <td align="right"><%=sumSessions%></td>
        </tr>
        <tr class="tableRow0">
            <td><kantega:label key="aksess.statistics.summary.avghitspersession"/></td>
            <td align="right"><%=myFormatter.format(avgHitsPerSession)%></td>
        </tr>
    </table>
    <p>&nbsp;</p>
    <%
        TrafficLogQuery query = new TrafficLogQuery();
        query.setSiteId(siteId);
        query.setTrafficOrigin(trafficOrigin);

        if (sumHits > 0) {
            List contentViewStats = TrafficLogAO.getMostVisitedContentStatistics(query, 50);
    %>
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr class="tableHeading">
            <td width="25">&nbsp;</td>
            <td width="225"><b><kantega:label key="aksess.statistics.pageviews.page"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.statistics.pageviews.noviews"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.statistics.pageviews.percent"/></b></td>
        </tr>
        <%
            for (int i = 0; i < contentViewStats.size(); i++) {
                ContentViewStatistics stat = (ContentViewStatistics)contentViewStats.get(i);
        %>
        <tr class="tableRow<%=(i%2)%>">
            <td><%=(i+1)%>.</td>
            <td><a href="<%=stat.getUrl()%>" target="_new"><%=stat.getTitle()%></a></td>
            <td align="right"><%=stat.getNoPageViews()%></td>
            <td align="right"><%=myFormatter.format(((double)stat.getNoPageViews()*100)/sumHits)%> %</td>
        </tr>
        <%
            }
        %>

    </table>
    <p>&nbsp;</p>
    <%
        List dateViewStatistics = TrafficLogAO.getPeriodViewStatistics(query, java.util.Calendar.DATE);
    %>
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr class="tableHeading">
            <td width="250"><b><kantega:label key="aksess.statistics.pageviews.day"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.statistics.pageviews.noviews"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.statistics.pageviews.percent"/></b></td>
        </tr>
        <%
            for (int i = 0; i < dateViewStatistics.size(); i++) {
                PeriodViewStatistics stat = (PeriodViewStatistics)dateViewStatistics.get(i);
        %>
        <tr class="tableRow<%=(i%2)%>">
            <td><%=stat.getPeriod()%></td>
            <td align="right"><%=stat.getNoPageViews()%></td>
            <td align="right"><%=myFormatter.format(((double)stat.getNoPageViews()*100)/sumHits)%> %</td>
        </tr>
        <%
            }
        %>

    </table>
    <p>&nbsp;</p>
    <%
        List hourViewStatistics = TrafficLogAO.getPeriodViewStatistics(query, java.util.Calendar.HOUR);
    %>
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr class="tableHeading">
            <td width="250"><b><kantega:label key="aksess.statistics.pageviews.hour"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.statistics.pageviews.noviews"/></b></td>
            <td width="75" align="right"><b><kantega:label key="aksess.statistics.pageviews.percent"/></b></td>
        </tr>
        <%
            for (int i = 0; i < hourViewStatistics.size(); i++) {
                PeriodViewStatistics stat = (PeriodViewStatistics)hourViewStatistics.get(i);
        %>
        <tr class="tableRow<%=(i%2)%>">
            <td><%=stat.getPeriod()%>:00 - <%=stat.getPeriod()%>:59</td>
            <td align="right"><%=stat.getNoPageViews()%></td>
            <td align="right"><%=myFormatter.format(((double)stat.getNoPageViews()*100)/sumHits)%> %</td>
        </tr>
        <%
                }
            }
        %>
    </table>

    <p>&nbsp;</p>
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr>
            <td>
                <div class=helpText><kantega:label key="aksess.statistics.hjelp"/></div>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>