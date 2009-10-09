<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ page import="no.kantega.commons.util.StringHelper"%>
<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ page import="no.kantega.publishing.common.util.MultimediaHelper" %>
<%@ page import="java.util.List" %>
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
    Multimedia mm = (Multimedia)session.getAttribute("currentMultimedia");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>viewmultimedia.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <script type="text/javascript" src="../../aksess/js/aksessmultimedia.jjs"></script>		
    <script type="text/javascript" src="../../aksess/js/swfobject.js"></script>
</head>
<body class="bodyWithMargin">
<%=MultimediaHelper.mm2HtmlTag(mm)%>
<%
    if (mm.getUsage() != null && mm.getUsage().length() > 0) {
%>
<p>
<table border="0" cellspacing="0" cellpadding="0" class="info">
    <tr>
        <td>
            <b><kantega:label key="aksess.multimedia.usage"/></b><br>
            <%=StringHelper.replace(mm.getUsage(), "\n", "<br>")%>
        </td>
    </tr>
</table>
</p>
<%
    }
%>
<p>&nbsp;</p>
<form name="myform" action="selectmultimedia.jsp">
    <input type="hidden" name="id" value="<%=mm.getId()%>">
    <input type="hidden" name="name" value="<%=mm.getName()%>">
    <script language="Javascript">
    if (window.parent && window.parent.parent && window.parent.parent.opener) {
        document.write('<table border="0" cellspacing="0" cellpadding="0"><tr><td>');
        <%
            if (mm.getMimeType().getType().indexOf("image") != -1) {
        %>
            // Kun ved innsetting av bildetag skal dette vises
            if (window.parent.parent.opener.doInsertTag) {
                document.write('<select name="align">');
                document.write('<option value="">Ingen tekstflyt</option>');
                document.write('<option value="right">Bilde til høyre for tekst</option>');
                document.write('<option value="left">Bilde til venstre for tekst</option>');
                document.write('</select></td><td>&nbsp;</td><td>');
            }
        <%
            }
        %>
        document.write('<a href="Javascript:document.myform.submit()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a></td></tr></table>');
    }
    </script>
    <%
        List usages = mediaService.getUsages(mm.getId());
        request.setAttribute("pages", usages);
        request.setAttribute("noUsages", usages.size());
    %>
        <c:if test="${not empty pages}">
        <p>
            <a href="#" onclick="document.getElementById('usages').style.display='block'" class="button"><kantega:label key="aksess.status.usagecount"/> ${noUsages} <kantega:label key="aksess.status.usagecount.pages"/></a>
        </p>
        <div id="usages" style="display:none;">    
        <ul>
            <c:forEach items="${pages}" var="page">
                <li>
                    <a href="${page.url}" target="_new">${page.title}</a>
                </li>
            </c:forEach>
        </ul>
        </div>
        </c:if>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>