<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
    <a href="${searchHit.url}"><c:out value="${searchHit.title}" escapeXml="false"/></a>
</div>
<c:choose>
    <c:when test="${searchHit.contextText != ''}">
        <c:out value="${searchHit.contextText}" escapeXml="false"/>
    </c:when>
    <c:otherwise><c:out value="${searchHit.summary}" escapeXml="false"/></c:otherwise>
</c:choose>

<c:if test="${searchHit.pathElements != null}">
    <div class="navigationpath">
        <c:forEach var="pathElement" items="${searchHit.pathElements}">
            <c:out value="${pathElement.title}" escapeXml="false"/> &gt;
        </c:forEach>
        <c:out value="${searchHit.title}" escapeXml="false"/>
    </div>
</c:if>