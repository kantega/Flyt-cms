<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.cache.ContentTemplateCache,
                 no.kantega.publishing.common.cache.DisplayTemplateCache,
                 no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.ContentTemplate"%>
<%@ page import="no.kantega.publishing.common.data.DisplayTemplate" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentStatus" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    RequestParameters param = new RequestParameters(request);
    String activetab = param.getString("activetab");
    String statusmessage = param.getString("statusmessage");

    int saveStatus = ContentStatus.WAITING;
            
    boolean showPublishButtons = false;
    boolean showApproveButtons = false;
    boolean hearingEnabled = false;

    Content current = (Content)session.getAttribute("currentContent");
    Content showContent = (Content)session.getAttribute("showContent");


    String jsFramePrefix = "window.parent.contentmain.";
    if (activetab.equalsIgnoreCase("previewcontent")) {
        jsFramePrefix = "";
    }

    if (current != null) {
        ContentTemplate contentTemplate = ContentTemplateCache.getTemplateById(current.getContentTemplateId());
        hearingEnabled = contentTemplate.isHearingEnabled();

        if ((current.isModified()) || ((!activetab.equalsIgnoreCase("previewcontent")) && (!activetab.equalsIgnoreCase("editnotes")))) {
            if (securitySession.isAuthorized(current, Privilege.APPROVE_CONTENT)) {
                saveStatus = ContentStatus.PUBLISHED;
            }
            showPublishButtons = true;
        }
    }

    if (showContent != null && !showPublishButtons) {
        if (showContent.getStatus() == ContentStatus.WAITING) {
            if (securitySession.isAuthorized(showContent, Privilege.APPROVE_CONTENT)) {
                showApproveButtons = true;
            }
        }
    }
%>
<%
    Locale lang = (Locale)request.getAttribute("aksess_locale");
    String locale_bildesti_framework = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/framework/";
    String locale_bildesti_buttons = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/buttons/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>publish/statusframe.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
var hasSubmitted = false;

function saveContent(status) {
    if (!hasSubmitted) {
        hasSubmitted = true;
        document.saveempty.status.value = status;
        document.saveempty.submit();
    }
}

function cancelEdit() {
    window.parent.location = "CancelEdit.action";
}

function approveContent() {
    document.approvereject.status.value = <%=ContentStatus.PUBLISHED%>;
    document.approvereject.submit();

}

function addNote(note) {
    document.approvereject.note.value = note;
    document.approvereject.status.value = <%=ContentStatus.REJECTED%>;
    document.approvereject.submit();
}

function sendHearing() {
    var hearingwin = window.open("hearing.jsp?contentId=<%=current.getId()%>", "hearingWindow", "toolbar=no,width=625,height=600,resizable=yes,scrollbars=yes");
    hearingwin.focus();
}

function submitHearing() {
    <%=jsFramePrefix%>saveContent(<%=ContentStatus.HEARING%>);
}

function rejectContent() {
   var notewin = window.open("../popups/note.jsp", "noteWindow", "toolbar=no,width=280,height=200,resizable=yes,scrollbars=yes");
   notewin.focus();
}

function showStats() {
<%
    if (showContent != null) {
%>
   var statwin = window.open("../statistics/statistics.jsp?contentId=<%=showContent.getId()%>&language=<%=showContent.getLanguage()%>", "statWindow", "toolbar=no,width=300,height=400,resizable=yes,scrollbars=yes");
   statwin.focus();
<%
    }
%>
}

function showUser(userid) {
    var userwin = window.open("../popups/showuser.jsp?userid=" + userid, "userWindow", "toolbar=no,width=280,height=100,resizable=yes,scrollbars=yes");
    userwin.focus();
}

