<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.enums.ContentType" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
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

    Content content = (Content)request.getAttribute("aksess_this");
    if (content != null) {
        url = content.getLocation();
        if (url.charAt(0) == '/') {
            url = Aksess.getContextPath() + url;
        } else {
            url = content.getLocation();
        }
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <title>link</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/jquery-1.5.1.min.js"></script>
    <script type="text/javascript">
        /*
         * Adds the target=contentmain attribute for all links, this prevents us from getting frames inside frames inside frames...
         */
        $(document).ready(function() {
            $("#LinkFrame").load(function() {
                addTargetToLinks();
            });

            function addTargetToLinks() {
                try {
                    $("a[target!=contentmain]", window.LinkFrame.document).each(function() {
                        $(this).attr('target', 'contentmain');
                    });
                    // In case page is modified with ajax
                    setTimeout(addTargetToLinks, 2000);
                } catch (e) {
                    // Accessing external links gives error
                }
            }

        });

    </script>



<frameset rows="60,*" frameborder="no" border="0" framespacing="0">
    <frame src="${pageContext.request.contextPath}/admin/linktip.jsp" scrolling="auto" marginwidth="0" marginheight="0">
    <%
        if (url != null) {
    %>
    <frame src="<%=url%>" id="LinkFrame" name="LinkFrame" scrolling="auto" marginwidth="0" marginheight="0">
    <%
        }
    %>
</frameset>


</html>
