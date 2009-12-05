<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType"%>
<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
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
    Content currentContent = (Content) session.getAttribute("currentContent");
    InputScreenRenderer screen = new InputScreenRenderer(pageContext, currentContent, AttributeDataType.CONTENT_DATA);
%>
<html>
<head>
<title>Aksess Publiseringsystem - Hurtigredigering</title>
<link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
<style type="text/css">
    body {
        background-color: #eeeeed;
    }

    .box {
        background-color: #ffffff;
    }

    .frame {
        background-color: #b5b5b5;
    }

    .shadow {
        background-color: #d2d2d2;
    }
</style>
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
        // Usynlig element som ikke kan f� fokus
    }
}
</script>
<%@ include file="../../admin/publish/include/calendarsetup.jsp"%>
    <script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
    <script type="text/javascript" language="Javascript" src="../js/edit.jjs"></script>
    <script type="text/javascript" language="Javascript" src="../js/richtext.jjs"></script>
    <script type="text/javascript" language="Javascript" src="../../aksess/js/autocomplete.js"></script>


</head>
<body onLoad="initialize()">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td align="center">
                <br>
                <%@ include file="../include/infobox.jsf" %>
                <%
                    if (!securitySession.isAuthorized(currentContent, Privilege.APPROVE_CONTENT)) {
                %>
                    <div class="info" style="width: 590px;">
                        <kantega:label key="aksess.simpleedit.approvereminder"/>
                    </div>
                <%
                    }
                %>
                <table border="0" cellspacing="0" cellpadding="0" width="615">
                    <tr>
                        <td width="1" rowspan="3" class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="610" class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="1" rowspan="3" class="frame"><img src="${pageContext.request.contextPath}/login/bitmaps/blank.gif" width="1" heigth="1"></td>
                        <td width="2" rowspan="3" class="shadow" valign="top"><img src="${pageContext.request.contextPath}/login/bitmaps/corner.gif" width="2" heigth="2"></td>
                     </tr>
                     <tr>
                        <td class="box">
                            <form name="myform" action="SimpleEditSaveContent.action" method="post" enctype="multipart/form-data">
                            <table border="0" cellspacing="0" cellpadding="0" width="600" align="center">
                            <%
                                screen.generateInputScreen();
                            %>
                           </table>
                                <input type="hidden" name="redirectUrl" value="<%=(request.getAttribute("redirectUrl") != null)?request.getAttribute("redirectUrl"):""%>">
                           </form>
                        </td>
                     </tr>
                    <tr>
                        <td class="frame"><img src="../bitmaps/blank.gif" width="1" height="1"></td>
                     </tr>
                     <tr>
                        <td colspan="4" class="shadow"><img src="${pageContext.request.contextPath}/login/bitmaps/corner.gif" width="2" height="2"></td>
                    </tr>
                    <tr>
                        <td colspan="4"><br>
                            <a href="Javascript:saveContent()"><img src="../bitmaps/<%=skin%>/buttons/publiser.gif" alt="Publiser" border="0"></a>&nbsp;&nbsp;&nbsp;<a href="SimpleEditCancel.action<% if(request.getAttribute("redirectUrl") != null) out.print("?redirectUrl="+request.getAttribute("redirectUrl"));%>"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" alt="Avbryt" border="0"></a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
