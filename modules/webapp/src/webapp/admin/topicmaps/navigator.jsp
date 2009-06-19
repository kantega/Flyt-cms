<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege"%>
<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
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
    List topicMaps = topicService.getTopicMaps();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>navigator.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/tree.jsp" type="text/javascript">
</script>
<script language="Javascript" type="text/javascript">
function gotoInstances(mapId, instanceOf) {
    window.parent.content.location = "topicmap.jsp?activetab=instances&topicId=" + instanceOf + "&topicMapId=" + mapId;
}


function editPermissions() {
    hideContextMenu();

    if (activeId != null) {
        var permwin = window.open("../security/EditPermissions.action?type=<%=ObjectType.TOPICMAP%>&id=" + activeId.id,  "permwin", "toolbar=no,width=610,height=440,resizable=yes,scrollbars=no");
        permwin.focus();
    }

}
</script>
<body class="bodyWithMargin">
<form name="tree" method="get" action="navigator.jsp">
    <input type="hidden" name="select" value="false">
</form>

<table border="0">
    <tr>
        <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
        <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.topicmaps.title"/></a></td>
    </tr>
    <%
        for (int i = 0; i < topicMaps.size(); i++) {
            TopicMap topicMap = (TopicMap)topicMaps.get(i);
            int mapId = topicMap.getId();
            String mapName = topicMap.getName();
            if (securitySession.isAuthorized(topicMap, Privilege.VIEW_CONTENT)) {

    %>
    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr onMouseOver="enableMenu(<%=mapId%>, 0, 0)" onMouseOut="disableMenu()" id="item_<%=mapId%>%>">
                    <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="#" class="navNormal"><%=mapName%></a></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <table border="0">
                            <%
                                List topicTypes = topicService.getTopicTypes(mapId);
                                for (int j = 0; j < topicTypes.size(); j++) {
                                    Topic topicType = (Topic)topicTypes.get(j);

                            %>
                                    <tr>
                                        <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                        <td nobr><a href="Javascript:gotoInstances(<%=mapId%>, '<%=topicType.getId()%>')" class="navNormal"><%=topicType.getBaseName()%></a></td>
                                    </tr>
                            <%
                                }
                            %>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
<div id="contextMenu" style="position:absolute; left: 0px; top: 0px; visibility:hidden;">
    <div id="contextMenu_0">
    <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="3" width="100%">
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:editPermissions()">Angi rettigheter</a></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    </div>
</div>
</body>
</html>

<%@ include file="../include/jsp_footer.jsf" %>