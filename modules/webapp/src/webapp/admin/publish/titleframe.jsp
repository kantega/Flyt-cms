<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.commons.util.StringHelper,
                 no.kantega.publishing.common.exception.ContentNotFoundException"%>
<%@ page import="no.kantega.publishing.common.service.lock.ContentLock"%>
<%@ page import="no.kantega.publishing.common.service.lock.LockManager"%>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
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

    String activeTab = param.getString("activetab");
    String url = param.getString("url");
    int id = param.getInt("thisId");
    int noNotes = 0;

    boolean updateFrames = false;

    Content current = (Content)session.getAttribute("currentContent");  // Siden som redigeres
    Content showContent = (Content)session.getAttribute("showContent"); // Siden som vises

    if ((showContent == null) || (url != null && url.indexOf("PreviewContent.action") != -1)) {
        showContent = current;
    }

    try {
        if (url != null) {
            if (url.indexOf("PreviewContent.action") == -1) {
                ContentIdentifier cid = new ContentIdentifier(request, url);
                showContent = aksessService.getContent(cid);
            }
            updateFrames = true;
        } else if (id != -1) {
            ContentIdentifier cid = new ContentIdentifier(request);
            showContent = aksessService.getContent(cid);
            updateFrames = true;
        }
        if (showContent != null && current == null) {
            current = showContent;
            session.setAttribute("currentContent", current);
        }
    } catch (ContentNotFoundException e) {
    }

    // Brukes av bunn frame og navigasjonstree
    session.setAttribute("showContent", showContent);

    String tabAction = "save";
    if (activeTab.equalsIgnoreCase("previewcontent")) {
        tabAction = "edit";
    }

    int showId = -1;
    if (showContent != null) {
        showId = showContent.getAssociation().getId();
    }

    int currentId = -1;
    if (current != null) {
        currentId = current.getAssociation().getId();
    }

    String showTitle = "";
    if (showContent != null) {
        showTitle = showContent.getTitle();
        if (showTitle.length() > 60) {
            showTitle = showTitle.substring(0, 56) + "...";
        }
        showTitle = StringHelper.removeIllegalCharsInTitle(showTitle);
    }

    boolean updateTree = updateFrames;
    if (request.getParameter("updatetree") != null) {
        updateTree = true;
    }

    boolean canUpdate = false;
    boolean canDelete = false;
    boolean canCreateSubPage = true;
    if (showContent != null) {
        if (showContent.getType() != ContentType.PAGE) {
            canCreateSubPage = false;
        }
        canUpdate = securitySession.isAuthorized(showContent, Privilege.UPDATE_CONTENT);
        if (showContent.getVersion() > 1 || showContent.getStatus() == ContentStatus.PUBLISHED) {
            canDelete = securitySession.isAuthorized(showContent, Privilege.APPROVE_CONTENT);
        } else {
            // Dersom dette er første versjon og ikke publisert, vi lar brukeren få slette
            canDelete = canUpdate;
        }
        noNotes = showContent.getNumberOfNotes();

        if (showContent.getId() == -1) {
            // Kan ikke opprette underside til en side som ikke er lagret
            canCreateSubPage = false;
        }
    }
    String lockedBy  = null;
    ContentLock lock = LockManager.peekAtLock(showId);
    if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
        lockedBy = lock.getOwner();
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>titleframe.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
var activeTab  = "<%=activeTab%>";
    var thisURL = "" + window.parent.contentmain.location;

function getId(ask) {
    var currentId = document.cid.currentId.value;
    var showId    = document.cid.showId.value;
    var id = "-1";
    if(currentId == -1 || isModified() == false) {
        // Last nytt
        id = showId;
    } else if (showId == currentId) {
        // Behold current
        id = currentId;
    } else {
        // Brukeren befinner seg på en annen side enn den han redigerer, spør hva han vil
        if (ask) {
            if (confirm("<kantega:label key="aksess.editcontent.contentnotsaved"/>")) {
                // Last nytt
                id = showId;
            } else {
                // Behold current
                id = currentId;
            }
        } else {
            // Brukeren har allerede svart
            id = showContentId;
        }
    }
    return id;
}

