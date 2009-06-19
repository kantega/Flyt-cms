<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
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
    RequestParameters param = new RequestParameters(request, "utf-8");
    int type = param.getInt("type");

    String key = "aksess.showcontentinframe.link";
    if (type == ContentType.FILE.getTypeAsInt()) {
        key = "aksess.showcontentinframe.file";
    } else if (type == ContentType.FORM.getTypeAsInt()) {
        key = "aksess.showcontentinframe.form";
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>Untitled</title>
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/<%=skin%>.css">
</head>

<body class="bodyWithMargin">
    <div>
        <table border="0" cellspacing="0" cellpadding="0" class="info">
            <tr>
                <td>
                    <kantega:label key="<%=key%>"/>
                </td>
            </tr>
        </table>
    </div>
</body>
</html>
<%@ include file="include/jsp_footer.jsf" %>