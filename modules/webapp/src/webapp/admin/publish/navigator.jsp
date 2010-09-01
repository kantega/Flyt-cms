<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.commons.util.StringHelper,
                 no.kantega.publishing.admin.util.NavigatorUtil"%>
<%@ page import="no.kantega.publishing.common.data.*" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentType" %>
<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
<%@ page import="no.kantega.publishing.common.exception.ContentNotFoundException" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.admin.util.DateUtil" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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

    int selectedId = -1;

    int startId = param.getInt("startId");

    String sort = param.getString("sort");
    if (sort == null) {
        sort = (String)session.getAttribute("navigatorSortOrder");
        if (sort == null) {
            sort = ContentProperty.PRIORITY;
        }
    }
    session.setAttribute("navigatorSortOrder", sort);

    Content showContent = (Content)session.getAttribute("showContent");
    if (showContent != null) {
        selectedId = showContent.getAssociation().getId();
    }

    String openFoldersList = param.getString("openFolders");
    if (openFoldersList == null || openFoldersList.length() == 0) {
        try {
            ContentIdentifier cid = new ContentIdentifier(request, "/");
            openFoldersList = "0," + cid.getAssociationId();
            if (startId != -1) {
                openFoldersList = openFoldersList + "," + startId;
            }

            if (selectedId == -1) {
                selectedId = cid.getAssociationId();
            }
        } catch (ContentNotFoundException e) {
            openFoldersList = "0";
        }
    }

    // Liste med åpne foldere
    int[] openFolders = StringHelper.getInts(openFoldersList, ",");

    // Objekt som skal ha focus
    int focusId = param.getInt("focusId");
    if (focusId == -1) {
        focusId = selectedId;
    }

    // Operasjon som skal utføres (cut eller copy)
    boolean isCopy = param.getBoolean("isCopy");

    // Id (er) som ligger på clipboard
    String clipboard = param.getString("clipboard");
    if (clipboard == null) {
        clipboard = "";
    }

    // Angir om tre skal åpnes
    boolean expand = param.getBoolean("expand", true);

    // Angir om brukeren skal kun velge et objekt (i popups)
    boolean selectAssociationId = param.getBoolean("selectAssociationId");

    // Angir om brukeren skal velge contentId eller associationId
    boolean selectContentId = param.getBoolean("selectContentId");
    boolean select = false;
    if (selectAssociationId || selectContentId) {
        select = true;
    }


    if (expand && showContent != null) {
        // Vi må legge til id'er slik at treet åpnes og viser denne...
        Association a = showContent.getAssociation();
        if (a != null) {
            String path = a.getPath();
            if (path.length() > 1) {
                int pathIds[] = StringHelper.getInts(path, "/");
                if (pathIds != null) {
                    for (int i = 0; i < pathIds.length; i++) {
                        int pId = pathIds[i];
                        boolean exists = false;
                        for (int j = 0; j < openFolders.length; j++) {
                            if (pId == openFolders[j]) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            openFoldersList += "," + pId;
                        }
                    }
                }
                openFolders = StringHelper.getInts(openFoldersList, ",");
            }
        }
    }

    ContentManagementService aksess = new ContentManagementService(request);
%>
<%!
    boolean isFolderOpen(int id, int[] openList) {
        for (int i = 0; i < openList.length; i++) {
            if (id == openList[i]) {
                return true;
            }
        }
        return false;
    }
