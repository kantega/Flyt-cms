<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.security.data.Role" %>
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

<kantega:section id="head">
    <script language="Javascript" type="text/javascript" src="<%=Aksess.getContextPath()%>/aksess/js/autocomplete.js"></script>
</kantega:section>

<kantega:section id="body">
    <script type="text/javascript">
        function selectRole(role, name) {
            if (window.opener) {
                window.opener.insertIdAndValueIntoForm(role, name);
            }
            window.close();
        }
    </script>

    <div id="SelectRoleForm">
        <form action="" name="linkform">
            <div class="fieldset">
                <fieldset>
                    <legend><kantega:label key="aksess.addrole.title"/></legend>

                    <div style="height: 250px; overflow-y:auto">
                        <form name="roles" action="${action}" method="post">
                            <input type="hidden" name="roletype" value="Role">
                            <table border="0" width="370" cellspacing="0" cellpadding="0">
                                <tr>
                                    <c:if test="${select}">
                                        <td width="20">&nbsp;</td>
                                    </c:if>
                                    <td width="350">&nbsp;</td>
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
                                        <c:when test="${select}">
                                            <td><input type="checkbox" name="role" value="<c:out value="${role.id}'"/>"></td>
                                            <td>${displayName}</td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><a href="javascript:selectUser('<c:out value="${role.id}"/>', '<c:out value="${displayName}"/>')"><c:out value="${displayName}"/></a></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                                <%
                                    }
                                %>
                            </table>
                        </form>
                    </div>
                </fieldset>
            </div>
        </form>
        <div class="buttonGroup">
            <a href="Javascript:selectRole()" class="button ok"><span><kantega:label key="aksess.button.ok"/></span></a>
            <a href="Javascript:window.close()" class="button cancel"><span><kantega:label key="aksess.button.cancel"/></span></a>
        </div>
    </div>
</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>
