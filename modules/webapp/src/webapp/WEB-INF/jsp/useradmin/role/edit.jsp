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

<kantega:section id="title">
    <kantega:label key="useradmin.role.title"/>
</kantega:section>


<kantega:section id="content">
    <form action="edit" name="myform" method="post">
        <input type="hidden" name="save" value="true">
        <div class="fieldset">

        <fieldset>
            <h1>
                <kantega:label key="useradmin.role.title"/>            
            </h1>
            <%@ include file="../../admin/layout/fragments/infobox.jsp" %>
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

            <div class="buttonGroup">
                <c:if test="${canEdit}">
                    <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.save"/>"></span>
                </c:if>
                <span class="button"><input type="button" class="cancel" onclick="location='search'" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </fieldset>
        </div>            
    </form>
</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>