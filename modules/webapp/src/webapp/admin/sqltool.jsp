<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory"%>
<%@ page import="no.kantega.publishing.security.SecuritySession"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.ResultSetMetaData" %>

<%
    SecuritySession securitySession = SecuritySession.getInstance(request);
%>

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
    String query = request.getParameter("query");
    if (query == null) {
        query = "";
    }
%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
    <title>SQL tool</title>
</head>
<body>
<form action="sqltool.jsp" method="post">
    <textarea rows="8" cols="60" name="query"><%=query%></textarea><input accesskey="E" type="submit" value="Execute query">
</form>
<table border="1">
    <%
        if (securitySession.isUserInRole(Aksess.getAdminRole())) {
            if (query.length() > 0) {
                Connection c = null;
                try {
                    c = dbConnectionFactory.getConnection();
                    PreparedStatement st = c.prepareStatement(query);

                    ResultSet rs = st.executeQuery();
                    ResultSetMetaData mdata = rs.getMetaData();
                    int cols = mdata.getColumnCount();
                    out.write("<tr>");
                    for (int i = 1; i <= cols; i++) {
                        out.write("<td><b>");
                        out.write(mdata.getColumnName(i));
                        out.write("</b></td>");
                    }
                    out.write("</tr>");
                    while(rs.next()) {
                        out.write("<tr>");
                        for (int i = 1; i <= cols; i++) {
                            String obj = rs.getString(i);
                            out.write("<td>");
                            if (obj == null) obj = "null";
                            out.write(obj);
                            out.write("</td>");
                        }
                        out.write("</tr>");
                    }
                } catch (Exception e) {
                    out.write(e.toString());
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    %>
</table>
</body>
</html>