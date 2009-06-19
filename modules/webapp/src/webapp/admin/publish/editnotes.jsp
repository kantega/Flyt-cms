<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page buffer="none" %>
<%@ page import="no.kantega.publishing.common.ao.NotesAO,
                 no.kantega.publishing.common.data.Note,
                 java.text.SimpleDateFormat,
                 java.text.DateFormat"%>
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

<%
    Note[] notes = NotesAO.getNotesByContentId(current.getId());
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>editnotes.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/edit.jsp"></script>
<script language="Javascript">

    function removeNote(noteid) {
        if (confirm('Notatet vil nå bli slettet. Ønsker du å fortsette?')) {
            document.myform.noteaction.value = "removenote";
            document.myform.noteid.value = noteid;
            document.myform.submit();
        }
    }


    function addNote() {
        document.myform.noteaction.value = "addnote";
        document.myform.submit();
    }


    function saveContent(status) {
        document.myform.status.value = status;
        document.myform.submit();
    }

</script>
<body class="bodyWithMargin">
<%@ include file="../include/infobox.jsf" %>
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editnotes.addnote"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <form name="myform" action="SaveNotes.action"  target="content" method="post">
                    <input type="hidden" name="noteid" value="">
                    <input type="hidden" name="status" value="">
                    <input type="hidden" name="noteaction" value="">
                    <input type="hidden" name="action" value="">
                    <input type="hidden" name="currentId" value="<%=current.getId()%>">
                    <input type="hidden" name="sortlist" value="">
                    <input type="hidden" name="isModified" value="<%=current.isModified()%>">
                    <textarea name="note" class="noteArea" cols="40" rows="6"></textarea>
                </form>
            </td>
        </tr>
        <tr>
            <td align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><a href="Javascript:addNote()"><img src="../bitmaps/common/buttons/innhold_legg_til_emner.gif" border="0"></a></td>
                        <td><a href="Javascript:addNote()" class="button"><kantega:label key="aksess.editnotes.addnote"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <%
            if (notes.length > 0) {
        %>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editnotes.header"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <%
                for (int i = 0; i < notes.length; i++) {
                    Note note = notes[i];
        %>
                    <tr class="tableRow0">
                        <td width="90%">
                            <div class="note">
                                <strong><%=note.getAuthor() +"</strong>: " + df.format(note.getDate())%><br>
                                <div style="margin-top: 3px"><%=note.getText().replaceAll("\n", "<br>")%></div>
                            </div>
                        </td>
                        <td width="10%" align="right" valign="top">
                            <table border="0">
                                <tr>
                                    <td><a href="Javascript:removeNote('<%=note.getNoteId()%>')"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></td>
                                    <td><a href="Javascript:removeNote('<%=note.getNoteId() %>')" class="button"><kantega:label key="aksess.button.slett"/></td>
                                </tr>
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
        %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>