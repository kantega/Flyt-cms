<%@ page import="no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory"%>
<%@ page import="no.kantega.publishing.security.SecuritySession"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
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

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
    <title>Søk og erstatt tekst i database</title>
</head>
<body>
Erstatter tekst
<%
    int count = 0;
    if (ss.isUserInRole(Aksess.getAdminRole()) && (findText.length() > 3) && (replaceText.length() > 3)) {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement attributeSt = c.prepareStatement("select * from contentattributes where ContentVersionId = ?");
            PreparedStatement attributeUpdateSt = c.prepareStatement("update contentattributes set Value = ? where AttributeId = ?");

            PreparedStatement cvSt = c.prepareStatement("select * from contentversion where ContentVersionId = ?");
            PreparedStatement cvUpdateSt = c.prepareStatement("update contentversion set Title = ?, Description = ? where ContentVersionId = ?");

            PreparedStatement cSt = c.prepareStatement("select * from content where ContentId in (select ContentId from contentversion where ContentVersionId = ?)");
            PreparedStatement cUpdateSt = c.prepareStatement("update content set Location = ? where ContentId = ?");

            String[] cvids = request.getParameterValues("cvid");
            for (int i = 0; i < cvids.length; i++) {
                int cvid = Integer.parseInt(cvids[i]);
                // Replace all occurences in contentattributes
                attributeSt.setInt(1, cvid);
                ResultSet rs = attributeSt.executeQuery();
                while (rs.next()) {
                    int attributeId = rs.getInt("AttributeId");
                    String value = rs.getString("Value");
                    if (value.indexOf(findText) != -1) {
                        value = StringHelper.replace(value, findText, replaceText);
                        attributeUpdateSt.setString(1, value);
                        attributeUpdateSt.setInt(2, attributeId);
                        attributeUpdateSt.executeUpdate();
                        count++;
                    }
                }

                // Replace occurence in contentversion
                cvSt.setInt(1, cvid);
                ResultSet rs2 = cvSt.executeQuery();
                while (rs2.next()) {
                    String title = rs2.getString("Title");
                    String description = rs2.getString("Description");

                    if (title.indexOf(findText) != -1 || description.indexOf(findText) != -1) {
                        title = StringHelper.replace(title, findText, replaceText);
                        description = StringHelper.replace(description, findText, replaceText);
                        cvUpdateSt.setString(1, title);
                        cvUpdateSt.setString(2, description);
                        cvUpdateSt.setInt(3, cvid);
                        cvUpdateSt.executeUpdate();
                        count++;
                    }
                }

                // Replace occurence in contentversion
                cSt.setInt(1, cvid);
                ResultSet rs3 = cSt.executeQuery();
                while (rs3.next()) {
                    int contentId = rs3.getInt("ContentId");
                    String location = rs3.getString("Location");
                    if (location != null && location.indexOf(findText) != -1) {
                        location = StringHelper.replace(location, findText, replaceText);
                        cUpdateSt.setString(1, location);
                        cUpdateSt.setInt(2, contentId);
                        cUpdateSt.executeUpdate();
                        count++;
                    }
                }


            }
        } catch (Exception e) {
                out.write(e.toString());
        } finally {
            if (c != null) {
                 c.close();
            }
        }
    }
%>
<p>Erstattet <%=count%> forekomster av ordet <%=findText%></p>
</body>
</html>