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


<kantega:section id="title">OpenAksess Setup</kantega:section>

<kantega:section id="head">
    <script type="text/javascript" src="${pageContext.request.contextPath}/login/js/formlabels.js"></script>
</kantega:section>

<kantega:section id="body">
    <h1>OpenAksess initial database setup</h1>
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

        <script type="text/javascript">
            function setDefaultUrl(defaultUrl) {
                var url = document.getElementById("jdbcurl");
                url.value = defaultUrl;
            }
        </script>

        <div class="text">
            <label for="username">Database driver</label>
            <c:forEach var="driver" items="${drivers}">
                <input name="driver" value="<c:out value="${driver.value.id}"/>" id="driver_<c:out value="${driver.value.id}"/>" type="radio" onclick="setDefaultUrl('<c:out value="${driver.value.defaultUrl}"/>')" <c:if test="${driverName == driver.value.id}">checked="checked"</c:if>> <label for="driver_<c:out value="${driver.value.id}"/>"><c:out value="${driver.value.name}"/></label>
                <c:if test="${driver.value.helpText != null}">
                    (${driver.value.helpText})
                </c:if><br />
            </c:forEach>
        </div>

        <div class="text">
            <label for="username">Database url</label>
            <input type="text" name="url" id="jdbcurl" size="100" maxlength="100" value="${url}">
        </div>

        <div class="text">
            <label for="username">Username</label>
            <input type="text" name="username" id="username" size="20" maxlength="20" value="${username}">
        </div>

        <div class="password">
            <label for="password">Password</label>
            <input type="password" name="password" id="password" size="20" maxlength="20">
        </div>

        <div class="submit">
            <input type="submit" value="Save" onclick="this.disabled=true; this.value='Creating database...'; this.form.submit()">
        </div>

    </form>
</kantega:section>

<%@ include file="../jsp/admin/layout/loginLayout.jsp" %>

