<%@ page import="javax.tools.Diagnostic" %>
<%@ page import="javax.tools.JavaFileObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <style type="text/css">
        body {
        font-family: sans-serif;
        }
        .error {
            color:red;
        }
    </style>
</head>
<body>
<h1>Exception loading plugin</h1>

<pre>
    <%
        Throwable t = (Throwable) request.getAttribute("exception");
       t.printStackTrace(new PrintWriter(out));
    %>
</pre>
</body>
</html>
