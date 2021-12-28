<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
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
    <kantega:label key="useradmin.userroles.title"/>
</kantega:section>

<kantega:section id="content">
    <script type="text/javascript">
        function removeRole(roleId, roleDomain) {
            if (confirm("<kantega:label key="useradmin.userroles.remove.confirm"/>")) {
                document.forms['removerole'].roleId.value = roleId;
                document.forms['removerole'].roleDomain.value = roleDomain;
                document.forms['removerole'].submit();
            }
        }
    </script>

    <admin:box>
        <h1><kantega:label key="useradmin.userroles.title"/></h1>
        <form action="removeuserrole" name="removerole" method="post">
            <input type="hidden" name="userId" value="${userId}">
            <input type="hidden" name="userDomain" value="${userDomain}">
            <input type="hidden" name="roleId" value="">
            <input type="hidden" name="roleDomain" value="">
            <input type="hidden" name="context" value="user">
        </form>
        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <c:forEach items="${roleSets}" var="roleSet">
                <tr class="tableHeading">
                    <td>${roleSet.description}</td>
                    <td></td>
                </tr>
                <c:forEach items="${roleSet.userRoles}" var="role" varStatus="status">
                    <tr class="tableRow${status.index mod 2}">
                        <td>${role.name}</td>
                        <td align="right">
                            <c:if test="${roleSet.isEditable}">
                                <a href="Javascript:removeRole('${role.id}', '${role.domain}')"><kantega:label key="useradmin.userroles.remove"/></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${roleSet.isEditable}">
                    <tr>
                        <td colspan="2" align="right">
                            <kantega:label key="useradmin.userroles.add"/>:<br>
                            <form action="adduserrole" method="post">
                                <input type="hidden" name="userId" value="${userId}">
                                <input type="hidden" name="userDomain" value="${userDomain}">
                                <input type="hidden" name="roleDomain" value="${roleSet.domain}">
                                <select name="roleId">
                                    <c:forEach items="${roleSet.availableRoles}" var="role">
                                        <option value="${role.id}">${role.name}</option>
                                    </c:forEach>
                                </select>
                                <span class="button"><input type="submit" class="add" value="<kantega:label key="aksess.button.add"/>"></span>
                            </form>
                        </td>
                    </tr>
                </c:if>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
            </c:forEach>
        </table>
    </admin:box>

</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>