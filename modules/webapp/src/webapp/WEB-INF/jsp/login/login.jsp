<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1"%>
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

            showHideUsernameBG();
            showHidePasswordBG();
        };

        function showHideUsernameBG() {
            if (document.loginForm.j_username.value != '') {
                // Hide background
                document.loginForm.j_username.className = '';
            } else {
                // Show background
                document.loginForm.j_username.className = 'empty';
            }
            return true;
        }

        function showHidePasswordBG() {
            if (document.loginForm.j_password.value != '') {
                // Hide background
                document.loginForm.j_password.className = '';
            } else {
                // Show background
                document.loginForm.j_password.className = 'empty';
            }
            return true;
        }


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

<kantega:section id="body">
    <div id="LoginForm">
    <form method="post" action="<%=response.encodeURL(Aksess.getContextPath() + "/Login.action")%>" name="loginForm" onsubmit="return checkPassword()">
        <input type="hidden" name="j_domain" value="<%=Aksess.getDefaultSecurityDomain()%>">
        <input type="hidden" name="redirect" value="<c:out value="${redirect}"/>">

        <div id="UserName">
            <input type="text" id="j_username" name="j_username" value="<c:out value="${username}"/>" onkeyup="showHideUsernameBG()" size="25" maxlength="60">
        </div>
        <div id="Password">
            <input type="password" id="j_password" name="j_password"  onfocus="showHidePasswordBG()" onkeyup="showHidePasswordBG()" size="25" maxlength="60">
        </div>
        <div id="Submit">
            <input type="submit" value="<kantega:label key="aksess.login.login"/>">
        </div>
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
<%@ include file="../admin/layout/loginLayout.jsp" %>