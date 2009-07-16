<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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

<kantega:section id="title">
    <kantega:label key="useradmin.deleterole.title"/>
</kantega:section>

<kantega:section id="content">
    <form action="delete" name="myform" method="post">
        <input type="hidden" name="domain" value="<c:out value="${role.domain}"/>">
        <input type="hidden" name="roleId" value="<c:out value="${role.id}"/>">
        <input type="hidden" name="confirm" value="true">
            <p>
                <kantega:label key="useradmin.deleterole.confirm"/> <c:out value="${role.name}"/> ?
            </p>

            <p>
                <input type="submit" class="button" value="<kantega:label key="aksess.button.delete"/>">
                <input type="button" class="button" onclick="history.back();" value="<kantega:label key="aksess.button.cancel"/>">
            </p>
    </form>
</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>