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
<head><title>Upgrade from Aksess 6.2.x to 7.0.x</title></head>
<body>
<h1>Upgrade from Aksess 6.2.x to 7.0.x</h1>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {

        PreparedStatement updateSt = c.prepareStatement("ALTER TABLE contentversion ADD ChangeFrom DATETIME");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("ALTER TABLE content ADD IsSearchable INT");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("UPDATE content SET IsSearchable=1");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("ALTER TABLE multimedia ADD NoFiles INT");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("UPDATE multimedia SET NoFiles=0");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("ALTER TABLE multimedia ADD NoSubFolders INT");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("UPDATE multimedia SET NoSubFolders=0");
        updateSt.executeUpdate();

    } finally {
        c.close();
    }
%>
<h2>Upgrade complete</h2>
</body>
</html>
