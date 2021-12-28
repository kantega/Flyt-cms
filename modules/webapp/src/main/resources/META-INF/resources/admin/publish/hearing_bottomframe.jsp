<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
	<title>navigatorbottom.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td background="<%=framework_bitmaps%>/navigator_bottom.gif"><img src="${pageContext.request.contextPath}/admin/bitmaps/blank.gif" width="4" height="4"></td>
        </tr>
        <tr>
            <td class="framework"><a href="Javascript:window.parent.main.submitHearing()"><img src="${pageContext.request.contextPath}/admin/bitmaps/<%=skin%>/buttons/ok.gif" border="0" hspace="4" vspace="4"></a>
            <a href="Javascript:window.parent.close()"><img src="${pageContext.request.contextPath}/admin/bitmaps/<%=skin%>/buttons/avbryt.gif" border="0" hspace="4" vspace="4"></a></td>
        </tr>
        <tr>
            <td class="framework">&nbsp;</td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
