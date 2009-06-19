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
	<title>titleframe.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="framework">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="21"><img src="../bitmaps/<%=skin%>/framework/navigatorsplit_top.gif" width="21" height="38"></td>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr height="1">
                    <td background="../bitmaps/<%=skin%>/shadow_light.gif"><img src="../bitmaps/blank.gif" width="600" height="1"></td>
                </tr>
                <tr height="36">
                    <td><b><kantega:label key="aksess.selecttemplate.title"/></b></td>
                </tr>
                <tr height="1">
                    <td background="../bitmaps/<%=skin%>/shadow_dark.gif"><img src="../bitmaps/blank.gif" width="600" height="1"></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>