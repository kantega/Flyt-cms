<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.util.MultimediaHelper"%>
<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ page import="no.kantega.commons.util.URLHelper" %>

<%
    Multimedia mm = (Multimedia)request.getAttribute("media");
    RequestParameters param = new RequestParameters(request, "utf-8");

    String align = param.getString("align");
    String cssClass = "align-center";
    if ("left".equalsIgnoreCase(align)) cssClass="align-left";
    if ("right".equalsIgnoreCase(align)) cssClass="align-right";

    String baseUrl = URLHelper.getRootURL(request);
    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title>...</title>
</head>
<script language="Javascript">
    function insertMMObject() {
        if (window.opener && <%=mm.getId()%> != -1) {
        if (window.opener.doInsertTag) {
            // Insert IMG or other tag
            var str = document.mediaform.tag.value;
            window.opener.insertTag(str);
        } else {
            // Insert id and name
            window.opener.insertIdAndValueIntoForm(<%=mm.getId()%>, '<%=mm.getName()%>');
        }

    }
        window.close();
    }
</script>
<body onLoad="insertMMObject()">
<form name="mediaform" style="display:none;">
    <textarea name="tag" rows="2" cols="30"><%=MultimediaHelper.mm2HtmlTag(baseUrl, mm, align, -1, -1, cssClass)%></textarea>
</form>
</body>
</html>


