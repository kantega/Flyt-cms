<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.util.MultimediaHelper"%>
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
    Multimedia mm = new Multimedia();
    RequestParameters param = new RequestParameters(request, "utf-8");
    int id = param.getInt("id");

    String align = param.getString("align");
    String cssClass = "align-center";
    if ("left".equalsIgnoreCase(align)) cssClass="align-left";
    if ("right".equalsIgnoreCase(align)) cssClass="align-right";
    
    if (id != -1) {
        mm = mediaService.getMultimedia(id);
    }

    String baseUrl = URLHelper.getRootURL(request);
    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>selectmultimedia.jsp</title>
</head>
<script language="Javascript">
function insertMMObject() {
    if (window.parent && window.parent.parent && window.parent.parent.opener && <%=mm.getId()%> != -1) {
        if (window.parent.parent.opener.doInsertTag) {
            var str = '<%=MultimediaHelper.mm2HtmlTag(baseUrl, mm, align, -1, -1, cssClass)%>';
            window.parent.parent.opener.insertTag(str);
        } else {
            // Vanlig form felt for valg av mediaobjekt
            window.parent.parent.opener.insertIdAndValueIntoForm(<%=mm.getId()%>, '<%=mm.getName()%>');
        }

    }
    window.parent.parent.close();
}
</script>
<body onLoad="insertMMObject()">
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>

