<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.configuration.Configuration" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>

<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.File" %>

<%
    SecuritySession securitySession = SecuritySession.getInstance(request);
%>
<html>
<head>
    <style type="text/css">
        td {
            font-family: courier,monospace;
        }

        tr.odd {
            background-color: #eee;
        }
    </style>
</head>
<body>

<%
    Configuration configuration = Aksess.getConfiguration();
    String logfile = configuration.getString("logfile.path", configuration.getApplicationDirectory() + "/logs/aksess.log");

    RequestParameters param = new RequestParameters(request);


    int maxLines = param.getInt("maxLines");
    if (maxLines <= 0) {
        maxLines = 400;
    }
%>


<form action="logreader.jsp">
    Vis antall linjer (fra slutten) <input type="text" name="maxLines" value="<%=maxLines%>">
    <input type="submit" value="Vis">

    <table border="0">


        <%
            if (securitySession.isUserInRole(Aksess.getAdminRole())) {
                File src = new File(logfile);

                BufferedReader reader = new BufferedReader(new FileReader(src));
                String[] lines = new String[maxLines];
                int lastNdx = 0;
                for (String line=reader.readLine(); line != null; line=reader.readLine()) {
                    if (lastNdx == lines.length) {
                        lastNdx = 0;
                    }
                    lines[lastNdx++] = line;
                }

                for (int ndx=lastNdx; ndx != lastNdx-1; ndx++) {
                    if (ndx == lines.length) {
                        ndx = 0;
                    }

                    String clz = "odd";
                    if (ndx % 2 == 0) {
                        clz = "even";
                    }

                    if (lines[ndx] != null) {
                        out.write("<tr class=\"" + clz + "\"><td>" + lines[ndx] + "</td></tr>");
                    }
                }

                reader.close();

                src = null;
            }
        %>
    </table>
</form>
</body>

</html>