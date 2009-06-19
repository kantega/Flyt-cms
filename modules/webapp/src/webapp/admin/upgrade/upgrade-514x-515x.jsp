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
<head><title>Oppgradering fra Aksess 5.14.x til 5.15.x</title></head>
<body>
<h1>Oppgradering fra Aksess 5.14.x til 5.15.x</h1>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {

        PreparedStatement updateSt = c.prepareStatement("alter table content add IsLocked int default 0 not null");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table contenttemplates add PublicId varchar(255)");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table displaytemplates add PublicId varchar(255)");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table associationcategory add PublicId varchar(255)");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table contenttemplates add LastModified datetime");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table displaytemplates add LastModified datetime");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table associationcategory add LastModified datetime");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table objectpermissions add NotificationPriority int default 0 not null");
        updateSt.executeUpdate();

        updateSt = c.prepareStatement("alter table dbuserpassword add HashMech VARCHAR(64)");

        updateSt = c.prepareStatement("update objectpermissions set NotificationPriority = 1");
        updateSt.executeUpdate();

        String driver = dbConnectionFactory.getDriverName();
        if (driver.indexOf("mysql") != -1) {
            updateSt = c.prepareStatement("update contenttemplates set lastmodified = CURRENT_TIMESTAMP()");
            updateSt.executeUpdate();

            updateSt = c.prepareStatement("update displaytemplates set lastmodified = CURRENT_TIMESTAMP()");
            updateSt.executeUpdate();

            updateSt = c.prepareStatement("update associationcategory set lastmodified = CURRENT_TIMESTAMP()");
            updateSt.executeUpdate();            
        } else {
            updateSt = c.prepareStatement("update contenttemplates set lastmodified = GETDATE()");
            updateSt.executeUpdate();

            updateSt = c.prepareStatement("update displaytemplates set lastmodified = GETDATE()");
            updateSt.executeUpdate();

            updateSt = c.prepareStatement("update associationcategory set lastmodified = GETDATE()");
            updateSt.executeUpdate();
        }

    } finally {
        c.close();
    }


%>
<h2>Oppgradering fullført</h2>
</body>
</html>