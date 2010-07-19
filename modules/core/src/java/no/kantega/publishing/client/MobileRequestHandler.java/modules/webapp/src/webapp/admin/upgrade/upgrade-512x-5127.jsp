<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

<html>
<head><title>Oppgradering fra Aksess 5.12.x til 5.12.7</title></head>
<body>
<h1>Oppgradering fra Aksess 5.12.x til 5.12.7</h1>
<h2>Oppgraderer rettigheter multimediaarkiv</h2>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {
        PreparedStatement selectSt = c.prepareStatement("select * from objectpermissions where ObjectType = ? and Privilege > ? and ObjectSecurityId not in (select ObjectSecurityId from objectpermissions where ObjectType = ? and Privilege = ?)");
        selectSt.setInt(1, ObjectType.MULTIMEDIA);
        selectSt.setInt(2, Privilege.VIEW_CONTENT);
        selectSt.setInt(3, ObjectType.MULTIMEDIA);
        selectSt.setInt(4, Privilege.VIEW_CONTENT);

        PreparedStatement updateSt = c.prepareStatement("insert into objectpermissions values (?,?,?,?,?)");
        ResultSet selectRs = selectSt.executeQuery();
        while (selectRs.next()) {
            updateSt.setInt(1, selectRs.getInt("ObjectSecurityId"));
            updateSt.setInt(2, ObjectType.MULTIMEDIA);
            updateSt.setInt(3, Privilege.VIEW_CONTENT);
            updateSt.setString(4, "Role");
            updateSt.setString(5, "everyone");
            updateSt.executeUpdate();
            out.write("Legger til rettighet...<br>");
        }

    } finally {
        c.close();
    }
%>
<h2>Oppgradering fullført</h2>
</body>
</html>