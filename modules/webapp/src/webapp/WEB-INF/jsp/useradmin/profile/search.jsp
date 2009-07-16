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
    <kantega:label key="useradmin.searchprofiles.title"/>
</kantega:section>

<kantega:section id="content">
    <script type="text/javascript">
        function doAction(action, domain, userId) {
            var af = document.actionform;
            af.action = action;
            af.domain.value = domain;
            af.userId.value = userId;
            af.submit();
        }
    </script>
    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="useradmin.searchprofiles.title"/></legend>

            <form action="search" method="post">
                <c:if test="${numProfileConfigurations > 1}">
                    <select name="domain">
                        <c:forEach items="${profileConfigurations}" var="config">
                            <option value="<c:out value="${config.domain}"/>" <c:if test="${domain eq config.domain}">selected</c:if>><c:out value="${config.description}"/></option>
                        </c:forEach>
                    </select>
                </c:if>
                <input type="text" name="q" value="<c:out value="${query}"/>">
                <input type="submit" value="Søk">
            </form>

            <table>
                <thead>
                <tr>
                    <th><kantega:label key="useradmin.searchprofiles.name"/></th>
                    <th><kantega:label key="useradmin.searchprofiles.department"/></th>
                    <th>&nbsp;</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="user" items="${users}" varStatus="status">
                <tr class="tableRow<c:out value="${status.index mod 2}"/>">
                    <td><a href="Javascript:doAction('edit', '${user.identity.domain}', '${user.identity.userId}')"><c:out value="${user.givenName}"/> <c:out value="${user.surname}"/></a></td>
                    <td><c:out value="${user.department}"/></td>
                    <td>
                        <c:if test="${canSetPassword}">
                            <a href="Javascript:doAction('../password/reset', '${user.identity.domain}', '${user.identity.userId}')" class="button password"><span><kantega:label key="useradmin.searchprofiles.password"/></span></a>
                        </c:if>
                        <c:if test="${canEdit}">
                            <a href="Javascript:doAction('delete', '${user.identity.domain}', '${user.identity.userId}')" class="button edit"><span><kantega:label key="useradmin.searchprofiles.delete"/></span></a>
                        </c:if>
                        <a href="Javascript:doAction('../role/user', '${user.identity.domain}', '${user.identity.userId}')" class="button roles"><span><kantega:label key="useradmin.searchprofiles.roles"/></span></a>
                    </td>
                </tr>
                </c:forEach>

                <c:if test="${canEdit}">
                <tr>
                    <td colspan="3" align="right">
                        <form action="edit">
                            <input type="hidden" name="domain" value="<c:out value="${domain}"/>">
                            <input type="submit" value="<kantega:label key="useradmin.searchprofiles.newprofile"/>">
                        </form>
                    </td>
                </tr>
                </c:if>
            </table>

        </fieldset>
    </div>

    <form action="edit" name="actionform" method="post">
        <input type="hidden" name="domain" value="">
        <input type="hidden" name="userId" value="">
    </form>
</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>