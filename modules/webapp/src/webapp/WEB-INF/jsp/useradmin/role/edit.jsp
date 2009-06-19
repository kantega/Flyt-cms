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
    <%@ include file="/admin/include/infobox.jsf" %>
    <form action="edit" name="myform" method="post">
        <input type="hidden" name="save" value="true">
        <fieldset>
            <c:choose>
                <c:when test="${numConfigurations > 1}">
                    <p>
                        <label><kantega:label key="useradmin.profile.domain"/></label>
                        <c:choose>
                            <c:when test="${isNew && numConfigurations > 1}">
                                <select name="domain" class="textInput">
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="domain" value="<c:out value="${role.domain}"/>">
                                <select name="domainDummy" disabled class="textInput">
                            </c:otherwise>
                        </c:choose>
                        <c:forEach items="${configurations}" var="config">
                            <option value="<c:out value="${config.domain}"/>" <c:if test="${role.domain eq config.domain}">selected</c:if> <c:if test="${config.roleUpdateManager == null}">disabled</c:if>><c:out value="${config.description}"/></option>
                        </c:forEach>
                        </select>
                    </p>
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="domain" value="<c:out value="${configurations[0].domain}"/>">
                </c:otherwise>
            </c:choose>
            <p>
                <label><kantega:label key="useradmin.role.name"/></label>
                <input type="text" name="roleId" class="textInput" value="<c:out value="${role.id}"/>" maxlength="64" <c:if test="${!canEdit}">disabled="disabled"</c:if>>
            </p>

            <p>
                <input type="submit" class="button" value="<kantega:label key="aksess.button.lagre"/>" <c:if test="${!canEdit}">disabled="disabled"</c:if>>
                <input type="button" class="button" onclick="location='search'" value="<kantega:label key="aksess.button.avbryt"/>">
            </p>
        </fieldset>
    </form>
</kantega:section>

<%@ include file="../include/design/standard.jsp" %>