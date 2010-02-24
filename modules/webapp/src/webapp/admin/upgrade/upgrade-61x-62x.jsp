<%@ page import="java.sql.Connection" %>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.sql.PreparedStatement" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~     http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>
<html>
<head><title>Upgrade from Aksess 6.1.x to 6.2.x</title></head>
<body>
<h1>Upgrade from Aksess 6.1.x to 6.2.x</h1>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {

        PreparedStatement updateSt = c.prepareStatement("ALTER TABLE content ADD RatingScore FLOAT NOT NULL DEFAULT 0");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("ALTER TABLE content ADD NumberOfRatings INT NOT NULL DEFAULT 0");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("CREATE TABLE ratings (userId VARCHAR(255), objectId VARCHAR(255) NOT NULL, context VARCHAR(255) NOT NULL, rating INT NOT NULL, ratingDate DATETIME NOT NULL)");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("UPDATE content set RatingScore = 0, NumberOfRatings = 0");
        updateSt.executeUpdate();


    } finally {
        c.close();
    }
%>
<h2>Upgrade complete</h2>
</body>
</html>
