<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page buffer="none" %>
<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer"%>
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
    InputScreenRenderer screen = new InputScreenRenderer(pageContext, current, AttributeDataType.CONTENT_DATA);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>editcontent.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <link rel="stylesheet" type="text/css" href="../css/formeditor.css">
    <%@ include file="include/calendarsetup.jsp"%>
</head>
<script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
<script type="text/javascript" language="Javascript" src="../js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" language="Javascript" src="../js/jquery-ui-1.7.2.min.js"></script>
<script type="text/javascript" language="Javascript" src="../js/edit.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/richtext.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/formeditor.jsp"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/autocomplete.js"></script>

<script language="Javascript" type="text/javascript">
    var hasSubmitted = false;

    function initialize() {
    <%
        screen.generatePreJavascript();
    %>
    try {
        document.myform.elements[0].focus()
    } catch (e) {
        // Usynlig element som ikke kan få fokus
    }
}

function saveContent(status) {
    <%
        screen.generatePostJavascript();
    %>
    if (!hasSubmitted) {
        hasSubmitted = true;
        formSave();
        document.myform.status.value = status;
        document.myform.submit();
    }
}

</script>
<body onLoad="initialize()" class="bodyWithMargin">
<div style="width: 600px">
<%@ include file="../include/infobox.jsf" %>  
<form name="myform" action="SaveContent.action" target="content" method="post" enctype="multipart/form-data">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
    <%
        screen.generateInputScreen();
    %>
    </table>
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="<%=current.getId()%>">
    <input type="hidden" name="isModified" value="<%=current.isModified()%>">
</form>
</div>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
