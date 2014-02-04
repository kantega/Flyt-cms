<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>

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
<head><title>Oppgradering fra Aksess 5.13.x til 5.14.x</title></head>
<body>
<h1>Oppgradering fra Aksess 5.13.x til 5.14.x</h1>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {

        PreparedStatement updateSt = c.prepareStatement("CREATE TABLE multimediausage (ContentId INT NOT NULL, MultimediaId INT NOT NULL)");
        updateSt.executeUpdate();
    } finally {
        c.close();
    }
%>
<h2>Oppgradering fullført</h2>
</body>
</html>