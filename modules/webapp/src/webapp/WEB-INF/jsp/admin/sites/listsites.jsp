<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.util.List"%>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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

<html>
<head>
    <title>listsites.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
    function editSite(id) {
        location = "UpdateSite.action?siteId=" + id;
    }

    function createSiteRoot(id) {
        location = "CreateRoot.action?siteId=" + id;
    }

</script>

<body class="bodyWithMargin">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><strong><kantega:label key="aksess.site.name"/></strong></td>
            <td><strong><kantega:label key="aksess.site.alias"/></strong></td>
            <td>&nbsp;</td>
        </tr>
        <c:forEach var="site" items="${sites}" varStatus="status">
            <tr class="tableRow${status.index mod 2}">
                <td>${site.name}</td>
                <td>${site.alias}</td>
                <td>
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td><a href="Javascript:editSite(${site.id})"><img src="../bitmaps/common/buttons/mini_rediger.gif" border="0"></a></td>
                            <td><a href="Javascript:editSite(${site.id})" class="button"><kantega:label key="aksess.site.editdomains"/></a></td>
                            <c:forEach var="siteId" items="${existingSiteIds}">
                                <c:if test="${siteId == site.id}">
                                    <c:set var="siteHasBeenCreated" value="true" />
                                </c:if>
                            </c:forEach>
                            <c:if test="${!siteHasBeenCreated}">
                                <%-- Do not show the createhomepage link if the site already has been created --%>
                                <td><img src="../bitmaps/common/textseparator.gif" alt=""></td>
                                <td><a href="Javascript:createSiteRoot(${site.id})"><img src="../bitmaps/common/buttons/mini_legg_til.gif" border="0"></a></td>
                                <td><a href="Javascript:createSiteRoot(${site.id})" class="button"><kantega:label key="aksess.site.createhomepage"/></a></td>
                            </c:if>
                            <c:set var="siteHasBeenCreated" value="false" />
                        </tr>
                    </table>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>
