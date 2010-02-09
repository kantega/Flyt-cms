<%@ page import="org.apache.commons.io.IOUtils" %>
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>OpenAksess setup</title>

    <style type="text/css">
<%
    IOUtils.copy(pageContext.getServletContext().getResourceAsStream("/admin/css/default.css"), out);
%>
        label {
            display:inline;
        }
</style>
</head>
<body>
<h1>OpenAksess initial database setup</h1>

<p>

</p>

<script type="text/javascript">
    function setDefaultUrl(defaultUrl) {
        var url = document.getElementById("jdbcurl");

        url.value = defaultUrl;

    }
</script>
<form action="<%=request.getContextPath()%>/Setup.initialAction" method="POST">
    <c:if test="${not empty errors}">
        <ul>
            <c:forEach var="error" items="${errors}">
                <li>
                    <c:out value="${error}"/>
                </li>
            </c:forEach>
        </ul>
    </c:if>
    <table>
        <tr>
            <td style="vertical-align:top;">
                Database driver:
            </td>
            <td>
                <c:forEach var="driver" items="${drivers}">
                    <input name="driver" value="<c:out value="${driver.value.id}"/>" id="driver_<c:out value="${driver.value.id}"/>" type="radio" onclick="setDefaultUrl('<c:out value="${driver.value.defaultUrl}"/>')" <c:if test="${driverName == driver.value.id}">checked="checked"</c:if>> <label for="driver_<c:out value="${driver.value.id}"/>"><c:out value="${driver.value.name}"/></label>
                    <c:if test="${driver.value.helpText != null}">
                        (${driver.value.helpText})
                    </c:if><br />
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td>
                Database url:
            </td>
            <td>
                <input name="url" size="100" id="jdbcurl" value="<c:out value="${url}"/>">
            </td>
        </tr>

        <tr>
            <td>
                Username:
            </td>
            <td>
                <input name="username" value="<c:out value="${username}"/>">
            </td>
        </tr>

        <tr>
            <td>
                Password:
            </td>
            <td>
                <input name="password" type="password">
            </td>
        </tr>

        <tr>
            <td>

            </td>
            <td>
                <input type="submit" value="Save">
            </td>
        </tr>
    </table>

</form>
</body>
</html>
