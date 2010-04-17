<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach var="topic" items="${topics}">
    ${topic.baseName}|${topic.topicMapId}:${topic.id}
</c:forEach>