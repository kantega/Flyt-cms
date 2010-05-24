<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.security.data.User" %>
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
    <kantega:label key="aksess.adduser.title"/>
</kantega:section>

<kantega:section id="body">
    <script type="text/javascript">
        function selectUser(user, name) {
            getParent().openaksess.editcontext.insertIdAndValueIntoForm(user, name);
            closeWindow();
        }

        function buttonOkPressed() {
            document.roles.submit();
            return false;
        }

    </script>

    <div id="SelectRoleForm">
        <admin:box>
            <form name="searchform" action="SelectUsers.action" method="post">
                <input type="hidden" name="action" value="${action}">
                <input type="hidden" name="multiple" value="${multiple}">
                <strong><kantega:label key="aksess.adduser.search"/>:</strong> <input type="text" name="name" value="${name}" size="10" maxlength="30">
                <span class="button"><input type="button" onclick="document.searchform.submit()" class="search" value="<kantega:label key="aksess.button.search"/>"></span>
                <c:if test="${notFound}">
                    <div class="ui-state-highlight"><kantega:label key="aksess.adduser.search.notfound"/></div>
                </c:if>
            </form>

            <div style="height: 340px; overflow-y:auto">
                <form name="roles" action="${action}" method="post">
                    <input type="hidden" name="roletype" value="User">
                    <table width="340">
                        <tr>
                            <c:if test="${multiple}">
                                <td width="20">&nbsp;</td>
                            </c:if>
                            <td width="320">&nbsp;</td>
                        </tr>
                        <%
                            List users = (List)request.getAttribute("users");
                            for (int i = 0; i < users.size(); i++) {
                                User u = (User)users.get(i);
                                String displayName = u.getName();
                                String department = u.getDepartment();
                                if (department != null && department.length() > 0) {
                                    displayName += " (" + department + ")";
                                }
                                request.setAttribute("displayName", displayName);
                                request.setAttribute("user", u);
                        %>
                        <tr class="tableRow<%=(i%2)%>">
                            <c:choose>
                                <c:when test="${multiple}">
                                    <td><input type="checkbox" name="role" id="role<%=i%>" value="<c:out value="${user.id}"/>"></td>
                                    <td title="<c:out value="${user.id}"/>"><label for="role<%=i%>">${displayName}</label></td>
                                </c:when>
                                <c:otherwise>
                                    <td><a title="<c:out value="${user.id}"/>" href="javascript:selectUser('<c:out value="${user.id}"/>', '<c:out value="${displayName}"/>')"><c:out value="${displayName}"/></a></td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </form>
            </div>
            <div class="buttonGroup">
                <c:if test="${multiple}">
                    <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                </c:if>
                <span class="button"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </admin:box>
    </div>
</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>
