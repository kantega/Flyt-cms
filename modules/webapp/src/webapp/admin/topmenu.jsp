<%@ page import="no.kantega.commons.util.URLHelper" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="java.util.Locale" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
    RequestParameters param = new RequestParameters(request);
    String activetab = param.getString("activetab");
    Locale lang = (Locale)request.getAttribute("aksess_locale");
    String locale_bildesti_framework = "bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/framework/";

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>topmenu.jsp</title>
    <link rel="stylesheet" type="text/css" href="css/<%=skin%>.css">
    <script language="Javascript" type="text/javascript">
    function showCopyright() {
        window.open("../login/about.jsp", "aboutWindow", "dependent,toolbar=no,width=230,height=50,resizable=yes");
    }

    function changeActiveTab(tab) {
        window.parent.topmenu.location = 'topmenu.jsp?activetab=' + tab;
        window.parent.main.location = tab + '/index.jsp';
    }
    </script>
</head>
<body>
    <map name="logo">
        <area type="rect" coords="300, 0, 500, 43" href="Javascript:showCopyright()">
    </map>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td background="<%=locale_bildesti_framework%>top_logo_spacer.gif" align="right"><img src="<%=locale_bildesti_framework%>top_logo.gif" width="500" height="43" usemap="#logo" border="0"></td>
        </tr>
    </table>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td background="<%=locale_bildesti_framework%>top_nav_spacer.gif" valign="top">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                    <% if (activetab.equals("publish")) { %>
                        <td><img src="<%=locale_bildesti_framework%>top_nav_publish_f2.gif" width="82" height="26" alt="Publisering av innhold" title="Publisering av innhold" border="0"></td>
                    <% } else {%>
                        <td><a href="Javascript:changeActiveTab('publish')"><img src="<%=locale_bildesti_framework%>top_nav_publish.gif" width="82" height="26" alt="Publisering av innhold" title="Publisering av innhold" border="0"></a></td>
                    <% } %>
                    <% if (activetab.equals("multimedia")) { %>
                        <td><img src="<%=locale_bildesti_framework%>top_nav_multimedia_f2.gif" width="100" height="26" alt="Multimediaarkiv" title="Multimediaarkiv" border="0"></td>
                    <% } else {%>
                        <td><a href="Javascript:changeActiveTab('multimedia')"><img src="<%=locale_bildesti_framework%>top_nav_multimedia.gif" width="100" height="26" alt="Multimediaarkiv" title="Multimediaarkiv" border="0"></a></td>
                    <% } %>
                    <%
                        if (Aksess.isFormsEnabled() && (securitySession.isUserInRole(Aksess.getAdminRole()) || securitySession.isUserInRole(Aksess.getFormsRoles()))) {
                            if (activetab.equals("forms")) {
                    %>
                            <td><img src="<%=locale_bildesti_framework%>top_nav_skjema_f2.gif" width="73" height="26" alt="Skjemaadministrasjon" title="Skjemaadministrasjon" border="0"></td>
                        <%  } else {%>
                            <td><a href="Javascript:changeActiveTab('forms')"><img src="<%=locale_bildesti_framework%>top_nav_skjema.gif" width="73" height="26" alt="Skjemaadministrasjon" title="Systemadministrasjon" border="0"></a></td>
                    <%
                            }
                        }
                    %>
                    <%
                        if (securitySession.isUserInRole(Aksess.getAdminRole())) {
                            if (activetab.equals("systemadmin")) {
                    %>
                            <td><img src="<%=locale_bildesti_framework%>top_nav_admin_f2.gif" width="73" height="26" alt="Systemadministrasjon" title="Systemadministrasjon" border="0"></td>
                        <%  } else {%>
                            <td><a href="Javascript:changeActiveTab('systemadmin')"><img src="<%=locale_bildesti_framework%>top_nav_admin.gif" width="73" height="26" alt="Systemadministrasjon" title="Systemadministrasjon" border="0"></a></td>
                    <%
                            }
                        }
                    %>
                    </tr>
                </table>
            </td>
            <td background="<%=locale_bildesti_framework%>top_nav_spacer.gif" align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                    <% if (activetab.equals("help")) { %>
                        <td><img src="<%=locale_bildesti_framework%>top_nav_hjelp_f2.gif" width="67" height="26" alt="Hjelp" title="Hjelp" border="0"></td>
                    <% } else {%>
                        <td><a target="aksessHelp" href="http://www.kantega.no/aksess-help/help/"><img src="<%=locale_bildesti_framework%>top_nav_hjelp.gif" width="67" height="26" alt="Hjelp" title="Hjelp" border="0"></a></td>
                    <% } %>
                        <td><a href="../Logout.action?redirect=<%=URLHelper.getRootURL(request)%>" target="_top"><img src="<%=locale_bildesti_framework%>top_nav_logout.gif" width="67" height="26" alt="Logg ut" title="Logg ut" border="0"></a></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="include/jsp_footer.jsf" %>