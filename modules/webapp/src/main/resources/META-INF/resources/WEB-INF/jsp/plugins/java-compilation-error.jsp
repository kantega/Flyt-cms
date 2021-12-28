<%@ page import="javax.tools.Diagnostic" %>
<%@ page import="javax.tools.JavaFileObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
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
<h1>Ouch!!</h1>
    <%
        List<Diagnostic<JavaFileObject>> diagnosticList = (List<Diagnostic<JavaFileObject>>) request.getAttribute("diagnostics");

        System.out.println(diagnosticList);
        for (Diagnostic<JavaFileObject> diagnostic : diagnosticList) {
            if(diagnostic.getKind() == Diagnostic.Kind.ERROR) {
            %>

            <h2><%=diagnostic.getSource().getName()%>:[<%=diagnostic.getLineNumber()%>,<%=diagnostic.getColumnNumber()%>]</h2>
<p>
<%=diagnostic.getMessage(Locale.getDefault())%>
</p>
<p>
    <%=diagnostic.getKind()%>: <%=diagnostic.getStartPosition()%> - <%=diagnostic.getEndPosition()%>
</p>
<%

    String content = diagnostic.getSource().getCharContent(true).toString();
    String[] lines = content.split("\n");
    for(int l = Math.max(0, (int) diagnostic.getLineNumber() - 3); l < diagnostic.getLineNumber() +3 && l < lines.length; l++) {
        %>
<pre class="<%= l == diagnostic.getLineNumber()-1 ? "error" : "" %>"><%=l%>: <%=lines[l]%></pre>
        <%
        if(l == diagnostic.getLineNumber()-1) {
            %>
            <pre><%

                for(int i = 0; i < diagnostic.getColumnNumber()+1+Long.toString(diagnostic.getColumnNumber()).length();i++) {
                    out.write(" ");
                }
                out.write("^^^");
           %></pre>
<%
        }
    }
    }
%>
            <%
        }
    %>


</body>
</html>
