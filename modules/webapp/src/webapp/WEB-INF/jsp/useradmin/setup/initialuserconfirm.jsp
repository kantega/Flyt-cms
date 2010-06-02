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


<kantega:section id="title">Confirm creation of initial user</kantega:section>

<kantega:section id="body">
    <h1>User account setup complete</h1>
    <form name="myform" action="<%=Aksess.getLoginUrl()%>?redirect=<aksess:geturl/>/admin/" method="POST">
        <input type="hidden" name="j_domain" value="dbuser">
        <input type="hidden" name="j_username" value="${username}">
        <input type="hidden" name="j_password" value="${password}">

        <p>
            The account <b>${username}</b> has been given administration privileges.
        </p>

        <p>
            Press Continue to proceed to login and admin page.
        </p>

        <div class="submit">
            <input type="submit" value="Continue">
        </div>

    </form>
    </div>
    </div>
</kantega:section>
<%@ include file="../../admin/layout/loginLayout.jsp" %>
