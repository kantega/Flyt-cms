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
	<title><kantega:label key="aksess.copypaste.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
function doSubmit() {
    document.myform.submit();
}
</script>
<body class="bodyWithMargin">

<p>
    <kantega:label key="aksess.copypaste.move"/> <b><c:out value="${multimedia.name}"/></b> <kantega:label key="aksess.copypaste.under"/> <b><c:out value="${newParent.name}"/></b> ?
</p>

<form name="myform" method="post" action="CopyPasteMultimedia.action">
<input type="hidden" name="mmId" value="<c:out value="${multimedia.id}"/>">
<input type="hidden" name="newParentId" value="<c:out value="${newParent.id}"/>">
</form>

<p>
  <a href="Javascript:doSubmit()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;&nbsp;<a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
</p>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>