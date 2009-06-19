<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.exception.ExceptionHandler,
                 no.kantega.publishing.common.Aksess"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="no.kantega.publishing.security.SecuritySession"%>
<%@ page import="java.util.Properties"%>
<%@ page import="java.io.IOException"%>
<%@ page import="no.kantega.commons.log.Log"%>
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
    request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());
    
    response.setDateHeader("Expires", 0);
    ExceptionHandler handler = (ExceptionHandler)request.getSession(false).getAttribute("handler");
    String error = handler.getMessage();
    String details = handler.getDetails();

    DateFormat df = new SimpleDateFormat("yyyy.MM.dd.MM - HH:mm");

    String ident = "";    String email = "";
    SecuritySession secSession = SecuritySession.getInstance(request);
    if (secSession != null && secSession.getUser() != null) {
        ident = secSession.getUser().getId();
        email = secSession.getUser().getEmail();
    }


    Properties versionInfo = new Properties();
    Properties webappVersionInfo = new Properties();

    try {
        versionInfo.load(getClass().getResourceAsStream("/aksess-version.properties"));
    } catch (IOException e) {
        Log.info("info/index.jsp", "aksess-version.properties not found", null, null);
    }
    try {
        webappVersionInfo.load(getClass().getResourceAsStream("/aksess-webapp-version.properties"));
    } catch (IOException e) {
        Log.info("info/index.jsp", "aksess-webapp-version.properties not found", null, null);
    }
%>
<html>
<head>
<title>Feil</title>
<link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/login/login.css">
</head>
<body>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td align="center">
                <table border="0" cellspacing="0" cellpadding="0" width="315">
                    <tr>
                        <td width="1" rowspan="3" class="frame"><img src="<%=Aksess.getContextPath()%>/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="310" class="frame"><img src="<%=Aksess.getContextPath()%>/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="1" rowspan="3" class="frame"><img src="<%=Aksess.getContextPath()%>/login/bitmaps/blank.gif" width="1" heigth="1"></td>
                        <td width="2" rowspan="3" class="shadow" valign="top"><img src="<%=Aksess.getContextPath()%>/login/bitmaps/corner.gif" width="2" heigth="2"></td>
                     </tr>
                     <tr>
                        <td class="box">
                            <table border="0" cellspacing="2" cellpadding="2" align="center" width="310">
                                <tr>
                                    <td class="inpHeading"><strong><kantega:label key="aksess.error.error"/></strong></td>
                                </tr>
                                <tr>
                                    <td><%=error%></td>
                                </tr>
                                <tr>
                                    <td>
                                        <table width="100%" border="0">
                                            <tr>
                                                <td><a href="Javascript:history.back()"><kantega:label key="aksess.error.back"/></a></td>
                                                <%
                                                    if (error.startsWith("System")) {
                                                %>
                                                    <td align="right"><a href="Javascript:document.errorreport.submit()"><kantega:label key="aksess.error.sendreport"/></a></td>
                                                <%
                                                }
                                                %>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <form action="http://opensource.kantega.no/feedback/error" target="_new" method="post" name="errorreport">
                                    <input type="hidden" name="product" value="Aksess">
                                    <input type="hidden" name="timestamp" value="<%=df.format(new Date())%>">
                                    <input type="hidden" name="error" value="<%=error%>">
                                    <input type="hidden" name="details" value="<%=details%>">
                                    <input type="hidden" name="ident" value="<%=ident%>">
                                    <input type="hidden" name="email" value="<%=email%>">
                                    <input type="hidden" name="applicationUrl" value="<%=Aksess.getApplicationUrl()%>">
                                    <input type="hidden" name="site" value="<%=request.getServerName()%>">
                                    <input type="hidden" name="version" value="<%=Aksess.getVersion()%>">
                                    <input type="hidden" name="release" value="<%= versionInfo.get("revision") != null ? versionInfo.get("revision") : "?"%>/<%= webappVersionInfo.get("revision") != null ? webappVersionInfo.get("revision") : "?"%>">
                                </form>
                            </table>
                        </td>
                     </tr>
                     <tr>
                        <td class="frame"><img src="<%=Aksess.getContextPath()%>/login/bitmaps/blank.gif" width="1" height="1"></td>
                     </tr>
                     <tr>
                        <td colspan="4" class="shadow"><img src="<%=Aksess.getContextPath()%>/login/bitmaps/corner.gif" width="2" height="2"></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>