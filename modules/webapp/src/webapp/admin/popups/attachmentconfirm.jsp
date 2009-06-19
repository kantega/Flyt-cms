<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.commons.util.URLHelper"%>
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
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
    if (window.opener) {
        <%
            if (insertLink) {
        %>
                var url = "<%=URLHelper.getRootURL(request)%>attachment.ap?id=<%=id%>";
                window.opener.createLink(url);
        <%
            } else {
        %>
                window.opener.location.reload();
        <%
            }
        %>
        window.close();
    }
</script>
<body class="bodyWithMargin">
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>