<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="no.kantega.publishing.common.ao.LinkAO"%>
<%@ page import="no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler"%>
<%@ page import="no.kantega.publishing.modules.linkcheck.check.LinkOccurrence"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="java.util.Date" %>
<%@ page import="no.kantega.publishing.common.data.Site" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.common.cache.SiteCache" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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

    final List list = new ArrayList();

    String sort = param.getString("sort");

    LinkAO.doForEachLinkOccurrence(siteId, sort, new LinkOccurrenceHandler() {
        public void handleLinkOccurrence(LinkOccurrence linkOccurrence) {
            list.add(linkOccurrence);
        }
    });

    pageContext.setAttribute("list", list);
    SimpleDateFormat format = new SimpleDateFormat(Aksess.getDefaultDateFormat());
    pageContext.setAttribute("format", format);

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>info.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0">
        <tr>
            <td colspan="5">
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.linkcheck.help"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="5">
                <table border="0" cellspacing="0" cellpadding="2">
                    <tr>
                        <td>
                            <b><kantega:label key="aksess.statistics.velgsite"/>:</b>
                        </td>
                        <td>
                            <form name="filterform" action="index.jsp" method="get">
                                <select name="siteId" onchange="document.filterform.submit()">
                                    <option value="-1"> </option>
                                <%
                                    for (int i = 0; i < sites.size(); i++) {
                                        Site site = (Site)sites.get(i);
                                %>
                                    <option value="<%=site.getId()%>" <%if (siteId == site.getId()) out.write(" selected");%>><%=site.getName()%></option>
                                <%
                                    }
                                %>
                                </select>
                            </form>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="tableHeading">
            <td><a href="?sort=title&amp;siteId=<%=siteId%>"><kantega:label key="aksess.linkcheck.page"/></a></td>
            <td><a href="?sort=title&amp;siteId=<%=siteId%>"><kantega:label key="aksess.linkcheck.field"/></a></td>
            <td><a href="?sort=url&amp;siteId=<%=siteId%>"><kantega:label key="aksess.linkcheck.url"/></a></td>
            <td><a href="?sort=status&amp;siteId=<%=siteId%>"><kantega:label key="aksess.linkcheck.status"/></a></td>

            <td><a href="?sort=lastchecked&amp;siteId=<%=siteId%>"><kantega:label key="aksess.linkcheck.lastchecked"/></a></td>
            <td><a href="?sort=timeschecked&amp;siteId=<%=siteId%>"><kantega:label key="aksess.linkcheck.timeschecked"/></a></td>
        </tr>
    <c:forEach var="link" items="${list}" varStatus="status">
        <tr class="tableRow<c:out value="${status.index mod 2}"/>" valign="top">
            <td>
                <a href="../index.jsp?contentId=<c:out value="${link.contentId}"/>" target="_top"><c:out value="${link.contentTitle}"/></a>
            </td>
            <td>
                <c:if test="${link.attributeName != null}">
                    <c:out value="${link.attributeName}"/>
                </c:if>
            </td>
            <c:set var="url" value="${link.url}"/>

            <%
                    String url = (String) pageContext.getAttribute("url");
                    if(url.startsWith(Aksess.VAR_WEB)) {
                        url = Aksess.getContextPath() + url.substring(Aksess.VAR_WEB.length());
                    }
            %>
            <td><a target="external" href="<%=url%>">

                <%= url.length() > 40 ? url.substring(0, 40) +"..." : url%>
            </a></td>
            <td>
                <c:choose>
                    <c:when test="${link.status == 2}"><kantega:label key="aksess.linkcheck.statuses.2"/></c:when>
                    <c:when test="${link.status == 3}">
                        <c:choose>
                            <c:when test="${link.httpStatus == 401}"><kantega:label key="aksess.linkcheck.httpstatus.401"/></c:when>
                            <c:when test="${link.httpStatus == 404}"><kantega:label key="aksess.linkcheck.httpstatus.404"/></c:when>
                            <c:when test="${link.httpStatus == 500}"><kantega:label key="aksess.linkcheck.httpstatus.500"/></c:when>
                            <c:otherwise>HTTP <c:out value="${link.httpStatus}"/></c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:when test="${link.status == 4}"><kantega:label key="aksess.linkcheck.statuses.4"/></c:when>
                    <c:when test="${link.status == 5}"><kantega:label key="aksess.linkcheck.statuses.5"/></c:when>
                    <c:when test="${link.status == 6}"><kantega:label key="aksess.linkcheck.statuses.6"/></c:when>
                    <c:when test="${link.status == 7}"><kantega:label key="aksess.linkcheck.statuses.7"/></c:when>
                    <c:when test="${link.status == 8}"><kantega:label key="aksess.linkcheck.statuses.8"/></c:when>
                    <c:when test="${link.status == 9}"><kantega:label key="aksess.linkcheck.statuses.9"/></c:when>
                    <c:when test="${link.status == 10}"><kantega:label key="aksess.linkcheck.statuses.10"/></c:when>
                    <c:when test="${link.status == 11}"><kantega:label key="aksess.linkcheck.statuses.11"/></c:when>
                    <c:otherwise><c:out value="${link.status}"/></c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:if test="${link.lastChecked != null}">
                    <c:set var="dato" value="${link.lastChecked}"/>
                    <%=format.format((Date)pageContext.getAttribute("dato"))%>
                </c:if>
            </td>
            <td>
                <c:out value="${link.timesChecked}"/>
            </td>
        </tr>
    </c:forEach>
</table>
<%@ include file="../include/jsp_footer.jsf" %>
