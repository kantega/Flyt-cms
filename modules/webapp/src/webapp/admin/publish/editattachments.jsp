<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat"%>
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
	<title>editattachments.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/common.js"></script>
<script language="Javascript" src="../js/edit.jsp"></script>
<script language="Javascript">
    function saveContent(status) {
        document.myform.status.value = status;
        document.myform.submit();
    }

    function addAttachment() {
        updateAttachment(-1);
    }

    function updateAttachment(id) {
        var attwin = window.open("../popups/attachment.jsp?attachmentId=" + id, "attachmentWindow", "dependent,toolbar=no,width=310,height=130,resizable=no,scrollbars=no");
        attwin.focus();
    }

    function deleteAttachment(id) {
        if (confirm("Ønsker du virkelig å slette dette vedlegget?")) {
            location = "DeleteAttachment.action?attachmentId=" + id;
        }
    }

</script>

<body class="bodyWithMargin">
<%@ include file="../include/infobox.jsf" %>
<form name="myform" action="SaveAttachments.action" target="content" method="post">
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="<%=current.getId()%>">
    <input type="hidden" name="isModified" value="<%=current.isModified()%>">
</form>
<table border="0" cellspacing="0" cellpadding="0" width="600">
    <tr class="tableHeading">
        <td><b><kantega:label key="aksess.attachments.attachment"/></b></td>
        <td><b><kantega:label key="aksess.attachments.size"/></b></td>
        <td><b><kantega:label key="aksess.attachments.lastmodified"/></b></td>
        <td>&nbsp;</td>
    </tr>
<%
    List attachments = null;
    if (current.getId() > 0) {
        ContentIdentifier cid = new ContentIdentifier();
        cid.setContentId(current.getId());
        cid.setLanguage(current.getLanguage());
        attachments = aksessService.getAttachmentList(cid);
    } else {
        attachments = current.getAttachments();
    }

    if (attachments != null) {
        for (int i = 0; i < attachments.size(); i++) {
            Attachment a = (Attachment)attachments.get(i);

            String modifiedDate = "";
            Date d = a.getLastModified();
            DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());

            try {
                modifiedDate = df.format(d);
            } catch (NumberFormatException e) {
            }
            String filename = a.getFilename();
            if (filename.length() > 50) {
                filename = filename.substring(0, 47) + "...";
            }
%>
        <tr class="tableRow<%=(i%2)%>">
            <td><a href="<%=Aksess.getContextPath()%>/attachment.ap?id=<%=a.getId()%>"><%=filename%></a></td>
            <td><%=FormatHelper.formatSize(a.getSize())%></td>
            <td><%=modifiedDate%></td>
            <td align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><a href="Javascript:updateAttachment(<%=a.getId()%>)"><img src="../bitmaps/common/buttons/mini_rediger.gif" border="0"></a></td>
                        <td><a href="Javascript:updateAttachment(<%=a.getId()%>)" class="button"><kantega:label key="aksess.button.erstattfil"/></a></td>
                        <td><img src="../bitmaps/common/textseparator.gif"></td>
                        <td><a href="Javascript:deleteAttachment(<%=a.getId()%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                        <td><a href="Javascript:deleteAttachment(<%=a.getId()%>)" class="button"><kantega:label key="aksess.button.slettvedlegg"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>
<%
        }
    }
%>
        <tr>
            <td colspan="4" align="right">
                <br>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><a href="Javascript:addAttachment()"><img src="../bitmaps/common/buttons/innhold_nytt_vedlegg.gif" border="0"></a></td>
                        <td><a href="Javascript:addAttachment()" class="button"><kantega:label key="aksess.button.nyttvedlegg"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>
<%
    if (attachments == null || attachments.size() == 0) {
%>
        <tr>
            <td colspan="3"><br><div class=helpText><kantega:label key="aksess.attachments.hjelp"/></div></td>
        </tr>
<%
    }
%>
</table>

</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>