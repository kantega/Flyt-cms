<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
  ~ Copyright 2014 Kantega AS
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
    <c:if test="${not blockedUser and not expiredLoginToken and not missingrecipientattribute and not sendtokenfailed}">
    <c:if test="${not empty profile}">
        <div>
            <kantega:label key="aksess.login.logintoken.help" recipient="${profile.email}"/>
        </div>
    </c:if>
    <div id="LoginForm">
        <form method="post" action="<%=response.encodeURL(Aksess.getContextPath() + "/LoginToken.action")%>" name="loginForm" >
            <input type="hidden" name="domain" value="<%=Aksess.getDefaultSecurityDomain()%>">
            <input type="hidden" name="redirect" value="${redirect}">
            <input type="hidden" name="username" value="${username}">

            <div id="LoginToken">
                <label for="logintokenField"><kantega:label key="aksess.login.logintoken"/></label>
                <input type="text" placeholder="<kantega:label key="aksess.login.logintoken"/>" id="logintokenField" name="logintoken" size="25" maxlength="60" autocomplete="off">
            </div>

            <div id="Submit" >
                <input type="submit" value="<kantega:label key="aksess.login.login"/>">
            </div>

        </form>
    </div>
    </c:if>

    <div id="LoginMessages">
        <c:if test="${loginfailed}">
            <div><kantega:label key="aksess.login.logintoken.failed"/></div>
        </c:if>
        <c:if test="${blockedUser}">
            <div><kantega:label key="aksess.login.logintoken.blockeduser"/></div>
        </c:if>
        <c:if test="${expiredLoginToken}">
                <div><kantega:label key="aksess.login.logintoken.expired"/></div>
            <script type="application/javascript">
                setTimeout(function(){
                    window.location.href = '${pageContext.request.contextPath}/Login.action?j_username=${username}&redirect=${redirect}';
                }, 5000)
            </script>
        </c:if>
        <c:if test="${missingrecipientattribute}">
            <div><kantega:label key="aksess.login.logintoken.missingrecipientattribute"/></div>
        </c:if>
        <c:if test="${sendtokenfailed}">
            <div><kantega:label key="aksess.login.logintoken.sendtokenfailed"/></div>
        </c:if>
    </div>
</kantega:section>
<jsp:include page="${loginLayout}"/>
