<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="no.kantega.publishing.common.data.enums.MultimediaType" %>
<%@ page import="no.kantega.publishing.common.util.database.SQLHelper" %>

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
<head><title>Update file and subfolder count in mediaarchive</title></head>
<body>
<h1>Update file and subfolder count in mediaarchive</h1>
<%
    Connection c = dbConnectionFactory.getConnection();
    try {
        PreparedStatement updateFilesSt = c.prepareStatement("update multimedia set NoFiles = 0, NoSubFolders = 0 where Type = " + MultimediaType.MEDIA.getTypeAsInt());
        updateFilesSt.execute();

        PreparedStatement selectSt = c.prepareStatement("select Id from multimedia where Type = " + MultimediaType.FOLDER.getTypeAsInt());
        PreparedStatement updateSt = c.prepareStatement("update multimedia set NoFiles = ?, NoSubFolders = ? where Id = ?");
        ResultSet selectRs = selectSt.executeQuery();

        while (selectRs.next()) {
            int parentId = selectRs.getInt("Id");
            int noFiles = SQLHelper.getInt(c, "select count(Id) as cnt from multimedia where ParentId = " + parentId + " and Type = " + MultimediaType.MEDIA.getTypeAsInt(), "cnt");
            int noSubFolders = SQLHelper.getInt(c, "select count(Id) as cnt from multimedia where ParentId = " + parentId + " and Type = " + MultimediaType.FOLDER.getTypeAsInt(), "cnt");
            updateSt.setInt(1, noFiles);
            updateSt.setInt(2, noSubFolders);
            updateSt.setInt(3, parentId);
            updateSt.execute();
        }

    } finally {
        c.close();
    }
%>
<h2>Done</h2>
</body>
</html>