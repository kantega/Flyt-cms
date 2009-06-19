<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.commons.util.StringHelper,
                 no.kantega.publishing.common.data.Multimedia,
                 no.kantega.publishing.common.data.MultimediaMapEntry,
                 no.kantega.publishing.common.data.PathEntry,
                 no.kantega.publishing.common.data.enums.MultimediaType,
                 no.kantega.publishing.common.data.enums.ObjectType"%>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="no.kantega.commons.exception.SystemException" %>
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
    RequestParameters param = new RequestParameters(request, "utf-8");
    String openFoldersList = param.getString("openFolders");
    if (openFoldersList == null || openFoldersList.length() == 0) {
        openFoldersList = "0";
    }

    // Liste med åpne foldere
    int[] openFolders = StringHelper.getInts(openFoldersList, ",");

    int selectedId = 0;

    Multimedia currentMultimedia = (Multimedia)session.getAttribute("currentMultimedia");
    if (currentMultimedia != null) {
        selectedId = currentMultimedia.getId();
    }

    // Objekt som skal ha focus
    int focusId = param.getInt("focusId");
    if (focusId == -1) {
        focusId = selectedId;
    }

    // Id (er) som ligger å clipboard
    String clipboard = param.getString("clipboard");
    if (clipboard == null) {
        clipboard = "";
    }

    // Angir om tre skal åpnes
    boolean expand = param.getBoolean("expand", true);

    // Angir om brukeren skal kun velge et objekt (i popups)
    boolean select = param.getBoolean("select");
    boolean getOnlyFolders = select;

    if (expand && currentMultimedia != null) {
        // Vi må legge til id'er slik at treet åpnes og viser denne...
        List parents = mediaService.getMultimediaPath(currentMultimedia);
        if (parents != null && parents.size() > 0) {

            for (int i = 0; i < parents.size(); i++) {
                PathEntry parent = (PathEntry)parents.get(i);
                boolean exists = false;
                for (int j = 0; j < openFolders.length; j++) {
                    if (parent.getId() == openFolders[j]) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    openFoldersList += "," + parent.getId();
                }
            }
            openFolders = StringHelper.getInts(openFoldersList, ",");
        }
    }

    MultimediaMapEntry sitemap = mediaService.getPartialMultimediaMap(openFolders, getOnlyFolders);
%>

