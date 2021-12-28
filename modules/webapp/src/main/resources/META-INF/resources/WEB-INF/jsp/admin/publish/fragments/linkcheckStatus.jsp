<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<c:choose>
    <c:when test="${linkStatus == 'UNKNOWN_HOST'}"><kantega:label
            key="aksess.linkcheck.statuses.2"/></c:when>
    <c:when test="${linkStatus == 'HTTP_NOT_200'}">
        <c:choose>
            <c:when test="${link.httpStatus == 401}"><kantega:label
                    key="aksess.linkcheck.httpstatus.401"/></c:when>
            <c:when test="${link.httpStatus == 404}"><kantega:label
                    key="aksess.linkcheck.httpstatus.404"/></c:when>
            <c:when test="${link.httpStatus == 500}"><kantega:label
                    key="aksess.linkcheck.httpstatus.500"/></c:when>
            <c:otherwise>HTTP ${link.httpStatus}</c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${linkStatus == 'IO_EXCEPTION'}"><kantega:label
            key="aksess.linkcheck.statuses.4"/></c:when>
    <c:when test="${linkStatus == 'CONNECTION_TIMEOUT'}"><kantega:label
            key="aksess.linkcheck.statuses.5"/></c:when>
    <c:when test="${linkStatus == 'CIRCULAR_REDIRECT'}"><kantega:label
            key="aksess.linkcheck.statuses.6"/></c:when>
    <c:when test="${linkStatus == 'CONNECT_EXCEPTION'}"><kantega:label
            key="aksess.linkcheck.statuses.7"/></c:when>
    <c:when test="${linkStatus == 'CONTENT_AP_NOT_FOUND'}"><kantega:label
            key="aksess.linkcheck.statuses.8"/></c:when>
    <c:when test="${linkStatus == 'INVALID_URL'}"><kantega:label
            key="aksess.linkcheck.statuses.9"/></c:when>
    <c:when test="${linkStatus == 'ATTACHMENT_AP_NOT_FOUND'}"><kantega:label key="aksess.linkcheck.statuses.10"/></c:when>
    <c:when test="${linkStatus == 'MULTIMEDIA_AP_NOT_FOUND'}"><kantega:label key="aksess.linkcheck.statuses.11"/></c:when>
    <c:otherwise>${linkStatus}</c:otherwise>
</c:choose>
