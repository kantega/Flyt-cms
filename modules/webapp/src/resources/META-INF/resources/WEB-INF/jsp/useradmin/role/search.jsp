<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
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
    <kantega:label key="useradmin.searchroles.title"/>
</kantega:section>

<kantega:section id="content">
    <script type="text/javascript">
        function doAction(action, domain, userId) {
            var af = document.actionform;
            af.action = action;
            af.domain.value = domain;
            af.roleId.value = userId;
            af.submit();
        }
    </script>
    <admin:box>
        <h1><kantega:label key="useradmin.searchroles.title"/></h1>

        <form action="search" method="post">
            <c:if test="${numRoleConfigurations > 1}">
                <select name="domain">
                    <c:forEach items="${roleConfigurations}" var="config">
                        <option value="${config.domain}" <c:if test="${domain eq config.domain}">selected</c:if>>${config.description}</option>
                    </c:forEach>
                </select>
            </c:if>
            <input type="text" name="q" value="${query}">
            <span class="button"><input type="submit" class="search" value="<kantega:label key="aksess.button.search"/>"></span>
        </form>
        <br>
        <table border="0" cellspacing="0" cellpadding="0" class="fullWidth">
            <thead>
            <tr>
                <th width="70%"><kantega:label key="useradmin.searchroles.rolename"/></th>
                <th width="15%"></th>
                <th width="15%"></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="role" items="${roles}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td><a href="Javascript:doAction('edit', '${role.domain}', '${role.id}')">${role.name}</a></td>
                    <td>
                        <c:if test="${canEdit && role.id != adminRole}">
                            <a href="Javascript:doAction('delete', '${role.domain}', '${role.id}')" class="button delete"><span><kantega:label key="useradmin.searchroles.delete"/></span></a>
                        </c:if>
                    </td>
                    <td><a href="Javascript:doAction('userswithrole', '${role.domain}', '${role.id}')" class="button users"><span><kantega:label key="useradmin.searchroles.users"/></span></a></td></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${canEdit}">
            <div class="buttonGroup">
                <form action="edit">
                    <input type="hidden" name="domain" value="${domain}">
                    <span class="button"><input type="submit" class="add" value="<kantega:label key="useradmin.searchroles.newrole"/>"></span>
                </form>
            </div>
        </c:if>
    </admin:box>
    <form action="edit" name="actionform" method="post">
        <input type="hidden" name="domain" value="">
        <input type="hidden" name="roleId" value="">
    </form>
</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>