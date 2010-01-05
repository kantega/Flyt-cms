<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1"%>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<title>Create initial user and role</title>
    <link rel="stylesheet" type="text/css" href="<aksess:geturl/>/login/login.css">
<body>
<form name="myform" action="CreateInitialUser.action" method="post" autocomplete="off">
    <table border="0" cellspacing="0" cellpadding="0" width="400" align="center">
        <tr>
            <td width="1" rowspan="3" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
            <td width="396" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
            <td width="1" rowspan="3" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" heigth="1"></td>
            <td width="2" rowspan="3" class="shadow" valign="top"><img src="<aksess:geturl/>/login/bitmaps/corner.gif" width="2" heigth="2"></td>
         </tr>
         <tr>
            <td class="box">
                <h1>Initial setup</h1>

                <c:choose>
                    <c:when test="${createUserAccount}">
                        <p>
                            Before you can use OpenAksess we must create an administrator account that you will need to access the administration interface.
                        </p>

                        <p>
                            Please enter a desired userid and password (minimum 6 chars) below to create a new account:
                        </p>

                        <p>
                            <label for="username">Username:</label><br>
                            <input type="text" name="username" id="username" size="20" maxlength="20" value="${username}">
                        </p>

                        <c:if test="${errorUsername}">
                            <p>Please enter a username, minimum 3 characters</p>
                        </c:if>

                        <p>
                            <label for="password">Password:</label><br>
                            <input type="password" name="password" id="password" size="20" maxlength="20">
                        </p>
                        <p>
                            <label for="password2">Repeat password:</label><br>
                            <input type="password" name="password2" id="password2" size="20" maxlength="20">
                        </p>

                        <c:if test="${errorPassword}">
                            <p>Please enter a password, minimum 6 characters</p>
                        </c:if>

                    </c:when>
                    <c:otherwise>
                        <p>
                            Before you can use OpenAksess we must add the role "<%=Aksess.getAdminRole()%>" to a user account
                        </p>

                        <p>
                            Please enter a userid which should receive the role "<%=Aksess.getAdminRole()%>":
                        </p>

                        <p>
                            <label for="username">Username:</label>
                            <input type="text" name="username" id="username" size="20" maxlength="20">
                        </p>

                        <c:if test="${errorUsername}">
                            <p>Please enter a username, minimum 3 characters</p>
                        </c:if>

                    </c:otherwise>
                </c:choose>

                <c:if test="${needsToken}">
                    <p>
                        <label for="token">Security token: </label> <br/>(from security/initialusertoken.txt)
                        <input type="text" name="token" id="token" size="40" value="<c:out value="${token}"/>">
                        <c:if test="${errorToken}">
                            <p>Please enter a token matching the token on the server</p>
                        </c:if>
                    </p>
                </c:if>
                <p>
                    <input type="submit" value="Continue">
                </p>

            </td>
         </tr>
        <tr>
            <td class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
         </tr>
         <tr>
            <td colspan="4" class="shadow"><img src="<aksess:geturl/>/login/bitmaps/corner.gif" width="2" height="2"></td>
        </tr>
    </table>


</form>
</body>
</html>
