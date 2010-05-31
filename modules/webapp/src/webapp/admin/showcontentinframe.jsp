<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
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
    String url = null;

    ContentType type = ContentType.FILE;

    Content content = (Content)request.getAttribute("aksess_this");
    if (content != null) {
        type = content.getType();
        if (type == ContentType.LINK) {
            url = content.getLocation();
            if (url.charAt(0) == '/') {
                url = Aksess.getContextPath() + url;
            } else if(Aksess.getConfiguration().getString("links.direct") != null && Aksess.getConfiguration().getString("links.direct").equals("false")){
                url = "admin/showcontentinframelink.jsp?type=" + type + "&url=" + url;
            } else {
                url = content.getLocation();
            }
        } else if (type == ContentType.FILE) {
            if (content.getLocation() != null) {
                url = Aksess.getContextPath() + "/attachment.ap?id=" + content.getLocation();
                if(Aksess.getConfiguration().getString("files.direct") != null && Aksess.getConfiguration().getString("files.direct").equals("false")){
                    url = "admin/showcontentinframelink.jsp?type=" + type + "&url=" + url;
                }
            }
        } else if (type == ContentType.FORM) {
            if (content.getLocation() != null) {
                url = Aksess.getContextPath() + "/forms/flow?_flowId=fillform&formId=" + content.getLocation();
            }
        }
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>showcontentinframe.jsp</title>
</head>

<frameset rows="60,*" frameborder="no" border="0" framespacing="0">
   <frame src="${pageContext.request.contextPath}/admin/showcontentinframetip.jsp?type=<%=type%>" scrolling="auto" marginwidth="0" marginheight="0">
   <%
       if (url != null) {
   %>
   <frame src="<%=url%>" scrolling="auto" marginwidth="0" marginheight="0">
   <%
       }
   %>
</frameset>
</html>
<%@ include file="include/jsp_footer.jsf" %>