%>
<%!
    void printFolder(SiteMapEntry sitemap, int level, int selectedId, int[] openList, boolean doSelectAssociationId, boolean doSelectContentId, int startId, boolean doPrint, JspWriter out) throws java.io.IOException {

        if (sitemap != null) {
            int id = sitemap.getId();
            int parentId = sitemap.getParentId();
            if (!doPrint && startId == id) {
                doPrint = true;
            }
            ContentType type = sitemap.getType();
            int contentId = sitemap.getContentId();

            String title = sitemap.getTitle();
            if (title.length() > 32) title = title.substring(0, 29) + "...";
            if (type == ContentType.SHORTCUT) {
                title = title + " (snarvei)";
            }

            title = StringHelper.removeIllegalCharsInTitle(title);

            int noChildren = 0;
            List children = sitemap.getChildren();
            if (children != null) {
                noChildren = children.size();
            }

            boolean isSelected = false;
            // Marker objekt (snarveier kan ikke markeres, man går direkte til objektet)
            if (id == selectedId && type != ContentType.SHORTCUT) {
                isSelected = true;
            }

            boolean isOpen = isFolderOpen(id, openList);
            String img = "closed";
            if (isOpen) {
                img = "open";
            }

            String action = "toogleSubTree";
            if (noChildren == 0 && !isOpen) {
                action = "loadSubTree";
            }

            String selectAction = "gotoObject(" + id + ")";
            if (doSelectAssociationId) {
                selectAction = "selectAssociationId(" + id + ",'" + title + "')";
            } else if (doSelectContentId) {
                selectAction = "selectContentId(" + contentId + ",'" + title + "')";
            }

            if (doPrint) {
                String icon = "";
                String iconText = "";
                int visibilityStatus = sitemap.getVisibilityStatus();
                if (parentId == 0) {
                    icon = "root.gif";
                    iconText = "Hjemmeside - " + title;
                } else {
                    icon = NavigatorUtil.getIcon(type, visibilityStatus, sitemap.getStatus());
                    iconText = NavigatorUtil.getIconText(type, visibilityStatus, sitemap.getStatus());
                }

                out.write("<tr onMouseOver=\"enableMenu(" + id + "," + sitemap.getUniqueId() + ",'" + type + "')\" onMouseOut=\"disableMenu()\" id=\"item_" + id + "\">\n");
                if ((isOpen && noChildren == 0) || (type == ContentType.SHORTCUT)) {
                    out.write("<td width=11><img src=\"../bitmaps/blank.gif\" width=11 height=11></td>");
                } else {
                    out.write("<td width=11 valign=\"top\" onClick=\"" + action + "('" + id + "')\"><img src=\"../bitmaps/common/navigator/nav_" + img + ".gif\" id=\"img_" + id + "\" width=7 height=7 hspace=0 vspace=2></td>\n");
                }
                out.write("<td width=12 valign=\"top\"><img src=\"../bitmaps/common/navigator/" + icon + "\" width=12 height=14 alt=\"" + iconText + "\"></td>");
                String clazz = "navNormal";
                if (isSelected && (!doSelectAssociationId) && (!doSelectContentId)) {
                    clazz = "navSelected";
                }
                out.write("<td nobr width=\"100%\"><a href=\"Javascript:" + selectAction + "\" class=\"" +clazz +"\">" + title + "</a>");
                if(sitemap.getNumberOfNotes() > 0) {
                    out.write("<img src=\"../bitmaps/common/navigator/note.gif\" width=\"12\" height=\"10\" alt=\"" + sitemap.getNumberOfNotes() + " notat(er) \"");
                }
                out.write("</td>\n");

                out.write("</tr>\n");
            }
            if (isOpen) {
                if (noChildren > 0 && doPrint) {
                    out.write("<tr>\n<td></td><td colspan=\"2\">\n<table border=\"0\" id=\"tree_" + id + "\">\n");
                }
                for (int i = 0; i < noChildren; i++) {
                    SiteMapEntry child = (SiteMapEntry)children.get(i);
                    boolean print = startId == -1;
                    printFolder(child, level+1, selectedId, openList, doSelectAssociationId, doSelectContentId, startId, doPrint, out);
                }
                if (noChildren > 0 && doPrint) {
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
    function checkModified() {
        var ok = true;

        try {
            if (window.parent.content.contenttop.isModified()) {
                if (confirm('<kantega:label key="aksess.editcontent.contentnotsaved"/>')) {
                    ok = true;
                } else {
                    ok = false;
                }
            }
        } catch (e) {
        }

        return ok;
    }


    function gotoObject(id, version) {
        var ok = checkModified();

        if (ok) {
            var cid = "thisId=" + id;
            if (version) {
                cid += "&version=" + version;
            }
            window.parent.content.location = "content.jsp?activetab=previewcontent&" + cid;
        }
    }

    function selectAssociationId(id, title) {
        if (window.parent) {
            var w = window.parent.opener;
            if (w && (typeof w.insertValueIntoForm == 'function' || typeof w.insertIdAndValueIntoForm == 'function')) {
                if (w.doInsertTag) {
                    w.insertValueIntoForm("/content.ap?thisId=" + id);
                } else {
                    w.insertIdAndValueIntoForm(id, title);
                }
                window.parent.close();
            } else if (window.parent.selectAssociationId) {
                window.parent.selectAssociationId(id, title);
            }
        }
    }

    function selectContentId(id, title) {
        if (window.parent) {
            var w = window.parent.opener;
            if (w && (typeof w.insertValueIntoForm == 'function' || typeof w.insertIdAndValueIntoForm == 'function')) {
                if (w.doInsertTag) {
                    w.insertValueIntoForm("/content.ap?contentId=" + id + "&amp;contextId=$contextId");
                } else {
                    w.insertIdAndValueIntoForm(id, title);
                }
                window.parent.close();
            } else {
                window.parent.selectContentId(id, title);
            }
        }
    }

    function newObject() {
        hideContextMenu();

        var ok = checkModified();

        if (ok && activeId != null) {
            window.parent.content.location = "EditContent.action?thisId=" + activeId.id + "&action=selecttemplate";
        }
    }


    function deleteObject(deleteShortcut) {
        hideContextMenu();

        var ok = checkModified();

        if (ok && activeId != null) {
            var confirmwin = window.open("confirmdelete.jsp?target=parent.content&id=" + activeId.uniqueId, "confirmwin", "toolbar=no,width=350,height=285,resizable=yes,scrollbars=yes");
            confirmwin.focus();
        }
    }


    function restoreObject(id) {
        hideContextMenu();

        var confirmwin = window.open("restore.jsp?id=" + id, "confirmwin", "toolbar=no,width=350,height=85,resizable=yes,scrollbars=yes");
        confirmwin.focus();
    }


    function viewObject(openInNewWindow) {
        hideContextMenu();

        if (activeId != null) {
            if (openInNewWindow) {
                var newwin = window.open("../../content.ap?thisId=" + activeId.id);
                newwin.focus();
            } else {
                gotoObject(activeId.id);
            }
        }
    }


    function editPermissions() {
        hideContextMenu();

        if (activeId != null) {
            var permwin = window.open("../security/EditPermissions.action?&type=<%=ObjectType.ASSOCIATION%>&id=" + activeId.id,  "permwin", "toolbar=no,width=610,height=440,resizable=yes,scrollbars=no");
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
    <input type="hidden" name="selectAssociationId" value="<%=selectAssociationId%>">
    <input type="hidden" name="selectContentId" value="<%=selectContentId%>">
    <input type="hidden" name="sort" value="<%=sort%>">
    <input type="hidden" name="clipboard" value="<%=clipboard%>">
    <input type="hidden" name="isCopy" value="<%=isCopy%>">
    <input type="hidden" name="startId" value="<%=startId%>">
</form>

<table border="0" cellspacing="0" cellpadding="0">
<%
    if (!selectAssociationId && !selectContentId) {
        List myWorkList = aksess.getMyContentList();
        List myDeletedItems = aksess.getDeletedItems();
        if (myWorkList.size() > 0 || myDeletedItems.size() > 0) {
%>
<tr>
    <td width="11" valign="top"><img src="../bitmaps/common/navigator/my_nav_open.gif" width=7 height=7 hspace=0 vspace=2></td>
    <td width="12" valign="top"><img src="../bitmaps/common/navigator/page.gif" width=12 height=14></td>
    <td width="100%" nobr><a href="#" class="navMyNormal"><kantega:label key="aksess.navigator.mycontent.title"/></a></td>
</tr>
<tr>
    <td></td>
    <td></td>
    <td>
        <table border="0">
            <%
                for (int i = 0; i < myWorkList.size(); i++) {
                    WorkList items = (WorkList)myWorkList.get(i);

                    if (items.size() > 0) {
                        int treeId = 100000 + i;
                        String label = "aksess.navigator.mycontent." + items.getDescription();
                        boolean isOpen = isFolderOpen(treeId, openFolders);
                        String display = "none";
                        String navIcon = "closed";
                        if (isOpen) {
                            display = "block";
                            navIcon = "open";
                        }
            %>
            <tr id="item_<%=treeId%>">
                <td width="11" valign="top" onClick="toogleSubTree('<%=treeId%>')"><img src="../bitmaps/common/navigator/my_nav_<%=navIcon%>.gif" width=7 id="img_<%=treeId%>" height=7 hspace=0 vspace=2></td>
                <td width="12" valign="top"><img src="../bitmaps/common/navigator/page.gif" width=12 height=14></td>
                <td width="100%" nobr><a href="Javascript:toogleSubTree('<%=treeId%>')" class="navMyNormal"><kantega:label key="<%=label%>"/> (<%=items.size()%>)</a></td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <table border="0" id="tree_<%=treeId%>" style="display:<%=display%>;">
                        <%
                            for (int j = 0; j < items.size(); j++) {
                                Content c = (Content)items.get(j);
                                String title = c.getTitle();
                                if (title.length() > 32) {
                                    title = title.substring(0, 29) + "...";
                                }
                                String cssClass = "navMyNormal";
                                if (c.getId() == selectedId) {
                                    cssClass = "navMySelected";
                                }
                                String icon = NavigatorUtil.getIcon(c.getType(), c.getVisibilityStatus(), c.getStatus());
                                String iconText = NavigatorUtil.getIcon(c.getType(), c.getVisibilityStatus(), c.getStatus());

                        %>
                        <tr>
                            <td width="11"><img src="../bitmaps/blank.gif" width=11 height=11></td>
                            <td width="12" valign="top"><img src="../bitmaps/common/navigator/<%=icon%>" alt="<%=iconText%>" width=12 height=14></td>
                            <td width="100%" nobr><a href="Javascript:gotoObject(<%=c.getAssociation().getId()%>)" class="<%=cssClass%>"><%=title%></a>
                                <%
                                    if(c.getNumberOfNotes() > 0) {
                                        out.write("<img src=\"../bitmaps/common/navigator/note.gif\" width=\"12\" height=\"10\" alt=\"" + c.getNumberOfNotes() + " notat(er) \"");
                                    }
                                %>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </td>
            </tr>
            <%
                    }
                }

                if (myDeletedItems.size() > 0) {
                    int treeId = 100099;
                    boolean isOpen = isFolderOpen(treeId, openFolders);
                    String display = "none";
                    String navIcon = "closed";
                    if (isOpen) {
                        display = "block";
                        navIcon = "open";
                    }
            %>
            <tr id="item_<%=treeId%>">
                <td width="11" valign="top" onClick="toogleSubTree('<%=treeId%>')"><img src="../bitmaps/common/navigator/my_nav_<%=navIcon%>.gif" width=7 id="img_<%=treeId%>" height=7 hspace=0 vspace=2></td>
                <td width="12" valign="top"><img src="../bitmaps/common/navigator/page.gif" width=12 height=14></td>
                <td width="100%" nobr><a href="Javascript:toogleSubTree('<%=treeId%>')" class="navMyNormal"><kantega:label key="aksess.navigator.mycontent.deleted"/> (<%=myDeletedItems.size()%>)</a></td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <table border="0" id="tree_<%=treeId%>" style="display:<%=display%>;">
                        <%
                            for (int j = 0; j < myDeletedItems.size(); j++) {
                                DeletedItem item = (DeletedItem)myDeletedItems.get(j);

                                String title = item.getTitle();
                                if (title.length() > 32) {
                                    title = title.substring(0, 29) + "...";
                                }
                        %>
                        <tr>
                            <td width="11"><img src="../bitmaps/blank.gif" width=11 height=11></td>
                            <td width="12" valign="top"><img src="../bitmaps/common/navigator/deleted.gif" alt="Slettet" width=12 height=14></td>
                            <td width="100%" nobr><a href="Javascript:restoreObject(<%=item.getId()%>)" class="navMyNormal"><%=title%></a></td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </td>
            </tr>
            <%
                }
            %>
        </table>
    </td>
</tr>

<%
    }

    List contentForApproval = aksess.getContentListForApproval();
    if (contentForApproval != null && contentForApproval.size() > 0) {
        int treeId = 2000000;
        boolean isOpen = isFolderOpen(treeId, openFolders);
        String display = "none";
        String navIcon    = "closed";
        if (isOpen) {
            display = "block";
            navIcon    = "open";
        }

%>
<tr id="item_<%=treeId%>">
    <td valign="top" onClick="toogleSubTree('<%=treeId%>')"><img src="../bitmaps/common/navigator/my_nav_<%=navIcon%>.gif" width=7 id="img_<%=treeId%>" height=7 hspace=0 vspace=2></td>
    <td valign="top"><img src="../bitmaps/common/navigator/page.gif" width=12 height=14></td>
    <td nobr><a href="Javascript:toogleSubTree('<%=treeId%>')" class="navMyNormal"><kantega:label key="aksess.navigator.approval.title"/> (<%=contentForApproval.size()%>)</a></td>
</tr>
<tr>
    <td></td>
    <td colspan="2">
        <table border="0" id="tree_<%=treeId%>" style="display:<%=display%>;">
            <%
                String cssClass = "navMyNormal";
                for (int i = 0; i < contentForApproval.size(); i++) {
                    Content c = (Content)contentForApproval.get(i);
                    String title = c.getTitle();
                    if (title.length() > 32) {
                        title = title.substring(0, 29) + "...";
                    }
                    if (c.getId() == selectedId) {
                        cssClass = "navMySelected";
                    }
                    String icon = NavigatorUtil.getIcon(c.getType(), c.getVisibilityStatus(), c.getStatus());
                    String iconText = NavigatorUtil.getIconText(c.getType(), c.getVisibilityStatus(), c.getStatus());
                    String dateText = DateUtil.getAgeAsString(c.getLastModified(), Aksess.getDefaultAdminLocale());
            %>
            <tr>
                <td width="11"><img src="../bitmaps/blank.gif" width=11 height=11></td>
                <td width="12" valign="top"><img src="../bitmaps/common/navigator/<%=icon%>" title="<%=iconText%>" alt="<%=iconText%>" width=12 height=14></td>
                <td nobr><a href="Javascript:gotoObject(<%=c.getAssociation().getId()%>)" class="<%=cssClass%>"><%=title%> (<%=dateText%>)</a>
                    <%
                        if(c.getNumberOfNotes() > 0) {
                            out.write("<img src=\"../bitmaps/common/navigator/note.gif\" width=\"12\" height=\"10\" alt=\"" + c.getNumberOfNotes() + " notat(er) \"");
                        }
                    %>
                </td>
            </tr>
            <%
                }
            %>
        </table>
    </td>
</tr>

<%
    }
%>
<tr>
    <td colspan="3">&nbsp;</td>
</tr>
<%
    }

    List sites = siteService.getSites();
    for (int i = 0; i < sites.size(); i++) {
        Site site = (Site)sites.get(i);
        if (!site.isDisabled()) {
            SiteMapEntry sitemap = aksess.getNavigatorMenu(site.getId(), openFolders, -1, sort);
            if (sitemap != null) {
                sitemap.setTitle(site.getName());
                boolean print = startId == -1;
                printFolder(sitemap, 0, selectedId, openFolders, selectAssociationId, selectContentId, startId, print, out);
                out.write("<tr><td colspan=\"3\">&nbsp;</td></tr>");
            }
        }
    }
%>
</table>
<%
    String cssPasteClass;
    String cssPasteShortCutClass;

    if (clipboard.length() > 0) {
        cssPasteClass = "cMenu";
    } else {
        cssPasteClass = "cMenuDisabled";
    }

    if (clipboard.length() > 0 && isCopy) {
        cssPasteShortCutClass = "cMenu";
    } else {
        cssPasteShortCutClass = "cMenuDisabled";
    }
%>
<div id="contextMenu" style="position:absolute; left: 0px; top: 0px; visibility:hidden;">
    <div id="contextMenu_<%=ContentType.PAGE%>">
        <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="3" width="100%">
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(false)"><kantega:label key="aksess.navigator.open"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(true)"><kantega:label key="aksess.navigator.opennewwindow"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>

                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:newObject()"><kantega:label key="aksess.navigator.newpage"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:deleteObject()"><kantega:label key="aksess.navigator.delete"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(false)"><kantega:label key="aksess.navigator.cut"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(true)"><kantega:label key="aksess.navigator.copy"/></a></td>
                        </tr>
                        <tr>
                            <td class="<%=cssPasteClass%>" id="cMenu_paste0" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject()"><kantega:label key="aksess.navigator.paste"/></a></td>
                        </tr>
                        <tr>
                            <td class="<%=cssPasteShortCutClass%>" id="cMenu_pasteshortcut0" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject(true)"><kantega:label key="aksess.navigator.pasteshortcut"/></a></td>
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
    <div id="contextMenu_<%=ContentType.SHORTCUT%>">
        <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="3" width="100%">
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(false)"><kantega:label key="aksess.navigator.open"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(true)"><kantega:label key="aksess.navigator.opennewwindow"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:deleteObject()"><kantega:label key="aksess.navigator.deleteshortcut"/></a></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    <div id="contextMenu_<%=ContentType.FILE%>">
        <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="3" width="100%">
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(false)"><kantega:label key="aksess.navigator.open"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(true)"><kantega:label key="aksess.navigator.opennewwindow"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>

                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:deleteObject()"><kantega:label key="aksess.navigator.delete"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(false)"><kantega:label key="aksess.navigator.cut"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(true)"><kantega:label key="aksess.navigator.copy"/></a></td>
                        </tr>
                        <tr>
                            <td class="<%=cssPasteClass%>" id="cMenu_paste1" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject()"><kantega:label key="aksess.navigator.paste"/></a></td>
                        </tr>
                        <tr>
                            <td class="<%=cssPasteShortCutClass%>" id="cMenu_pasteshortcut1" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject(true)"><kantega:label key="aksess.navigator.pasteshortcut"/></a></td>
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
    <div id="contextMenu_<%=ContentType.LINK%>">
        <table border="0" cellspacing="1" cellpadding="0" class="cMenuFrame" width="120">
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="3" width="100%">
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(false)"><kantega:label key="aksess.navigator.open"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:viewObject(true)"><kantega:label key="aksess.navigator.opennewwindow"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>

                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:deleteObject()"><kantega:label key="aksess.navigator.delete"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenuFrame" height="1"></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(false)"><kantega:label key="aksess.navigator.cut"/></a></td>
                        </tr>
                        <tr>
                            <td class="cMenu" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:copyCutObject(true)"><kantega:label key="aksess.navigator.copy"/></a></td>
                        </tr>
                        <tr>
                            <td class="<%=cssPasteClass%>" id="cMenu_paste2" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject()"><kantega:label key="aksess.navigator.paste"/></a></td>
                        </tr>
                        <tr>
                            <td class="<%=cssPasteShortCutClass%>" id="cMenu_pasteshortcut2" onMouseOver="menuItemOver(this)" onMouseOut="menuItemOut(this)"><a href="Javascript:pasteObject(true)"><kantega:label key="aksess.navigator.pasteshortcut"/></a></td>
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