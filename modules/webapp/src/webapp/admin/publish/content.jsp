<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.Date"%>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
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
    RequestParameters param = new RequestParameters(request);
    String activetab = param.getString("activetab");
    String infomessage = param.getString("infomessage");
    String updatetree  = param.getString("updatetree");

    String updatetreeStr = "";
    if (updatetree != null) {
        updatetreeStr = "&updatetree=1";
    }

    String contentmainframe;
    if (activetab == null || activetab.equalsIgnoreCase("previewcontent")) {
        activetab = "previewcontent";
        contentmainframe = Aksess.getContextPath() + "/PreviewContent.action?dummy=" + new Date().getTime();
    } else {
        contentmainframe = activetab + ".jsp?dummy=" + new Date().getTime();
        if (infomessage != null) {
            // Vis alerts i hovedfelt
            contentmainframe += "&infomessage=" + infomessage;
        }
    }

    String url = param.getString("url");
    String cidStr = "";

    Content current = (Content)session.getAttribute("currentContent");
    try {
        if (url != null || request.getParameter("thisId") != null || request.getParameter("contentId") != null) {
            ContentIdentifier cid = null;
            if (url != null) {
                cid = new ContentIdentifier(request, url);
            } else {
                cid = new ContentIdentifier(request);
            }
            current = aksessService.getContent(cid);
            cidStr = cid.toString();
        }
    } catch (ContentNotFoundException e) {
        e.printStackTrace();
    }
    
    try {
        if (current == null ) {
            // Ikke nå current objekt, og ikke noe spesifisert, gå til startsida
            ContentIdentifier cid = new ContentIdentifier(request, "/");
            current = aksessService.getContent(cid);
            cidStr = cid.toString();
        }
    } catch (ContentNotFoundException e) {
    }

    // Oppdater sesjon med objektet vi jobber på
    session.setAttribute("currentContent", current);
    session.setAttribute("showContent", current);

    // Mindre viktige status meldinger i bunnfelt (som ikke brukeren trenger å tenke på
    String statusmessage  = param.getString("statusmessage");
    if (statusmessage != null) {
        statusmessage = "&statusmessage=" + statusmessage;
    } else {
        statusmessage = "";
    }

    long refresh = new Date().getTime();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.title"/></title>
</head>
    <frameset name="myframeset" rows="38,*,28"  frameborder="0" border="0">
        <frame name="contenttop" src="titleframe.jsp?activetab=<%=activetab%>&dummy=<%=refresh%>&<%=cidStr%><%=updatetreeStr%>" scrolling="no" marginwidth="0" marginheight="0" noresize>
        <frameset name="myframeset2" cols="22,*"  frameborder="0" border="0">
            <frame name="navigatorsplit" src="../navigatorsplit.jsp" scrolling="no" marginwidth="0" marginheight="0" noresize>
            <frame name="contentmain" src="<%=contentmainframe%>" scrolling="auto" marginwidth="0" marginheight="0" noresize>
        </frameset>
        <frame name="contentbottom" src="statusframe.jsp?dummy=<%=refresh%>&activetab=<%=activetab%><%=statusmessage%>" scrolling="no" marginwidth="0" marginheight="0" noresize>
    </frameset>
</html>
<%@ include file="../include/jsp_footer.jsf" %>