<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.note.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/common.js">
</script>
<script language="Javascript">
function addNote() {
   if (window.opener) {
      window.opener.addNote(document.myform.note.value);
   }
   window.close();
}
</script>

<body class="bodyWithMargin">
<form name="myform">
    <table border="0" width="100%" cellspacing="0">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.note.title"/></b></td>
        </tr>
        <tr>
            <td>
                <kantega:label key="aksess.note.text"/>
                <textarea name="note" class="popupNoteArea"></textarea>
            </td>
        </tr>
    </table>
    <p>
        <a href="Javascript:addNote()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </p>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>