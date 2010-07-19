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
<head><title>Upgrade from Aksess 6.0.x to 6.1.x</title></head>
<body>
<h1>Upgrade from Aksess 6.0.x to 6.1.x</h1>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {

        String driver = dbConnectionFactory.getDriverName();
        if (driver.indexOf("mysql") != -1) {
            PreparedStatement updateSt = c.prepareStatement("CREATE TABLE formsubmission " +
                    "(" +
                    "    FormSubmissionId INTEGER NOT NULL AUTO_INCREMENT," +
                    "    FormId INTEGER NOT NULL," +
                    "    SubmittedBy VARCHAR(255) NULL," +
                    "    AuthenticatedIdentity VARCHAR(255) NULL," +
                    "    Password VARCHAR(255) NULL," +
                    "    Email VARCHAR(255) NULL," +
                    "    SubmittedDate DATETIME NOT NULL," +
                    "    PRIMARY KEY (FormSubmissionId)" +
                    ") ;");
            updateSt.executeUpdate();

            updateSt = c.prepareStatement("CREATE TABLE formsubmissionvalues " +
                    "(" +
                    "    FormSubmissionId INTEGER NOT NULL," +
                    "    FieldNumber INTEGER NOT NULL," +
                    "    FieldName VARCHAR(255) NOT NULL," +
                    "    FieldValue LONGTEXT NULL" +
                    ") ;");
            updateSt.executeUpdate();

        } else {
            PreparedStatement updateSt = c.prepareStatement("CREATE TABLE formsubmission " +
                    " (" +
                    "    FormSubmissionId INT NOT NULL IDENTITY (1,1) ," +
                    "    FormId INT NOT NULL," +
                    "    SubmittedBy VARCHAR(255)," +
                    "    AuthenticatedIdentity VARCHAR(255)," +
                    "    Password VARCHAR(255)," +
                    "    Email VARCHAR(255)," +
                    "    SubmittedDate DATETIME NOT NULL," +
                    "    PRIMARY KEY (FormSubmissionId)" +
                    ");");
            updateSt.executeUpdate();

            updateSt = c.prepareStatement("CREATE TABLE formsubmissionvalues " +
                    "(" +
                    "    FormSubmissionId INT NOT NULL," +
                    "    FieldNumber INT NOT NULL," +
                    "    FieldName VARCHAR(255) NOT NULL," +
                    "    FieldValue TEXT" +
                    ");");
            updateSt.executeUpdate();

        }

    } finally {
        c.close();
    }
%>
<h2>Oppgradering fullført</h2>
</body>
</html>