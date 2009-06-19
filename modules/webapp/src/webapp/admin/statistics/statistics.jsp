<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier" %>
<%@ page import="no.kantega.publishing.common.data.TrafficLogQuery" %>
<%@ page import="no.kantega.publishing.common.data.enums.TrafficOrigin" %>
<%@ page import="no.kantega.publishing.common.service.TrafficStatisticsService" %>
<%@ page import="java.util.List" %>
<%@ include file="../include/jsp_header.jsf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    TrafficStatisticsService trafficService = new TrafficStatisticsService();

    RequestParameters param = new RequestParameters(request, "utf-8");
    ContentIdentifier cid = new ContentIdentifier();
    cid.setContentId(param.getInt("contentId"));
    cid.setLanguage(param.getInt("language"));

    int intHits = -1;
    int extHits = -1;
    int sumHits = -1;

    int intSessions = -1;
    int extSessions = -1;
    int sumSessions = -1;

    TrafficLogQuery query = new TrafficLogQuery();
    query.setCid(cid);

    if (Aksess.getInternalIpSegment() != null) {
        // Skiller mellom interne og eksterne visninger
        query.setTrafficOrigin(TrafficOrigin.INTERNAL);
        intHits = trafficService.getNumberOfVisitsInPeriod(query);

        query.setTrafficOrigin(TrafficOrigin.EXTERNAL);
        extHits = trafficService.getNumberOfVisitsInPeriod(query);
        sumHits = intHits + extHits;

        query.setTrafficOrigin(TrafficOrigin.INTERNAL);
        intSessions = trafficService.getNumberOfSessionsInPeriod(query);

        query.setTrafficOrigin(TrafficOrigin.EXTERNAL);
        extSessions = trafficService.getNumberOfSessionsInPeriod(query);
        sumSessions = intSessions + extSessions;

    } else {
        query.setTrafficOrigin(TrafficOrigin.ALL_USERS);
        sumHits = trafficService.getNumberOfVisitsInPeriod(query);
        sumSessions = trafficService.getNumberOfVisitsInPeriod(query);

    }

    query.setTrafficOrigin(TrafficOrigin.ALL_USERS);

    List topReferers = trafficService.getReferersInPeriod(query);
    List topReferingHosts = trafficService.getReferingHostsInPeriod(query);
    List topReferingQueries = trafficService.getReferingQueriesInPeriod(query);

    pageContext.setAttribute("topReferers", topReferers);
    pageContext.setAttribute("topReferingHosts", topReferingHosts);
    pageContext.setAttribute("topReferingQueries", topReferingQueries);

%>
<html>
<head>
<title><kantega:label key="aksess.statistics.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body>
    <img src="../bitmaps/blank.gif" width="200" height="10"><br>
    <table border="0" cellspacing="0" cellpadding="0" width="265" align="center">
        <tr>
            <td class="box">
                <table border="0" cellspacing="2" cellpadding="2" align="center" width="100%">
                    <tr>
                        <td class="inpHeading"><b><kantega:label key="aksess.statistics.hits.title"/></b></td>
                    </tr>
                    <tr>
                        <td>
                            <table border="0" width="100%">
                            <%
                                if (Aksess.getInternalIpSegment() != null) {
                            %>
                                <tr>
                                    <td><kantega:label key="aksess.statistics.hits.eksternt"/></td>
                                    <td align="right"><%=extHits%></td>
                                </tr>
                                <tr>
                                    <td><kantega:label key="aksess.statistics.hits.internt"/></td>
                                    <td align="right"><%=intHits%></td>
                                </tr>
                            <%
                                }
                            %>
                                <tr>
                                    <td><kantega:label key="aksess.statistics.hits.sum"/></td>
                                    <td align="right"><%=sumHits%></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="inpHeading"><b><kantega:label key="aksess.statistics.sessions.title"/></b></td>
                    </tr>
                    <tr>
                        <td>
                            <table border="0" width="100%">
                            <%
                                if (Aksess.getInternalIpSegment() != null) {
                            %>
                                <tr>
                                    <td><kantega:label key="aksess.statistics.sessions.eksternt"/></td>
                                    <td align="right"><%=extSessions%></td>
                                </tr>
                                <tr>
                                    <td><kantega:label key="aksess.statistics.sessions.internt"/></td>
                                    <td align="right"><%=intSessions%></td>
                                </tr>
                            <%
                                }
                            %>
                                <tr>
                                    <td><kantega:label key="aksess.statistics.sessions.sum"/></td>
                                    <td align="right"><%=sumSessions%></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class=helpText><kantega:label key="aksess.statistics.hjelp"/></div>
                        </td>
                    </tr>
                    <table border="0" cellspacing="2" cellpadding="2" align="center" width="100%">
                        <tr>
                            <td class="inpHeading"><b><kantega:label key="aksess.statistics.topreferers"/></b></td>
                        </tr>
                        <tr>
                            <td>
                               <table border="0" width="100%">
                                   <c:forEach items="${topReferers}" var="ref">
                                       <tr>
                                           <td>
                                               <a target="refererwindow" href="<c:out value="${ref.referer}"/>"><c:out value="${ref.refererShort}"/></a>
                                           </td>
                                           <td>
                                               <c:out value="${ref.occurrences}"/>
                                           </td>
                                       </tr>
                                   </c:forEach>
                               </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="inpHeading"><b><kantega:label key="aksess.statistics.topreferinghosts"/></b></td>
                        </tr>
                        <tr>
                            <td>
                               <table border="0" width="100%">
                                   <c:forEach items="${topReferingHosts}" var="ref">
                                       <tr>
                                           <td>
                                               <c:out value="${ref.referer}"/>
                                           </td>
                                           <td>
                                               <c:out value="${ref.occurrences}"/>
                                           </td>
                                       </tr>
                                   </c:forEach>
                               </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="inpHeading"><b><kantega:label key="aksess.statistics.topreferingqueries"/></b></td>
                        </tr>
                        <tr>
                            <td>
                               <table border="0" width="100%">
                                   <c:forEach items="${topReferingQueries}" var="ref">
                                       <tr>
                                           <td>
                                               <c:out value="${ref.referer}"/>
                                           </td>
                                           <td>
                                               <c:out value="${ref.occurrences}"/>
                                           </td>
                                       </tr>
                                   </c:forEach>
                               </table>
                            </td>
                        </tr>
                    </table>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>