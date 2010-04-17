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
    <c:choose>
        <c:when test="isNew"><kantega:label key="useradmin.profile.new.title"/></c:when>
        <c:otherwise><kantega:label key="useradmin.profile.edit.title"/></c:otherwise>
    </c:choose>
</kantega:section>

<kantega:section id="content">
    <form action="edit" id="EditUserProfileForm" name="myform" method="post" class="inline">
        <input type="hidden" name="isNew" value="<c:out value="${isNew}"/>">
        <input type="hidden" name="save" value="true">
        <div class="fieldset">
            <fieldset>
                <h1>
                    <c:choose>
                        <c:when test="isNew"><kantega:label key="useradmin.profile.new.title"/></c:when>
                        <c:otherwise><kantega:label key="useradmin.profile.edit.title"/></c:otherwise>
                    </c:choose>
                </h1>
                <%@ include file="../../admin/layout/fragments/infobox.jsp" %>
                <c:choose>
                    <c:when test="${numConfigurations > 1}">
                        <div class="formElement">
                            <div class="heading"><label><kantega:label key="useradmin.profile.domain"/></label></div>
                            <div class="inputs">
                                <c:choose>
                                <c:when test="${isNew}">
                                <select name="domain" class="textInput">
                                    </c:when>
                                    <c:otherwise>

                                    <select name="domainDummy" disabled class="textInput">
                                        </c:otherwise>
                                        </c:choose>
                                        <c:forEach items="${configurations}" var="config">
                                            <option value="<c:out value="${config.domain}"/>" <c:if test="${profile.identity.domain eq config.domain}">selected</c:if> <c:if test="${config.profileUpdateManager == null}">disabled</c:if>><c:out value="${config.description}"/></option>
                                        </c:forEach>
                                    </select>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="domain" value="<c:out value="${profile.identity.domain}"/>">
                    </c:otherwise>
                </c:choose>

                <div class="formElement">
                    <div class="heading"><label><kantega:label key="useradmin.profile.userid"/></label></div>
                    <div class="inputs">
                        <c:choose>
                            <c:when test="${isNew}">
                                <input type="text" name="userId" class="textInput" value="<c:out value="${profile.identity.userId}"/>" maxlength="64">
                            </c:when>
                            <c:otherwise>
                                <input type="text" name="userIdDummy" class="textInput" value="<c:out value="${profile.identity.userId}"/>" maxlength="64" disabled="disabled">
                                <input type="hidden" name="userId" value="<c:out value="${profile.identity.userId}"/>">
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="formElement">
                    <div class="heading"><label><kantega:label key="useradmin.profile.givenname"/></label></div>
                    <div class="inputs"><input type="text" name="givenName" class="textInput" value="<c:out value="${profile.givenName}"/>" maxlength="64" <c:if test="${!canEdit}">disabled="disabled"</c:if>></div>
                </div>

                <div class="formElement">
                    <div class="heading"><label><kantega:label key="useradmin.profile.surname"/></label></div>
                    <div class="inputs"><input type="text" name="surname" class="textInput" value="<c:out value="${profile.surname}"/>" maxlength="64" <c:if test="${!canEdit}">disabled="disabled"</c:if>></div>
                </div>

                <div class="formElement">
                    <div class="heading"><label><kantega:label key="useradmin.profile.email"/></label></div>
                    <div class="inputs"><input type="text" name="email" class="textInput" value="<c:out value="${profile.email}"/>" maxlength="64" <c:if test="${!canEdit}">disabled="disabled"</c:if>></div>
                </div>

                <div class="formElement">
                    <div class="heading"><label><kantega:label key="useradmin.profile.department"/></label></div>
                    <div class="inputs"><input type="text" name="department" class="textInput" value="<c:out value="${profile.department}"/>" maxlength="64" <c:if test="${!canEdit}">disabled="disabled"</c:if>></div>
                </div>

                <c:if test="${!canEdit}">
                    <div class="ui-state-highlight">
                        <kantega:label key="useradmin.profile.noteditable"/>
                    </div>
                </c:if>

                <div class="buttonGroup">
                    <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.save"/>" <c:if test="${!canEdit}">disabled="disabled"</c:if>></span>
                    <span class="button"><input type="button" class="cancel" onclick="location='search?domain=<c:out value="${profile.identity.domain}"/>'" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
            </fieldset>
        </div>
    </form>
</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>