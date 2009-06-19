<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.data.Multimedia"%>
<%@ page import="no.kantega.publishing.common.data.enums.MultimediaType" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="java.util.Date" %>
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

    String activetab = param.getString("activetab");
    String updatetree = param.getString("updatetree");
    if (updatetree != null) {
        updatetree = "&updatetree=true";
    } else {
        updatetree = "";
    }

    String statusmessage = param.getString("statusmessage");
    if (statusmessage != null) {
        // Vis alerts i hovedfelt
        statusmessage = "&statusmessage=" + statusmessage;
    } else {
        statusmessage = "";
    }

    Multimedia mm = (Multimedia)session.getAttribute("currentMultimedia");

    int id = param.getInt("id");
    int parentId = param.getInt("parentId");

    if (id > 0) {
        mm = mediaService.getMultimedia(id);
        updatetree = "&updatetree=true";
        if (!securitySession.isAuthorized(mm, Privilege.VIEW_CONTENT)) {
            mm = null;
        }
    } else if (parentId != -1) {
        mm = new Multimedia();
        mm.setParentId(parentId);
        mm.setType(MultimediaType.getMultimediaTypeAsEnum(param.getInt("type")));
    }

    if (mm == null || id == 0) {
        // Gå til root mappe
        mm = new Multimedia();
        mm.setId(0);
        mm.setType(MultimediaType.FOLDER);
        updatetree = "&updatetree=true";
        activetab = "viewfolder";

    }

    if (activetab == null ) {
        if (mm.getType() == MultimediaType.FOLDER) {
            activetab = "viewfolder";
        } else {
            activetab = "viewmultimedia";
        }
    }

    session.setAttribute("currentMultimedia", mm);

    long refresh = new Date().getTime();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.title"/></title>
</head>
    <frameset name="myframeset" rows="38,*,28"  frameborder="0" border="0">
        <frame name="contenttop" src="titleframe.jsp?activetab=<%=activetab%><%=updatetree%>&dummy=<%=refresh%>" scrolling="no" marginwidth="0" marginheight="0" noresize>
        <frameset name="myframeset2" cols="22,*"  frameborder="0" border="0">
            <frame name="navigatorsplit" src="../navigatorsplit.jsp" scrolling="no" marginwidth="0" marginheight="0" noresize>
            <frame name="contentmain" src="<%=activetab%>.jsp?dummy=<%=refresh%>" scrolling="auto" marginwidth="0" marginheight="0" noresize>
        </frameset>
        <frame name="contentbottom" src="statusframe.jsp?dummy=<%=refresh%><%=statusmessage%>" scrolling="no" marginwidth="0" marginheight="0" noresize>
    </frameset>
</html>
<%@ include file="../include/jsp_footer.jsf" %>