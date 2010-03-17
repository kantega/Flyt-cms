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
    <script type="text/javascript">
        function doAction(action, domain, userId) {
            var af = document.actionform;
            af.action = action;
            af.domain.value = domain;
            af.userId.value = userId;
            af.submit();
        }
    </script>
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

    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <thead>
            <tr class="tableHeading">
                <th><kantega:label key="useradmin.searchprofiles.name"/></th>
                <th><kantega:label key="useradmin.searchprofiles.department"/></th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${users}" varStatus="status">
                <tr class="tableRow<c:out value="${status.index mod 2}"/>">
                    <td><a href="Javascript:doAction('edit', '${user.identity.domain}', '${user.identity.userId}')"><c:out value="${user.givenName}"/> <c:out value="${user.surname}"/></a></td>
                    <td><c:out value="${user.department}"/></td>
                    <td align="right">
                        <table border="0">
                            <tr>
                                <td>
                                    <c:if test="${canSetPassword}">
                                        <a href="Javascript:doAction('../password/reset', '${user.identity.domain}', '${user.identity.userId}')"><kantega:label key="useradmin.searchprofiles.password"/></a>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${canEdit}">
                                        <a href="Javascript:doAction('delete', '${user.identity.domain}', '${user.identity.userId}')"><kantega:label key="useradmin.searchprofiles.delete"/></a>
                                    </c:if>
                                </td>
                                <td><a href="Javascript:doAction('../role/user', '${user.identity.domain}', '${user.identity.userId}')"><kantega:label key="useradmin.searchprofiles.roles"/></a></td>
                                <td>
                                    <c:if test="${canEdit}">
                                        <a href="profileimage?domain=${user.identity.domain}&amp;userId=${user.identity.userId}"><kantega:label key="useradmin.searchprofiles.profileimage"/></a>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${canEdit}">
            <tr>
                <td colspan="5">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="5" align="right">
                    <form action="edit">
                        <input type="hidden" name="domain" value="<c:out value="${domain}"/>">
                        <input type="submit" value="<kantega:label key="useradmin.searchprofiles.newprofile"/>">
                    </form>
                </td>
            </tr>
            </c:if>
        </tbody>
    </table>
    <form action="edit" name="actionform" method="post">
        <input type="hidden" name="domain" value="">
        <input type="hidden" name="userId" value="">
    </form>
</kantega:section>

<%@ include file="../include/design/standard.jsp" %>