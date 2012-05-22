<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier"%>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService"%>
<%@ page import="no.kantega.publishing.common.service.lock.ContentLock"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map"%>
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
	<title>info.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
<%
    Map locks = (Map)request.getAttribute("locks");
    if(locks.size() > 0) {
        Iterator i  = locks.values().iterator();
        DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
        ContentManagementService cms = new ContentManagementService(request);
%>
        <table border="0" cellspacing="0" cellpadding="0" width="600">
            <tr class="tableHeading">
                <td><kantega:label key="aksess.locks.page"/></td>
                <td><kantega:label key="aksess.locks.owner"/></td>
                <td><kantega:label key="aksess.locks.when"/></td>
                <td>&nbsp;</td>
            </tr>
    <%
            int count = 0;
            while (i.hasNext()) {
                ContentLock contentLock = (ContentLock) i.next();
                ContentIdentifier cid = new ContentIdentifier();
                cid.setContentId(contentLock.getContentId());
                Content c = cms.getContent(cid);
    %>
            <tr class="tableRow<%=count++%2%>" >
                <td>
                    <a href="<%=c.getUrl()%>" target="_new"><%=c.getTitle()%></a>
                </td>
                <td>
                    <%=contentLock.getOwner()%>
                </td>
                <td>
                    <%= df.format(contentLock.getCreateTime()) %> <%= tf.format(contentLock.getCreateTime())%>
                </td>
                <td align="right">
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td><a href="RemoveContentLock.action?contentId=<%=contentLock.getContentId()%>"><img src="${pageContext.request.contextPath}/admin/bitmaps/common/buttons/mini_slett.gif" border="0" alt=""></a></td>
                            <td><a href="RemoveContentLock.action?contentId=<%=contentLock.getContentId()%>" class="button"><kantega:label key="aksess.locks.remove"/></a></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <%}%>
            <tr>
                <td colspan="4"><br><div class=helpText><kantega:label key="aksess.locks.hjelp"/></div></td>
            </tr>
        </table>
    <%
        } else {
    %>
                <p><strong><kantega:label key="aksess.locks.notfound"/></strong></p>
    <%
        }
    %>
</body>
</html>
