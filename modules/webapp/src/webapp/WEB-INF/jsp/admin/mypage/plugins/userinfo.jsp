<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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

<div>
    <table class="fullWidth">
        <tr>
            <td><kantega:label key="aksess.userinformation.name"/></td>
            <td><c:out value="${currentUser.name}"/></td>
        </tr>
        <tr>
            <td><kantega:label key="aksess.userinformation.email"/></td>
            <td><c:out value="${currentUser.email}"/></td>
        </tr>
        <tr>
            <td><kantega:label key="aksess.userinformation.department"/></td>
            <td><c:out value="${currentUser.department}"/></td>
        </tr>
        <tr>
            <td><kantega:label key="aksess.userinformation.roles"/></td>
            <td>
                <c:choose>
                    <c:when test="${not empty currentUser.roles}">
                        <ul>
                            <c:forEach items="${currentUser.roles}" var="role">
                                <li>
                                    <c:choose>
                                        <c:when test="${not empty role.value.name}"><c:out value="${role.value.name}"/></c:when>
                                        <c:otherwise><c:out value="${role.value.id}"/></c:otherwise>
                                    </c:choose>

                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        None
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <c:if test="${not empty currentUser.topics}">
            <tr>
                <td><kantega:label key="aksess.userinformation.topics"/></td>
                <td>
                    <ul>
                        <c:forEach items="${currentUser.topics}" var="topic">
                            <li><c:out value="${topic.baseName}"/></li>
                        </c:forEach>
                    </ul>

                </td>
            </tr>
        </c:if>
        <c:if test="${not empty currentUser.orgUnits}">
            <tr>
                <td><kantega:label key="aksess.userinformation.orgunits"/></td>
                <td>
                    <ul>
                        <c:forEach items="${currentUser.orgUnits}" var="orgUnit">
                            <li><c:out value="${orgUnit.name}"/></li>
                        </c:forEach>
                    </ul>
                </td>
            </tr>
        </c:if>
    </table>
</div>
