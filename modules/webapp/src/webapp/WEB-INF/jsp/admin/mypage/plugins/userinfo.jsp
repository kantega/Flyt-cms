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

<aksess:getuser name="currentUser"/>

<table class="fullWidth">
    <tr>
        <th><strong><kantega:label key="aksess.userinformation.property"/></strong></th>
        <th><strong><kantega:label key="aksess.userinformation.value"/></strong></th>
    </tr>

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
                            <li><c:out value="${role.key}"/></li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    None
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td><kantega:label key="aksess.userinformation.topics"/></td>
        <td>
            <c:choose>
                <c:when test="${not empty currentUser.topics}">
                    <ul>
                        <c:forEach items="${currentUser.topics}" var="topic">
                            <li><c:out value="${topic.baseName}"/></li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    None
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td><kantega:label key="aksess.userinformation.orgunits"/></td>
        <td>
            <c:choose>
                <c:when test="${not empty currentUser.orgUnits}">
                    <ul>
                        <c:forEach items="${currentUser.orgUnits}" var="orgUnit">
                            <li><c:out value="${orgUnit.name}"/></li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    None
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td><kantega:label key="aksess.userinformation.attributes"/></td>
        <td>
            <c:choose>
                <c:when test="${not empty currentUser.attributes}">
                    <ul>
                        <c:forEach items="${currentUser.attributes}" var="attribute">
                            <li><c:out value="${attribute.key}"/></li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    None
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>
