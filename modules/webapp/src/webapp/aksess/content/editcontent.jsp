<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType"%>
<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
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
    Content currentContent = (Content)session.getAttribute("currentContent");
    InputScreenRenderer screen = new InputScreenRenderer(pageContext, currentContent, AttributeDataType.CONTENT_DATA);
%>
<html>
<head>
<title>Aksess Publiseringsystem - Hurtigredigering</title>
<script language="JavaScript" type="text/javascript">
var hasSubmitted = false;

function saveContent() {
    <%
        screen.generatePostJavascript();
    %>
    if (!hasSubmitted) {
        hasSubmitted = true;
        document.myform.submit();
    }
}


function initialize() {
    <%
        screen.generatePreJavascript();
    %>
    try {
        document.myform.elements[0].focus();
    } catch (e) {
        // Invisible field, cant get focus
    }
}
</script>
<script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
<script type="text/javascript" language="Javascript" src="../js/edit.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/richtext.jjs"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/autocomplete.js"></script>

<link rel="stylesheet" type="text/css" href="../login/login.css">
</head>
<body onLoad="initialize()">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td background="${pageContext.request.contextPath}/login/bitmaps/top_logo_spacer.gif" align="right"><img src="${pageContext.request.contextPath}/login/bitmaps/top_logo.gif" width="500" height="43" usemap="#logo" border="0"></td>
        </tr>
        <tr>
            <td><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" height="75"></td>
        </tr>
        <tr>
            <td align="center">
                <table border="0" cellspacing="0" cellpadding="0" width="615">
                    <tr>
                        <td width="1" rowspan="3" class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="610" class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="1" rowspan="3" class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" heigth="1"></td>
                        <td width="2" rowspan="3" class="shadow" valign="top"><img src="${pageContext.request.contextPath}/login/bitmaps/corner.gif" width="2" heigth="2"></td>
                     </tr>
                     <tr>
                        <td class="box">
                            <table border="0" cellspacing="0" cellpadding="0" width="600" align="center">
                            <%
                                screen.generateInputScreen();
                            %>
                           </table>
                        </td>
                     </tr>
                    <tr>
                        <td class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" height="1"></td>
                     </tr>
                     <tr>
                        <td colspan="4" class="shadow"><img src="${pageContext.request.contextPath}/login/bitmaps/corner.gif" width="2" height="2"></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>