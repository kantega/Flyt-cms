<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<title><kantega:label key="aksess.displaytemplates.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>

<body class="bodyWithMargin">

    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td>
                <div class=helpText><kantega:label key="aksess.displaytemplates.usages.help"/></div>
            </td>
        </tr>
    </table><br>

    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><kantega:label key="aksess.displaytemplates.usages.title"/></td>
            <td><kantega:label key="aksess.displaytemplates.usages.lastmodified"/></td>
        </tr>
        <aksess:getcollection name="pages" contentquery="${query}" skipattributes="true" max="50" varStatus="status">
            <tr class="tableRow<c:out value="${status.index mod 2}"/>">
                <td><aksess:link collection="pages" target="_blank"><aksess:getattribute name="title" collection="pages"/></aksess:link></td>
                <td><aksess:getattribute name="lastmodified" collection="pages"/></td>
            </tr>
        </aksess:getcollection>
    </table>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>