<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<kantega:section id="head">

</kantega:section>

<kantega:section id="innhold">

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
    <%@ include file="/admin/include/infobox.jsf" %>
</kantega:section>

<%@ include file="../include/design/standard.jsp" %>