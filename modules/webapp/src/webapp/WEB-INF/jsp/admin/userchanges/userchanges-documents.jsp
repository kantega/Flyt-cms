<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>userchanges-documents.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <tr>
            <td colspan="2">
                <strong><kantega:label key="aksess.userchanges.username"/>:</strong> ${username}
                <p>&nbsp;</p>
            </td>
        </tr>
        <tr class="tableHeading">
            <td><strong><kantega:label key="aksess.userchanges.documents.title"/></strong></td>
            <td><strong><kantega:label key="aksess.userchanges.documents.changed"/></strong></td>
        </tr>
        <aksess:getcollection contentquery="${cq}" name="endringer" skipattributes="true" orderby="lastmodified" max="200" descending="true" varStatus="status">
            <tr class="tableRow${status.index mod 2}">
                <td><a href="<aksess:geturl/>/admin/?thisId=<aksess:getattribute name="id" collection="endringer"/>" target="_top"><aksess:getattribute name="title" collection="endringer"/></a></td>
                <td><aksess:getmetadata name="lastmodified" collection="endringer"/></td>
            </tr>
        </aksess:getcollection>
        <tr>
            <td colspan="2">
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.userchanges.documents.help"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>