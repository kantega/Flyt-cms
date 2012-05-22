<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
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
<%
    request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());
%>

<kantega:section id="bodyclass">login</kantega:section>

<kantega:section id="body">
    <c:choose>
        <c:when test="${passwordChanged}">
            <div id="ResetPasswordHelp">
                <p><kantega:label key="aksess.resetpassword.changed"/></p>
                <p><a href="Login.action"><kantega:label key="aksess.resetpassword.login"/></a></p>
            </div>
        </c:when>
        <c:otherwise>
            <div id="ResetPasswordHelp">
                <kantega:label key="aksess.resetpassword.help" minPasswordLength="${minPasswordLength}"/>
            </div>
            <div id="RequestResetPasswordForm">

                <form method="post" action="ResetPassword.action" name="loginForm">
                    <input type="hidden" name="username" value="${username}">
                    <input type="hidden" name="domain" value="${domain}">
                    <input type="hidden" name="token" value="${token}">
                    <div id="Password">
                        <label>Passord...</label>
                        <input type="password" id="j_password" name="password1" size="25" maxlength="60">
                    </div>
                    <div id="RepeatPassword">
                        <label>Gjenta passord...</label>
                        <input type="password" id="j_password2" name="password2" size="25" maxlength="60">
                    </div>
                    <div id="Submit">
                        <input type="submit" value="<kantega:label key="aksess.resetpassword.changepw"/>">
                    </div>

                </form>
            </div>

            <div id="LoginMessages">
                <c:if test="${error != null}">
                    <div id="Error"><kantega:label key="${error}"/></div>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</kantega:section>
<jsp:include page="${loginLayout}"/>
