<%@ page import="no.kantega.publishing.security.data.Role" %>
<%@ page import="no.kantega.publishing.security.data.User" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title><kantega:label key="aksess.editpermissions.title"/> - ${title}</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
    function savePermissions() {
        document.permissionsForm.submit();
    }

    function addGroupOrUser(roletype) {
        var rolewin = window.open("addroleoruser.jsp?roletype=" + roletype, "rolewin", "toolbar=no,width=400,height=300,resizable=yes,scrollbars=no");
        rolewin.focus();
    }

    function changePermission(id) {
        var notificationDiv = document.getElementById("notificationDiv_" + id);
        if (notificationDiv) {
            var priv = parseInt(document.getElementById("role_" + id).value, 10);
            if (priv < ${minNotificationPrivilege}) {
                notificationDiv.style.display = 'none';
            } else {
                notificationDiv.style.display = 'block';
            }
        }
    }
</script>
<body>

<div class="padded">
<table border="0" width="580" cellspacing="0" cellpadding="0">
    <!-- Rettigheter -->
    <tr>
        <td width="200" class="tableHeading"><b><kantega:label key="aksess.editpermissions.grouporuser"/></b></td>
        <td width="100" align="center" class="tableHeading"><b><kantega:label key="aksess.editpermissions.privilege"/></b></td>
        <td width="80" class="tableHeading">&nbsp;</td>
        <td width="220" align="center" class="tableHeading"><b><kantega:label key="aksess.editpermissions.notification"/></b></td>
    </tr>
    <tr>
        <td colspan="4"><img src="../bitmaps/blank.gif" width="2" height="2"></td>
    </tr>
    <tr>
        <td colspan="4">
            <form name="permissionsForm" action="SavePermissions.action" method="post" target="_top">
                <div style="height: 250px; overflow-y:auto">
                    <table border="0" cellspacing="0" cellpadding="0" width="580">
                        <!-- Permissions -->
                        <c:forEach var="p" items="${permissionsList}" varStatus="status">
                            <tr class="tableRow${status.index mod 2}">
                                <input type="hidden" name="roletype_${p.securityIdentifier.id}" value="${p.securityIdentifier.type}">
                                <td width="200">${p.securityIdentifier.name}</td>
                                <td width="100">
                                    <c:choose>
                                        <c:when test="${canModifyPermissions}">
                                            <select name="role_${p.securityIdentifier.id}" id="role_${status.index}" onchange="changePermission('${status.index}')">
                                                <c:forEach var="privilege" items="${privileges}">
                                                    <option value="${privilege}" <c:if test="${privilege == p.privilege}">selected="selected"</c:if>><kantega:label key="aksess.editpermissions.priv${privilege}"/></option>
                                                </c:forEach>
                                            </select>
                                        </c:when>
                                        <c:otherwise>
                                            <kantega:label key="aksess.editpermissions.priv${p.privilege}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td width="80">
                                    <c:choose>
                                        <c:when test="${canModifyPermissions}">
                                            <table border="0" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td><a href="RemovePermission.action?removeId=${status.index}"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                                                    <td><a href="RemovePermission.action?removeId=${status.index}" class="button"><kantega:label key="aksess.button.slett"/></a></td>
                                                </tr>
                                            </table>
                                        </c:when>
                                        <c:otherwise>&nbsp;</c:otherwise>
                                    </c:choose>

                                </td>
                                <td width="200">
                                    <c:if test="${! empty priorities}">
                                        <div id="notificationDiv_${status.index}" <c:if test="${p.privilege < minNotificationPrivilege}">style="display:none;"</c:if>>
                                            <c:choose>
                                                <c:when test="${canModifyPermissions}">
                                                    <select name="notification_${p.securityIdentifier.id}">
                                                        <c:forEach var="priority" items="${priorities}">
                                                            <option value="${priority.notificationPriorityAsInt}" <c:if test="${priority == p.notificationPriority}">selected="selected"</c:if>><kantega:label key="aksess.editpermissions.notification${priority}"/></option>
                                                        </c:forEach>
                                                    </select>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${p.notificationPriority != null}">
                                                        <kantega:label key="aksess.editpermissions.notification${p.notificationPriority}"/>
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </form>

        </td>
    </tr>
    <c:if test="${canModifyPermissions}">
        <tr>
            <td colspan="4" align="right">
                <a href="Javascript:addGroupOrUser('<%=new Role().getType()%>')">Legg til rolle(r)</a><br>
                <a href="Javascript:addGroupOrUser('<%=new User().getType()%>')">Legg til bruker(e)</a>
            </td>
        </tr>
    </c:if>
</table>
<c:if test="${canModifyPermissions}">
    <c:if test="${objSecurityId == objectId}">
        <table border="0" cellspacing="0" cellpadding="0" class="info" width="600">
            <tr>
                <td>
                    <kantega:label key="aksess.editpermissions.editpermissionsfor"/> <b>${title}</b><br>
                    <kantega:label key="aksess.editpermissions.willbeupdated"/>
                </td>
            </tr>
        </table>
    </c:if>
    <c:if test="${objSecurityId != objectId && inheritedTitle != ''}">
        <table border="0" cellspacing="0" cellpadding="0" class="info" width="600">
            <tr>
                <td>
                    <b>${title}</b> <kantega:label key="aksess.editpermissions.inheritfrom"/> <a href="editpermissions.jsp?id=${objectId}&type=${permissionsObject.objectType}">${inheritedTitle}</a>.<br><br>
                    <kantega:label key="aksess.editpermissions.inheritfrom2"/>
                </td>
            </tr>
        </table>
    </c:if>
</c:if>
<c:if test="${!canModifyPermissions}">
    <table border="0" cellspacing="0" cellpadding="0" class="info" width="600">
        <tr>
            <td>
                <kantega:label key="aksess.editpermissions.readonly"/>
            </td>
        </tr>
    </table>
</c:if>
</div>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td background="<%=framework_bitmaps%>/navigator_bottom.gif"><img src="../bitmaps/blank.gif" width="4" height="4"></td>
    </tr>
    <tr>
        <td class="framework">
            <c:if test="${canModifyPermissions}">
                <a href="Javascript:savePermissions()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0" hspace="4" vspace="4"></a>
            </c:if>
            <a href="Javascript:window.parent.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0" hspace="4" vspace="4"></a></td>
    </tr>
    <tr>
        <td class="framework" height="30">&nbsp;</td>
    </tr>
</table>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>
