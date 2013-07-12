<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.commons.configuration.Configuration" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>

<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileReader" %>

<%
    SecuritySession securitySession = SecuritySession.getInstance(request);

    if (!securitySession.isUserInRole(Aksess.getAdminRole())) {
        return;
    }

    Configuration configuration = Aksess.getConfiguration();
    String logfile = configuration.getString("logfile.path", configuration.getApplicationDirectory() + "/logs/aksess.log");

    RequestParameters param = new RequestParameters(request);


    File src = new File(logfile);
    BufferedReader reader = new BufferedReader(new FileReader(src));

    boolean download = param.getBoolean("download", false);
    if (download) {
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=\"aksess.log\"");
    }

    int maxLines = param.getInt("maxLines");
    if (maxLines <= 0) {
        maxLines = 400;
    }

    String[] lines = new String[maxLines];
    int lastNdx = 0;
    for (String line=reader.readLine(); line != null; line=reader.readLine()) {
        if (download) {
            out.write(line);
        } else {
            if (lastNdx == lines.length) {
                lastNdx = 0;
            }
            lines[lastNdx++] = line;
        }
    }
    reader.close();

    src = null;

    if (download) {
        return;
    }
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
%>


<form action="logreader.jsp">
    Vis antall linjer (fra slutten) <input type="text" name="maxLines" value="<%=maxLines%>">
    <input type="submit" value="Vis">

    <p><a href="?download=true">Last ned hele loggen</a></p>

    <table border="0">
        <%
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

        %>
    </table>
</form>
</body>

</html>