<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="no.kantega.publishing.common.cache.SiteCache" %>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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
    Site site = (Site)request.getAttribute("site");
    List hostnames = site.getHostnames();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.site.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
<body class="bodyWithMargin">
<form name="myform" action="UpdateSite.action" method="post">
<input type="hidden" name="siteId" value="${site.id}">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.site.name"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>${site.name}</td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.site.alias"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>${site.alias}</td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.site.domains"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <%

                for (int i = 0; i < Math.min(hostnames.size() + 10, 40); i++) {
                    String hostname = "";
                    if (i < hostnames.size()) {
                        hostname = (String)hostnames.get(i);
                    }
            %>
                    <tr>
                        <td width="80"><kantega:label key="aksess.site.domain"/> <%=(i+1)%></td>
                        <td><input type="text" name="hostname<%=i%>" value="<%=hostname%>" size="40" maxlength="128"></td>
                    </tr>
            <%
                }
            %>
            </table>
            <br><div class=helpText><kantega:label key="aksess.site.domain.tip"/></div>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
    </table>
    <p>
       <a href="Javascript:document.myform.submit()"><img src="../bitmaps/<%=skin%>/buttons/lagre.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:location='index.jsp'"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </p>
</form>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>