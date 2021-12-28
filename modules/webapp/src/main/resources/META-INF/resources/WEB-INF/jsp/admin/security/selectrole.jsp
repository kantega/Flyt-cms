<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ page import="no.kantega.publishing.security.data.Role"%>
<%@ page import="java.util.List" %>
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
    <kantega:label key="aksess.addrole.title"/>
</kantega:section>


<kantega:section id="body">
    <script type="text/javascript">
        function selectRole(role, name) {
            getParent().openaksess.editcontext.insertValueAndNameIntoForm(role, name);
            closeWindow();
        }

        function buttonOkPressed() {
            document.roles.submit();
            return false;
        }
    </script>

    <div id="SelectRoleForm">
        <form name="roles" action="${action}" method="post">
            <admin:box>
                <div style="height: 250px; overflow-y:auto">
                    <input type="hidden" name="roletype" value="Role">
                    <table>
                        <tr>
                            <c:if test="${!multiple}">
                                <td width="20">&nbsp;</td>
                            </c:if>
                            <td>&nbsp;</td>
                        </tr>
                        <%
                            List users = (List)request.getAttribute("roles");
                            for (int i = 0; i < users.size(); i++) {
                                Role r = (Role)users.get(i);
                                request.setAttribute("displayName", r.getName());
                                request.setAttribute("role", r);
                        %>
                        <tr class="tableRow<%=(i%2)%>">
                            <c:choose>
                                <c:when test="${multiple}">
                                    <td><input type="checkbox" name="role" id="role<%=i%>" value="${role.id}"></td>
                                    <td><label for="role<%=i%>">${displayName}</label></td>
                                </c:when>
                                <c:otherwise>
                                    <td><a href="javascript:selectRole('${role.id}', '${displayName}')">${displayName}</a></td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </div>
                <div class="buttonGroup">
                    <c:if test="${multiple}">
                        <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                    </c:if>
                    <span class="button"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
            </admin:box>
        </form>
    </div>

</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>
