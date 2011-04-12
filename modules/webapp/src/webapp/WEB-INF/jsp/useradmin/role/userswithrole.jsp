<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
        function removeRole(userId, userDomain) {
            if (confirm("<kantega:label key="useradmin.userroles.remove.confirm"/>")) {
                document.forms['removerole'].userId.value = userId;
                document.forms['removerole'].userDomain.value = userDomain;
                document.forms['removerole'].submit();
            }
        }
    </script>

    <admin:box>
        <h1><kantega:label key="useradmin.roleusers.title"/></h1>

        <form action="removeuserrole" name="removerole" method="post">
            <input type="hidden" name="roleId" value="<c:out value="${roleId}"/>">
            <input type="hidden" name="roleDomain" value="<c:out value="${roleDomain}"/>">
            <input type="hidden" name="userId" value="">
            <input type="hidden" name="userDomain" value="">
            <input type="hidden" name="context" value="role">
        </form>
        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <c:forEach items="${profileSets}" var="profileSet">
                <tr class="tableHeading">
                    <td><c:out value="${profileSet.description}"/></td>
                </tr>
                <c:forEach items="${profileSet.profiles}" var="profile" varStatus="status">
                    <tr class="tableRow<c:out value="${status.index mod 2}"/>">
                        <td><a href="../profile/edit?domain=${profile.identity.domain}&amp;userId=${profile.identity.userId}"><c:out value="${profile.givenName}"/> <c:out value="${profile.surname}"/></a></td>
                        <td align="right">
                            <c:if test="${profileSet.isEditable}">
                                <a href="Javascript:removeRole('<c:out value="${role.id}"/>', '<c:out value="${role.domain}"/>')" class="button delete"><kantega:label key="useradmin.userroles.remove"/></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
            </c:forEach>
        </table>
    </admin:box>

</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>