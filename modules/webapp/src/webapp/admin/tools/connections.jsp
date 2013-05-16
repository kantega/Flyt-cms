<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>OpenAksess connections</title></head>
<body>
<%
    Map<Connection, StackTraceElement[]> connections = dbConnectionFactory.connections;

    Iterator<StackTraceElement[]> it = connections.values().iterator();
    int j = 0;
    while (it.hasNext()) {
        j++;
        out.write("<h1>Connection "+ j + "</h1>");
        out.write("<ul>");
        StackTraceElement[] stacktrace = it.next();
        for (StackTraceElement traceElement : stacktrace) {
            out.write("<li>");
            out.write(traceElement.toString());
            out.write("</li>");
        }
        out.write("</ul>");
    }
%>
</body>
</html>