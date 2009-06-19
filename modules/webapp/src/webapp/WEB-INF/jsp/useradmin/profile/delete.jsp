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

<kantega:section id="head">

</kantega:section>

<kantega:section id="innhold">
    <form action="delete" name="myform" method="post">
        <input type="hidden" name="domain" value="<c:out value="${profile.identity.domain}"/>">
        <input type="hidden" name="userId" value="<c:out value="${profile.identity.userId}"/>">
        <input type="hidden" name="confirm" value="true">
            <p>
                <kantega:label key="useradmin.deleteprofile.confirm"/> <c:out value="${profile.givenName}"/> <c:out value="${profile.surname}"/> ?
            </p>

            <p>
                <input type="submit" class="button" value="<kantega:label key="aksess.button.slett"/>">
                <input type="button" class="button" onclick="history.back();" value="<kantega:label key="aksess.button.avbryt"/>">
            </p>
    </form>
</kantega:section>

<%@ include file="../include/design/standard.jsp" %>