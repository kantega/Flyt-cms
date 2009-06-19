<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
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
    int id = param.getInt("attachmentId");
    boolean insertLink = param.getBoolean("insertLink");

    Locale lang = (Locale)request.getAttribute("aksess_locale");
    String locale_bildesti_framework = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/framework/";
    String locale_bildesti_buttons = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/buttons/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<%
    if (id == -1) {
%>
	<title><kantega:label key="aksess.attachment.add"/></title>
<%
    } else {
%>
    <title><kantega:label key="aksess.attachment.update"/></title>
<%
    }
%>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <script type="text/javascript">
        var hasSubmitted = false;
        function saveForm() {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }
        }
    </script>
</head>

<body class="bodyWithMargin">
<form name="myform" action="AddAttachment.action" method="post" enctype="multipart/form-data">
<input type="hidden" name="attachmentId" value="<%=id%>">
<input type="hidden" name="insertLink" value="<%=insertLink%>">
         <table border="0" width="300" cellspacing="0">
            <tr>
            <%
                if (id == -1) {
            %>
	            <td class="tableHeading"><b><kantega:label key="aksess.attachment.add"/></b></td>
            <%
                } else {
            %>
                <td class="tableHeading"><b><kantega:label key="aksess.attachment.update"/></b></td>
            <%
                }
            %>
            </tr>
            <tr>
                <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
            </tr>
            <tr>
               <td><input type="file" size="20" name="attachment" value="" style="width:300px;"></td>
            </tr>
            <tr>
                <td><img src="../bitmaps/blank.gif" width="2" height="8"></td>
            </tr>
            <tr>
                <td><a href="Javascript:saveForm()"><img src="<%=locale_bildesti_buttons%>ok.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.close()"><img src="<%=locale_bildesti_buttons%>avbryt.gif" border="0"></a></td>
            </tr>
         </table>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>