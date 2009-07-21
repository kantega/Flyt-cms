<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege"%>
<%@ include file="../include/jsp_header.jsf" %>
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

<%
    Multimedia mm = (Multimedia)session.getAttribute("currentMultimedia");

    boolean canUpdate = true;
    if (mm.getId() > 0) {
        canUpdate = securitySession.isAuthorized(mm, Privilege.UPDATE_CONTENT);
    }

    boolean updateTree = false;
    if (request.getParameter("updatetree") != null) {
        updateTree = true;
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>titleframe.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <script language="Javascript" src="../js/multimedia.jsp"></script>
    <script language="Javascript">
    function init() {
    <%
        if (updateTree) {
    %>
        if (window.parent.parent.navtree) {
            try {
                window.parent.parent.navtree.updateTree();
            } catch (e) {
            }
        }
    <%
        }
    %>    
    }
    </script>
</head>
<body class="framework" onload="init()">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="21"><img src="../bitmaps/<%=skin%>/framework/navigatorsplit_top.gif" width="21" height="38"></td>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr height="1">
                    <td background="../bitmaps/<%=skin%>/shadow_light.gif"><img src="../bitmaps/blank.gif" width="600" height="1"></td>
                </tr>
                <tr height="36">
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <%
                                    if (mm.getId() > 0) {
                                %>
                                    <td><b><%=mm.getName()%> (<%=mm.getFileType()%>)</b></td>
                                    <td><img src="../bitmaps/common/textseparator.gif"></td>

                                    <td><a href="Javascript:gotoMMObject(<%=mm.getParentId()%>, 'folder')"><img src="../bitmaps/common/icons/topp_tilbake.gif" border="0"></a></td>
                                    <td><a href="Javascript:gotoMMObject(<%=mm.getParentId()%>, 'folder')" class="topButton"><kantega:label key="aksess.button.tilbake"/></a></td>
                                    <%
                                        if (canUpdate) {
                                            if (mm.getType() == MultimediaType.FOLDER) {
                                    %>
                                            <td><img src="../bitmaps/common/textseparator.gif"></td>
                                            <td><a href="Javascript:newMMObject(<%=mm.getId()%>, <%=MultimediaType.FOLDER.getTypeAsInt()%>)"><img src="../bitmaps/common/icons/topp_ny_mappe.gif" border="0"></a></td>
                                            <td><a href="Javascript:newMMObject(<%=mm.getId()%>, <%=MultimediaType.FOLDER.getTypeAsInt()%>)" class="topButton"><kantega:label key="aksess.button.nymappe"/></a></td>
                                            <td><img src="../bitmaps/common/textseparator.gif"></td>
                                            <td><a href="Javascript:newMMObject(<%=mm.getId()%>, <%=MultimediaType.MEDIA.getTypeAsInt()%>)"><img src="../bitmaps/common/icons/topp_legg_til_fil.gif" border="0"></a></td>
                                            <td><a href="Javascript:newMMObject(<%=mm.getId()%>, <%=MultimediaType.MEDIA.getTypeAsInt()%>)" class="topButton"><kantega:label key="aksess.button.nyfil"/></a></td>
                                    <%
                                            }
                                    %>
                                        <td><img src="../bitmaps/common/textseparator.gif"></td>
                                        <td><a href="Javascript:editMMObject()"><img src="../bitmaps/common/icons/topp_rediger.gif" border="0"></a></td>
                                        <td><a href="Javascript:editMMObject()" class="topButton"><kantega:label key="aksess.button.rediger"/></a></td>
                                    <%
                                        if (mm.getMimeType().getType().indexOf("image") != -1) {
                                    %>
                                            <td><img src="../bitmaps/common/textseparator.gif"></td>
                                            <td><a href="Javascript:manipulateMMObject()"><img src="../bitmaps/common/icons/topp_billedbehandling.gif" border="0"></a></td>
                                            <td><a href="Javascript:manipulateMMObject()" class="topButton"><kantega:label key="aksess.button.bildebehandling"/></a></td>

                                            <td><img src="../bitmaps/common/textseparator.gif"></td>
                                            <td><a href="Javascript:imagemapMMObject()"><img src="../bitmaps/common/icons/topp_billedbehandling.gif" border="0"></a></td>
                                            <td><a href="Javascript:imagemapMMObject()" class="topButton"><kantega:label key="aksess.button.imagemap"/></a></td>

                                    <%
                                        }
                                    %>
                                        <td><img src="../bitmaps/common/textseparator.gif"></td>
                                        <td><a href="Javascript:deleteMMObject(<%=mm.getId()%>, '<%=mm.getName()%>')"><img src="../bitmaps/common/icons/topp_slett.gif" border="0"></a></td>
                                        <td><a href="Javascript:deleteMMObject(<%=mm.getId()%>, '<%=mm.getName()%>')" class="topButton"><kantega:label key="aksess.button.delete"/></a></td>
                                <%
                                        }
                                    } else if (mm.getParentId() > 0) {
                                %>
                                    <td><b><kantega:label key="aksess.multimedia.title"/></b></td>
                                    <td><img src="../bitmaps/common/textseparator.gif"></td>
                                    <td><a href="Javascript:gotoMMObject(<%=mm.getParentId()%>, 'folder')"><img src="../bitmaps/common/icons/topp_tilbake.gif" border="0"></a></td>
                                    <td><a href="Javascript:gotoMMObject(<%=mm.getParentId()%>, 'folder')" class="topButton"><kantega:label key="aksess.button.tilbake"/></a></td>

                                <%
                                    } else {
                                %>
                                    <td><b><kantega:label key="aksess.multimedia.title"/></b></td>
                                    <td><img src="../bitmaps/common/textseparator.gif"></td>
                                    <td><a href="Javascript:newMMObject(0, <%=MultimediaType.FOLDER.getTypeAsInt()%>)"><img src="../bitmaps/common/icons/topp_ny_mappe.gif" border="0"></a></td>
                                    <td><a href="Javascript:newMMObject(0, <%=MultimediaType.FOLDER.getTypeAsInt()%>)" class="topButton"><kantega:label key="aksess.button.nymappe"/></a></td>
                                <% } %>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr height="1">
                    <td background="../bitmaps/<%=skin%>/shadow_dark.gif"><img src="../bitmaps/blank.gif" width="600" height="1"></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>