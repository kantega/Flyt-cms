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

<kantega:section id="head">

    <script type="text/javascript" language="JavaScript">


        window.onload = function() {
            if (window.self != window.top) {
                window.open(".", "_top");
            }
            document.loginForm.j_username.focus();

        };

        function checkPassword() {
            if (document.loginForm.j_username.value.length < 1) {
                document.loginForm.j_username.focus();
                return false;
            }

            if (document.loginForm.j_password.value.length < 1) {
                document.loginForm.j_password.focus();
                return false;
            }

            return true;
        }
    </script>
</kantega:section>

<kantega:section id="bodyclass">login</kantega:section>

<kantega:section id="body">
    <div id="LoginForm">
        <form method="post" action="<%=response.encodeURL(Aksess.getContextPath() + "/Login.action")%>" name="loginForm" onsubmit="return checkPassword()">
            <input type="hidden" name="j_domain" value="<%=Aksess.getDefaultSecurityDomain()%>">
            <input type="hidden" name="redirect" value="${redirect}">

            <c:set var="autocomplete"><aksess:getconfig key="security.login.autocomplete"/></c:set>
            <div id="UserName">
                <label for="j_username"><kantega:label key="aksess.login.username"/></label>
                <input type="text" placeholder="<kantega:label key="aksess.login.username"/>" id="j_username" name="j_username" value="${username}" size="25" maxlength="60" <c:if test="${!autocomplete}">autocomplete="off"</c:if>>
            </div>
            <div id="Password">
                <label for="j_password"><kantega:label key="aksess.login.password"/></label>
                <input type="password" id="j_password" placeholder="Passord" name="j_password" size="25" maxlength="60">
            </div>
            <c:set var="isRememberMe"><aksess:getconfig key="security.login.rememberme.enabled"/></c:set>
            <c:if test="${isRememberMe}">
                <div id="RememberMe">
                    <label for="remember_me">Husk meg</label>
                    <input type="checkbox" id="remember_me" name="remember_me">
                </div>
            </c:if>
            <div id="Submit" class="<c:if test="${allowPasswordReset}">withPasswordReset</c:if>">
                <input type="submit" value="<kantega:label key="aksess.login.login"/>">
            </div>

            <c:if test="${allowPasswordReset}">
                <div id="PasswordReset"><a href="${pageContext.request.contextPath}/RequestPasswordReset.action"><kantega:label key="aksess.login.resetpassword"/></a></div>
            </c:if>
        </form>
    </div>

    <div id="LoginMessages">
        <c:if test="${loginfailed}">
            <div id="LoginFailed"><kantega:label key="aksess.login.loginfailed"/></div>
        </c:if>
        <c:if test="${blockedUser}">
            <div id="BlockedUser"><kantega:label key="aksess.login.blockeduser"/></div>
        </c:if>
        <c:if test="${blockedIP}">
            <div id="BlockedIp"><kantega:label key="aksess.login.blockedip"/></div>
        </c:if>

        <div id="CapsLock" style="display:none">
            <kantega:label key="aksess.login.caps"/>
        </div>

        <noscript>
            <div id="NoScript">Javascript must be enabled to login</div>
        </noscript>
    </div>

</kantega:section>
<jsp:include page="${loginLayout}"/>