<%!
    void printFolder(MultimediaMapEntry sitemap, int selectedId, int[] openList, boolean doSelect, JspWriter out, SecuritySession session) throws java.io.IOException, SystemException {

        if (sitemap != null) {

            if (!session.isAuthorized(sitemap, Privilege.VIEW_CONTENT)) {
                // Ikke vis dersom bruker ikke har tilgang til mappe
                return;
            }

            int id = sitemap.getId();
            int parentId = sitemap.getParentId();
            MultimediaType type = sitemap.getType();
            if (id == 0) {
                // Spesialhåndtering av root-mappe skal ikke kunne flyttes etc
                type = MultimediaType.ROOT_FOLDER;
            }

			String title = sitemap.getTitle();
            title = StringHelper.removeIllegalCharsInTitle(title);

            int noChildren = 0;
            List children = sitemap.getChildren();
            if (children != null) {
                noChildren = children.size();
            }

            boolean isSelected = false;
            // Marker objekt
            if (id == selectedId) {
                isSelected = true;
            }

            boolean isOpen = false;
            for (int i = 0; i < openList.length; i++) {
                if (id == openList[i]) {
                    isOpen = true;
                    break;
                }
            }

            String img = "closed";
            if (isOpen) {
                img = "open";
            }

            String action = "toogleSubTree";
            if (noChildren == 0 && !isOpen) {
                action = "loadSubTree";
            }

            String selectAction = "gotoObject(" + id + ")";
            if (doSelect) {
                selectAction = "selectObject(" + id + ",'" + StringHelper.replace(title, "'", "\'") + "')";
            }

            out.write("<tr onMouseOver=\"enableMenu(" + id + "," + id + ",'" + type + "')\" onMouseOut=\"disableMenu()\" id=\"item_" + id + "\">\n");
            if ((isOpen && noChildren == 0) || (type == MultimediaType.MEDIA)) {
                out.write("<td><img src=\"../bitmaps/blank.gif\" width=11 height=11></td>");
            } else {
                out.write("<td onClick=\"" + action + "('" + id + "')\"><img src=\"../bitmaps/common/navigator/nav_" + img + ".gif\" id=\"img_" + id + "\" width=7 height=7 hspace=2 vspace=2></td>\n");
            }

            if (isSelected && (!doSelect)) {
                out.write("<td nobr><a href=\"Javascript:" + selectAction + "\" class=\"navSelected\">" + title + "</a></td>\n");
            } else {
                out.write("<td nobr><a href=\"Javascript:" + selectAction + "\" class=\"navNormal\">" + title + "</a></td>\n");
            }

            out.write("</tr>\n");

            if (isOpen) {
                if (noChildren > 0) {
                    out.write("<tr>\n<td></td>\n<td>\n<table border=\"0\" id=\"tree_" + id + "\">\n");
                }
                for (int i = 0; i < noChildren; i++) {
                    MultimediaMapEntry child = (MultimediaMapEntry)children.get(i);
                    printFolder(child, selectedId, openList, doSelect, out, session);
                }
                if (noChildren > 0) {
                    out.write("</table>\n</td>\n</tr>");
                }
            }
        }
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>navigator.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/tree.jsp">
</script>
<script language="Javascript">

function gotoObject(id) {
    window.parent.content.location = "multimedia.jsp?id=" + id;
}


function selectObject(id, title) {
    var w = window.parent.opener;
    if (w) {
        if (w.doInsertTag) {
            w.insertValueIntoForm("/multimedia.ap?id=" + id);
        } else {
            w.insertIdAndValueIntoForm(id, title);
        }
        window.parent.close();
    }
}


function viewObject() {
    hideContextMenu();

    if (activeId != null) {
        gotoObject(activeId.id);
    }
}

function newFile() {
    hideContextMenu();
    window.parent.content.location = "multimedia.jsp?activetab=editmultimedia&parentId=" + activeId.id + "&type=<%=MultimediaType.MEDIA%>";
}

function newFolder() {
    hideContextMenu();
    window.parent.content.location = "multimedia.jsp?activetab=editmultimedia&parentId=" + activeId.id + "&type=<%=MultimediaType.FOLDER%>";
}


function editPermissions() {
    hideContextMenu();

    if (activeId != null) {
        var permwin = window.open("../security/EditPermissions.action?type=<%=ObjectType.MULTIMEDIA%>&id=" + activeId.id,  "permwin", "toolbar=no,width=610,height=440,resizable=yes,scrollbars=no");
        permwin.focus();
    }

}

</script>
<body class="bodyWithMargin" onLoad="initTree()">

<form name="tree" method="get" action="navigator.jsp">
    <input type="hidden" name="openFolders" value="<%=openFoldersList%>">
    <input type="hidden" name="focusId" value="<%=focusId%>">
    <input type="hidden" name="expand" value="true">
    <input type="hidden" name="select" value="<%=select%>">
    <input type="hidden" name="clipboard" value="<%=clipboard%>">
    <input type="hidden" name="isCopy" value="false">
</form>

<table border="0">
<%
    printFolder(sitemap, selectedId, openFolders, select, out, securitySession);
%>
</table>
<div id="contextMenu" style="position:absolute; left: 0px; top: 0px; visibility:hidden;">
    <div id="contextMenu_<%=MultimediaType.FOLDER%>">
    <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="3" width="100%">
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject()"><kantega:label key="aksess.navigator.open"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenuFrame" height="1"></td>
                    </tr>
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:newFile()"><kantega:label key="aksess.navigator.newfile"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:newFolder()"><kantega:label key="aksess.navigator.newfolder"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenuFrame" height="1"></td>
                    </tr>
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(false)"><kantega:label key="aksess.navigator.cut"/></a></td>
                    </tr>
                    <%
                        String cssClass = "cMenuDisabled";
                        if (clipboard.length() > 0) {
                            cssClass = "cMenu";
                        }
                    %>
                    <tr>
                        <td class="<%=cssClass%>" id="cMenu_paste0" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject()"><kantega:label key="aksess.navigator.paste"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenuFrame" height="1"></td>
                    </tr>
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:editPermissions()"><kantega:label key="aksess.navigator.permissions"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    </div>
    <div id="contextMenu_<%=MultimediaType.MEDIA%>">
    <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="3" width="100%">
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject()"><kantega:label key="aksess.navigator.open"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenuFrame" height="1"></td>
                    </tr>
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(false)"><kantega:label key="aksess.navigator.cut"/></a></td>
                    </tr>
                    <%
                        cssClass = "cMenuDisabled";
                        if (clipboard.length() > 0) {
                            cssClass = "cMenu";
                        }
                    %>
                    <tr>
                        <td class="<%=cssClass%>" id="cMenu_paste1" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject()"><kantega:label key="aksess.navigator.paste"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>

    </table>
    </div>
    <div id="contextMenu_<%=MultimediaType.MEDIA%>">
    <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="3" width="100%">
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject()"><kantega:label key="aksess.navigator.open"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenuFrame" height="1"></td>
                    </tr>
                    <%
                        cssClass = "cMenuDisabled";
                        if (clipboard.length() > 0) {
                            cssClass = "cMenu";
                        }
                    %>
                    <tr>
                        <td class="<%=cssClass%>" id="cMenu_paste2" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject()"><kantega:label key="aksess.navigator.paste"/></a></td>
                    </tr>
                    <tr>
                        <td class="cMenuFrame" height="1"></td>
                    </tr>
                    <tr>
                        <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:editPermissions()"><kantega:label key="aksess.navigator.permissions"/></a></td>
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