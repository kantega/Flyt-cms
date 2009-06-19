<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory"%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ include file="../include/jsp_header.jsf" %>
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
    ContentQuery query = new ContentQuery();
    String driver = dbConnectionFactory.getDriverName().toLowerCase();
    if (driver.indexOf("oracle") != -1) {
        query.setSql(" and content.Alias is not null and associations.Type = 1");
    } else {
        query.setSql(" and content.Alias is not null and content.Alias <> '' and associations.Type = 1");
    }
    pageContext.setAttribute("cq", query);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>aliases/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <%
        int i = 0;
    %>
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><kantega:label key="aksess.aliases.alias"/></td>
            <td><kantega:label key="aksess.aliases.page"/></td>
        </tr>
        <aksess:getcollection contentquery="${cq}" name="aliaser" skipattributes="true">

            <tr class="tableRow<%=(i%2)%>">
                <td><aksess:getattribute name="alias" collection="aliaser"/></td>
                <td><aksess:link collection="aliaser" target="_new"><aksess:getattribute name="title" collection="aliaser"/></aksess:link></td>
            </tr>
        <%
            i++;
        %>
        </aksess:getcollection>
        <tr>
            <td colspan="2"><br>
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.aliases.help"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>