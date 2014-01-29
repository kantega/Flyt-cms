<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8"%>
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


<kantega:section id="title">Create initial user and role</kantega:section>

<kantega:section id="body">

    <h1>Initial setup</h1>
    <form name="myform" action="CreateInitialUser.action" method="post" autocomplete="off">
        <c:choose>
            <c:when test="${createUserAccount}">
                <p>
                    Before you can use OpenAksess we must create an administrator account that you will need to access the administration interface.
                </p>
                <p>
                    Please enter a desired userid and password (minimum 6 chars) below to create a new account:
                </p>
                <div class="text">
                    <label for="username">Username:</label>
                    <input type="text" name="username" id="username" size="20" maxlength="20" value="${username}">
                </div>
                <c:if test="${errorUsername}">
                    <div class="error">Please enter a username, minimum 3 characters</div>
                </c:if>
                <div class="password">
                    <label for="password">Password:</label>
                    <input type="password" name="password" id="password" size="20" maxlength="20">
                </div>
                <div class="password">
                    <label for="password2">Repeat password:</label>
                    <input type="password" name="password2" id="password2" size="20" maxlength="20">
                </div>
                <c:if test="${errorPassword}">
                    <div class="error">Please enter a password, minimum 6 characters</div>
                </c:if>
            </c:when>
            <c:otherwise>
                <p>
                    Before you can use OpenAksess we must add the role "<%=Aksess.getAdminRole()%>" to a user account
                </p>

                <p>
                    Please enter a userid which should receive the role "<%=Aksess.getAdminRole()%>":
                </p>

                <div class="text">
                    <label for="username">Username:</label>
                    <input type="text" name="username" id="username" size="20" maxlength="20">
                </div>
                <c:if test="${errorUsername}">
                    <div>Please enter a username, minimum 3 characters</div>
                </c:if>

            </c:otherwise>
        </c:choose>

        <c:if test="${needsToken}">
            <div class="text securitytoken">
                <label for="token">Security token: </label>
                <input type="text" name="token" id="token" size="40" value="${token}">
                <div class="helptext">(from security/initialusertoken.txt)</div>
            </div>
            <c:if test="${errorToken}">
                <div class="error">Please enter a token matching the token on the server</div>
            </c:if>
        </c:if>
        <div class="submit">
            <input type="submit" value="Continue">
        </div>

    </form>

</kantega:section>
<%@ include file="../../admin/layout/loginLayout.jsp" %>
