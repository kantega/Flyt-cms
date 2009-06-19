<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 no.kantega.publishing.security.data.enums.Privilege"%>
<%@ page import="no.kantega.publishing.common.cache.ContentTemplateCache" %>
<%@ include file="../include/jsp_header.jsf" %>
<%@ include file="../include/edit_header.jsf" %>
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
	<title>editversions.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
var hasSubmitted = false;

function saveContent(status) {
    if (!hasSubmitted) {
        hasSubmitted = true;
        document.myform.status.value = status;
        document.myform.submit();
    }
}

function selectVersion(version) {
    document.activeversion.version.value = version;
    document.activeversion.submit();
}

function deleteVersion(version) {
    document.deleteversion.version.value = version;
    document.deleteversion.submit();
}

</script>

<body class="bodyWithMargin">
<%@ include file="../include/infobox.jsf" %>
<form name="myform" action="SaveVersionInfo.action" target="content" method="post">
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="<%=current.getId()%>">
    <input type="hidden" name="isModified" value="<%=current.isModified()%>">
</form>
<form name="activeversion" action="UseVersion.action" target="content" method="post">
    <input type="hidden" name="version" value="-1">
</form>
<form name="deleteversion" action="DeleteVersion.action" target="content" method="post">
    <input type="hidden" name="version" value="-1">
</form>

<table border="0" cellspacing="0" cellpadding="0" width="600">
    <tr class="tableHeading">
        <td colspan="2"><b><kantega:label key="aksess.versions.version"/></b></td>
        <td><b><kantega:label key="aksess.versions.lastmodified"/></b></td>
        <td><b><kantega:label key="aksess.versions.modifiedby"/></b></td>
        <td><b><kantega:label key="aksess.versions.status"/></b></td>
        <td>&nbsp;</td>
    </tr>
<%
    ContentIdentifier cid = new ContentIdentifier();
    cid.setContentId(current.getId());
    cid.setLanguage(current.getLanguage());
    List allVersions = aksessService.getAllContentVersions(cid);
    ContentTemplate contentTemplate = ContentTemplateCache.getTemplateById(current.getContentTemplateId());
    int keepVersions = contentTemplate.computeKeepVersions();
    for (int i = 0; i < allVersions.size(); i++) {
        Content c = (Content)allVersions.get(i);
        Date d = c.getLastModified();
        DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
        String modifiedDate = "";
        try {
            modifiedDate = df.format(d);
        } catch (NumberFormatException e) {
        }
        String title = c.getTitle();
        if (title.length() > 30) {
            title = title.substring(0, 27) + "...";
        }
        String statusKey = "aksess.versions.status." + c.getStatus();
        if (c.getStatus() == ContentStatus.PUBLISHED) {
            statusKey += "_" + c.getVisibilityStatus();
        }
%>
        <tr class="tableRow<%=(i%2)%>">
            <td><%=c.getVersion()%></td>
            <td><%=title%></td>
            <td><%=modifiedDate%></td>
            <td><%=c.getModifiedBy()%></td>
            <td><kantega:label key="<%=statusKey%>"/></td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><a href="<%=current.getUrl()%>&version=<%=c.getVersion()%>" target="_new"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0"></a></td>
                        <td><a href="<%=current.getUrl()%>&version=<%=c.getVersion()%>" target="_new" class="button"><kantega:label key="aksess.button.vis"/></a></td>
                        <td><img src="../bitmaps/common/textseparator.gif"></td>
                        <td><a href="Javascript:selectVersion(<%=c.getVersion()%>)"><img src="../bitmaps/common/buttons/mini_rediger.gif" border="0"></a></td>
                        <td><a href="Javascript:selectVersion(<%=c.getVersion()%>)" class="button"><kantega:label key="aksess.button.rediger"/></a></td>
                        <% if (c.getStatus() != ContentStatus.PUBLISHED && securitySession.isAuthorized(current, Privilege.APPROVE_CONTENT)) {%>
                            <td><img src="../bitmaps/common/textseparator.gif"></td>
                            <td><a href="Javascript:deleteVersion(<%=c.getVersion()%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></td>
                            <td><a href="Javascript:deleteVersion(<%=c.getVersion()%>)" class="button"><kantega:label key="aksess.button.slett"/></td>
                        <%}%>
                    </tr>
                </table>
            </td>
        </tr>
<%
    }
%>
    <tr>
        <td colspan="6">
            <br>
            <div class=helpText><kantega:label key="aksess.versions.hjelp"/>
                <%if(keepVersions != -1) { %>
                    <kantega:label key="aksess.versions.hjelp2"/> <%=Aksess.getHistoryMaxVersions()%> <kantega:label key="aksess.versions.hjelp3"/>
                <% } %>
            </div>
        </td>
    </tr>
</table>

</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>