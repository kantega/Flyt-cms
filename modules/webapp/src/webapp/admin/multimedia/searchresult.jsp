<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
    List searchResult = null;
    RequestParameters param = new RequestParameters(request, "utf-8");
    String phrase = param.getString("phrase");
    if (phrase != null && phrase.length() >= 3) {
        searchResult = mediaService.searchMultimedia(phrase);
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>search/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
<%
    if (phrase == null || phrase.length() < 3) {
%>
        <b><kantega:label key="aksess.search.manglendesokeord"/></b>
<%
    } else if (searchResult == null || searchResult.size() ==  0) {
%>
        <b><kantega:label key="aksess.search.ingentreff"/></b>
<%
    } else {
        // Skriv ut søkeresultat
        int treff = searchResult.size();
%>
        <b><kantega:label key="aksess.search.fant"/> <%=treff%> <kantega:label key="aksess.search.treff"/></b>
<%
        for (int i = 0; i < treff; i++) {
            Multimedia item = (Multimedia)searchResult.get(i);
            out.write("<p><a href=\"multimedia.jsp?activetab=viewmultimedia&amp;id=" + item.getId() + "\" target=\"content\">" + item.getName() + "</a><br>");
            out.write("<span class=\"comment\">(" + item.getFileType() + ")</span></p>");
        }
    }
%>
<p>&nbsp;</p>
<p><a href="navigator.jsp"><img src="../bitmaps/<%=skin%>/buttons/lukk_sokeresultat.gif" border="0"></a></p>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>