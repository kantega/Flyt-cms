<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:if test="${pageContext.request.method eq 'POST' or pageContext.request.method eq 'post'}">
    <%
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        long threadToKill = Long.parseLong(request.getParameter("thread"));
        for (Thread thread : threads) {
            if(thread.getId() == threadToKill) {
                out.println("Tried to kill thread " + thread.getName());
                thread.interrupt();
                break;
            }
        }
    %>

</c:if>

<%
    Set<Thread> threads = Thread.getAllStackTraces().keySet();
    request.setAttribute("threads", threads);
%>
<ul>
<c:forEach items="${threads}" var="thread">
    <li>${thread.name}
        <form action="" method="post">
            <input type="hidden" name="thread" value="${thread.id}">
            <button type="submit">Kill</button>
        </form>
    <ul>
        <c:forEach items="${thread.stackTrace}" var="frame">
            <li>${frame.className}.${frame.methodName}:${frame.lineNumber}</li>
        </c:forEach>
    </ul>
    </li>
</c:forEach>
</ul>
