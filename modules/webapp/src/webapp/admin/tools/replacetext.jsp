<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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
    String findText = "";
    String replaceText = "";
    if (request.getMethod().equalsIgnoreCase("POST")) {
        findText = request.getParameter("find");
        replaceText = request.getParameter("replace");
    }

    SecuritySession ss = SecuritySession.getInstance(request);
%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<html>
<head>
    <title>Søk og erstatt tekst i database</title>
</head>
<body>
<form action="replacetext.jsp" method="post">
    <p>
        <b>Finn</b>:<br>
        <input type="text" name="find" value="<%=findText%>">
    </p>
    <p>
        <b>Erstatt med</b>:<br>
        <input type="text" name="replace" value="<%=replaceText%>">
    </p>

    <p>
        <input type="submit" value="Finn forekomster">
    </p>
</form>

<%
    if (ss.isUserInRole(Aksess.getAdminRole()) && (findText.length() > 3) && (replaceText.length() > 3)) {
%>
<p>
    Fant følgende forekomster av <b><%=findText%></b>
</p>
<form action="replacetextAction.jsp" method="post">
    <input type="hidden" name="find" value="<%=findText%>">
    <input type="hidden" name="replace" value="<%=replaceText%>">
<table border="1">
    <%
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("select * from contentversion where contentversionid in (select contentversionid from contentattributes where value like '%" + findText + "%') and isactive = 1");

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int cvId = rs.getInt("ContentVersionId");
                int contentId = rs.getInt("ContentId");
                String title = rs.getString("title");
                %>
                <tr>
                    <td><input type="checkbox" name="cvid" value="<%=cvId%>" checked="checked"></td>
                    <td><a href="<%=Aksess.getContextPath()%>/content.ap?contentId=<%=contentId%>" target="_new"><%=title%></a></td>
                </tr>
                <%
            }

        } catch (Exception e) {
                out.write(e.toString());
        } finally {
            if (c != null) {
                 c.close();
            }
        }
    %>
</table>
    <p style="color: #ff0000">
        NB! Søket her tar ikke hensyn til små / store bokstaver.  Det kan derfor her vises sider som ikke vil bli endret.
        NB! Endringen skjer direkte i databasen og kan ikke endres, ta backup hvis nødvendig før du gjør endringer<br>
    </p>
    <p>
        <input type="submit" value="Erstatt tekst i valgte sider">
    </p>
<%
    }
%>
</form>
</body>
</html>