function editAction(action) {

    if (<%=lockedBy != null %>) {
        alert("<kantega:label key="aksess.feil.erlastav"/> (<%=lockedBy%>)");
        return;
    }
    if (!<%=canUpdate%>) {
        alert("<kantega:label key="aksess.feil.kanikkeredigere"/>");
        return;
    }
    if ('<%=activeTab%>' == action) return;

    var id = getId(true);
    window.parent.location = "EditContent.action?thisId=" + id + "&action=" + action;
}

function saveAction(action) {
    if ('<%=activeTab%>' == action) return;
    window.parent.contentmain.document.myform.elements['action'].value = action;
    window.parent.contentmain.saveContent("");
}

function newObject() {
    var ok = false;
    if (isModified()) {
        if (confirm("<kantega:label key="aksess.editcontent.contentnotsaved"/>")) {
            ok = true;
        }
    } else {
        ok = true;
    }

    if (ok) {
        var id = getId(false);
        window.parent.location = "EditContent.action?thisId=" + id + "&action=selecttemplate";
    }
}


function deleteObject() {
    var ok = false;
    if (isModified()) {
        if (confirm("<kantega:label key="aksess.editcontent.contentnotsaved"/>")) {
            ok = true;
        }
    } else {
        ok = true;
    }


    var title = "<%=showTitle%>";

    if (ok) {
        var id = getId(false);
        // Apne vindu for bekreftelse
        var confirmwin = window.open("confirmdelete.jsp?target=parent&id=" + id, "confirmwin", "toolbar=no,width=350,height=285,resizable=yes,scrollbars=yes");
        confirmwin.focus();
    }
}


function addFavorite(title, id) {
    var url = "<%=URLHelper.getRootURL(request)%>admin/?thisId=" + id;
    title = "Aksess - " + title;

    if (window.sidebar) {
        // Mozilla Firefox Bookmark
        window.sidebar.addPanel(title, url,"");
    } else if( window.external ) {
        // IE Favorite
        window.external.AddFavorite(url, title);
    }
}


function checkForModifiedURL() {
    var changed = false;

    if(window.parent.contentmain) {
        var url = "" + window.parent.contentmain.location;

        if(!thisURL || thisURL.indexOf("http") == -1) {
           thisURL = url;
        }
        if (url != thisURL && thisURL.length > 0 && thisURL.indexOf("http") != -1) {
            var query = "url=" + escape(url) + "&dummy=" + new Date().getTime();
            window.parent.contenttop.location.replace("titleframe.jsp?activetab=<%=activeTab%>&" + query);
            changed = true;
        }
    }
    if(!changed) {
        timerId = setTimeout("checkForModifiedURL()", 1000);
    }
}


function isModified() {
   try {
       var isModified = window.parent.contentmain.document.myform.isModified.value;
       if (isModified == "true") {
           return true;
       }
   } catch (e) {
   }

   <%  if (current != null) {%>
           return <%=current.isModified()%>;
   <%  } else { %>
           return false;
   <%
       }
   %>
}

function init() {
    <%
        if (updateFrames) {
    %>
        window.parent.contentbottom.location.replace("statusframe.jsp?activetab=<%=activeTab%>");
    <%
        }
    %>
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


    window.setTimeout('checkForModifiedURL()', 2000)
}

