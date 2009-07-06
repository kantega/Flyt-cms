<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="include/jsp_header.jsf" %>
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
    RequestParameters param = new RequestParameters(request);
    String statusmessage = param.getString("statusmessage");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>statusframe.jsp</title>
    <link rel="stylesheet" type="text/css" href="css/<%=skin%>.css">
</head>
<body class="framework">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="22"><img src="bitmaps/<%=skin%>/framework/navigatorsplit_bottom.gif" width="22" height="28"></td>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr height="3">
                    <td background="<%=framework_bitmaps%>/status_shadow.gif"><img src="<%=framework_bitmaps%>/status_shadow_corner.gif" width="1" height="3"></td>
                </tr>
                <tr height="2">
                    <td><img src="bitmaps/blank.gif" width="1" height="2"></td>
                </tr>
                <tr height="21">
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <%
                                    if (statusmessage != null) {
                                        String key = "aksess.statusmessage." + statusmessage;
                                %>
                                    <kantega:label key="<%=key%>"/>
                                <%
                                    }
                                %>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
<%@ include file="include/jsp_footer.jsf" %>