</script>
<body class="framework">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="22"><img src="<%=locale_bildesti_framework%>navigatorsplit_bottom.gif" width="22" height="28"></td>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr height="3">
                    <td background="<%=locale_bildesti_framework%>status_shadow.gif"><img src="<%=locale_bildesti_framework%>status_shadow_corner.gif" width="1" height="3"></td>
                </tr>
                <tr height="2">
                    <td><img src="../bitmaps/blank.gif" width="1" height="2"></td>
                </tr>
                <tr height="21">
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <%  if (showPublishButtons)  {%>
                                    <td>
                                    <%
                                        if (saveStatus == ContentStatus.PUBLISHED) {
                                    %>
                                        <a href="Javascript:<%=jsFramePrefix%>saveContent(<%=ContentStatus.PUBLISHED%>)"><img src="<%=locale_bildesti_buttons%>publiser.gif" border="0" tabindex="1000"></a>
                                    <%
                                        } else {
                                    %>
                                        <a href="Javascript:<%=jsFramePrefix%>saveContent(<%=ContentStatus.WAITING%>)"><img src="<%=locale_bildesti_buttons%>lagre.gif" border="0" tabindex="1010"></a>
                                    <%
                                        }
                                    %>
                                        <a href="Javascript:<%=jsFramePrefix%>saveContent(<%=ContentStatus.DRAFT%>)"><img src="<%=locale_bildesti_buttons%>lagre_kladd.gif" border="0"></a>
                                        <%if(hearingEnabled && current.getStatus() != ContentStatus.HEARING) { %>
                                            <a href="Javascript:sendHearing()"><img src="<%=locale_bildesti_buttons%>til_horing.gif" border="0"></a>
                                        <% }%>
                                        <a href="Javascript:cancelEdit()"><img src="<%=locale_bildesti_buttons%>avbryt.gif" border="0"></a>
                                    </td>
                                <%  } else if (showApproveButtons) { %>
                                    <td>
                                        <a href="Javascript:<%=jsFramePrefix%>approveContent()"><img src="<%=locale_bildesti_buttons%>godkjenn.gif" border="0" tabindex="1000"></a>
                                        <a href="Javascript:<%=jsFramePrefix%>rejectContent()"><img src="<%=locale_bildesti_buttons%>forkast.gif" border="0" tabindex="1010"></a>
                                    </td>
                                <%
                                    }
                                %>
                                <td>
                                <%  if (showPublishButtons || showApproveButtons) { %>
                                    <img src="../bitmaps/common/textseparator.gif" width="13" height="9">
                                <%  }
                                    if (statusmessage == null) {
                                        if (showContent != null) {
                                            int status = showContent.getStatus();
                                            switch (status) {
                                                case ContentStatus.WAITING:
                                                %>
                                                <kantega:label key="aksess.statusmessage.info.waiting"/>
                                                <%
                                                    break;

                                                case ContentStatus.REJECTED:
                                                %>
                                                <kantega:label key="aksess.statusmessage.info.rejected"/>
                                                <%
                                                    break;

                                                case ContentStatus.HEARING:
                                                %>
                                                <kantega:label key="aksess.statusmessage.info.hearing"/>
                                                <%
                                                    break;

                                                default:
                                                    DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
                                                    %>
                                                    <kantega:label key="aksess.status.endret"/>
                                                    <%
                                                    out.write(": " + df.format(showContent.getLastModified()));
                                                    String modifiedBy = showContent.getModifiedBy();
                                                    if (modifiedBy != null && modifiedBy.length() > 0) {
                                                        %>
                                                        <kantega:label key="aksess.status.av"/>
                                                        <%
                                                        out.write(" <a href=\"Javascript:showUser('" + modifiedBy + "')\" class=\"button\">" + modifiedBy + "</a>");
                                                    }
                                                    String approvedBy = showContent.getApprovedBy();
                                                    if (approvedBy != null && approvedBy.length() > 0 && !approvedBy.equalsIgnoreCase(modifiedBy)) {
                                                        out.write(", ");
                                                        %>
                                                        <kantega:label key="aksess.status.godkjentav"/>
                                                        <%
                                                        out.write(" <a href=\"Javascript:showUser('" + approvedBy + "')\" class=\"button\">" + approvedBy + "</a>");
                                                    }
                                                    out.write(".");
                                                    if (Aksess.isTrafficLogEnabled() && !showPublishButtons && !showApproveButtons) {
                                                        out.write("&nbsp;&nbsp;&nbsp;<a href=\"Javascript:showStats()\" class=\"button\">");
                                                        %>
                                                        <kantega:label key="aksess.status.visbesoksstat"/>                                                        
                                                        <%
                                                        out.write("</a>");
                                                    }
                                                    if (!showPublishButtons && !showApproveButtons) {
                                                        if (securitySession.isUserInRole(Aksess.getAdminRole())) {
                                                            if (showContent.getId() != -1) {
                                                                out.write("&nbsp;&nbsp;&nbsp;(");%><kantega:label key="aksess.status.sideid"/><%out.write(": " + showContent.getId());
                                                                if (showContent.getDisplayTemplateId() > 0) {
                                                                    DisplayTemplate template = DisplayTemplateCache.getTemplateById(showContent.getDisplayTemplateId());
                                                                    if (template != null) {
                                                                        out.write(",&nbsp;");%><kantega:label key="aksess.status.visningsmal"/><%out.write(": " + template.getView());
                                                                    }
                                                                }
                                                                out.write(")");
                                                            }
                                                        }
                                                    }
                                                    break;
                                            }
                                        }
                                    } else {
                                        String key = "aksess.statusmessage." + statusmessage;
                                %>
                                    <kantega:label key="<%=key%>"/>
                                <%
                                    }
                                %>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<form name="saveempty" action="SaveEmpty.action" target="content" method="post" enctype="multipart/form-data">
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <%
        if (current != null) {
    %>
        <input type="hidden" name="isModified" value="<%=current.isModified()%>">
        <input type="hidden" name="currentId" value="<%=current.getId()%>">
    <%
        } else {
    %>
        <input type="hidden" name="isModified" value="false">    
    <%
        }
    %>

</form>


<form name="approvereject" action="ApproveOrReject.action" target="content" method="post" enctype="multipart/form-data">
    <%
        if (showContent != null) {
    %>
        <input type="hidden" name="associationid" value="<%=showContent.getAssociation().getId()%>">
        <input type="hidden" name="version" value="<%=showContent.getVersion()%>">
        <input type="hidden" name="language" value="<%=showContent.getLanguage()%>">
    <%
        }
    %>
    <input type="hidden" name="status" value="">
    <input type="hidden" name="note" value="">
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>