</script>
<body onload="init()" class="framework">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="21"><img src="../bitmaps/<%=skin%>/framework/navigatorsplit_top.gif" width="21" height="38"></td>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr height="1">
                    <td background="../bitmaps/<%=skin%>/shadow_light.gif"><img src="../bitmaps/blank.gif" width="600" height="1"></td>
                </tr>
                <tr height="18">
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                            <td><b><%=showTitle%></b></td>
                            <%if (canUpdate && canCreateSubPage) {%>
                                <td><img src="../bitmaps/common/textseparator.gif"></td>
                                <td><a href="Javascript:newObject()"><img src="../bitmaps/common/buttons/topp_ny_side.gif" border="0"></a></td>
                                <td><a href="Javascript:newObject()" class="topButton"><kantega:label key="aksess.button.nyunderside"/></a></td>
                                <td>&nbsp;</td>
                            <%}%>
                            <%if (canDelete) {%>
                                <td><img src="../bitmaps/common/textseparator.gif"></td>
                                <td><a href="Javascript:deleteObject()"><img src="../bitmaps/common/buttons/topp_slett.gif" border="0"></a></td>
                                <td><a href="Javascript:deleteObject()" class="topButton"><kantega:label key="aksess.button.deletesiden"/></a></td>
                                <td>&nbsp;</td>
                            <%}%>
                            <%if (showId != -1) {%>
                                <td><img src="../bitmaps/common/textseparator.gif"></td>
                                <td><a href="Javascript:addFavorite('<%=showTitle%>', <%=showId%>)"><img src="../bitmaps/common/buttons/topp_favoritt.gif" border="0"></a></td>
                                <td><a href="Javascript:addFavorite('<%=showTitle%>', <%=showId%>)" class="topButton"><kantega:label key="aksess.button.leggtilfavoritt"/></a></td>
                            </tr>
                            <%}%>
                        </table>
                    </td>
                </tr>
                <tr height="19">
                    <td background="../bitmaps/<%=skin%>/tabs/tabs_end.gif">
                    <table border="0" cellspacing="0" cellpadding="2">
                        <tr height="19">
                            <td width="1"></td>
                            <%if (!activeTab.equals("previewcontent")) {%>
                                <td class="tab"><a href="Javascript:<%=tabAction%>Action('previewcontent')"><kantega:label key="aksess.tab.preview"/></a></td>
                            <%} else {%>
                                <td class="tabSelected"><kantega:label key="aksess.tab.preview"/></td>
                            <%}%>
                            <td width="1"></td>
                            <%if (!activeTab.equals("editcontent")) {%>
                                <td class="tab"><a href="Javascript:<%=tabAction%>Action('editcontent')"><kantega:label key="aksess.tab.content"/></a></td>
                            <%} else {%>
                                <td class="tabSelected"><kantega:label key="aksess.tab.content"/></td>
                            <%}%>
                            <td width="1"></td>
                            <%if (!activeTab.equals("editattachments")) {%>
                                <td class="tab"><a href="Javascript:<%=tabAction%>Action('editattachments')"><kantega:label key="aksess.tab.attachments"/></a></td>
                            <%} else {%>
                                <td class="tabSelected"><kantega:label key="aksess.tab.attachments"/></td>
                            <%}%>
                            <td width="1"></td>
                            <%if (!activeTab.equals("editmetadata")) {%>
                                <td class="tab"><a href="Javascript:<%=tabAction%>Action('editmetadata')"><kantega:label key="aksess.tab.metadata"/></a></td>
                            <%} else {%>
                                <td class="tabSelected"><kantega:label key="aksess.tab.metadata"/></td>
                            <%}%>
                            <td width="1"></td>
                            <%if (!activeTab.equals("editpublishinfo")) {%>
                                <td class="tab"><a href="Javascript:<%=tabAction%>Action('editpublishinfo')"><kantega:label key="aksess.tab.publishinfo"/></a></td>
                            <%} else {%>
                                <td class="tabSelected"><kantega:label key="aksess.tab.publishinfo"/></td>
                            <%}%>
                            <td width="1"></td>
                            <%if (!activeTab.equals("editversions")) {%>
                                <td class="tab"><a href="Javascript:<%=tabAction%>Action('editversions')"><kantega:label key="aksess.tab.versions"/></a></td>
                            <%} else {%>
                                <td class="tabSelected"><kantega:label key="aksess.tab.versions"/></td>
                            <%}%>
                            <%
                                if (showContent != null && showContent.getId() != -1) {
                            %>
                                <td width="1"></td>
                                <%if (!activeTab.equals("editnotes")) {%>
                                    <td class="tab"><a href="Javascript:<%=tabAction%>Action('editnotes')"><kantega:label key="aksess.tab.notes"/> (<%=noNotes%>)</a></td>
                                <%} else {%>
                                    <td class="tabSelected"><kantega:label key="aksess.tab.notes"/></td>
                                <%}%>
                            <%
                                }

                            %>
                        </tr>
                    </table>
                </tr>
            </table>
        </td>
    </tr>
</table>
<form name="cid">
   <input type="hidden" name="showId" value="<%=showId%>">
   <input type="hidden" name="currentId" value="<%=currentId%>">
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
