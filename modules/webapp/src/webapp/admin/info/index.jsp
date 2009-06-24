<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.commons.configuration.Configuration,
                 no.kantega.publishing.common.util.database.dbConnectionFactory,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 java.util.Properties,
                 java.io.IOException,
                 no.kantega.commons.log.Log,
                 java.text.DecimalFormat"%>
<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%@ page import="no.kantega.commons.configuration.ConfigurationLoader" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>info.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<%
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

    if ("true".equals(request.getParameter("reload"))) {
        Configuration conf = (Configuration) RootContext.getInstance().getBean("aksessConfiguration");
        ConfigurationLoader loader = (ConfigurationLoader) RootContext.getInstance().getBean("aksessConfigurationLoader");
        conf.setProperties(loader.loadConfiguration());
    }
    %>

    <body class="bodyWithMargin">
    <table border="0" cellspacing="0" cellpadding="0" width="400">
        <!-- Versjon -->
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.version"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><%=Aksess.getVersion()%> (<kantega:label key="aksess.systeminfo.revisjon.aksess"/> <%= versionInfo.get("revision") != null ? versionInfo.get("revision") : "?"%>/<kantega:label key="aksess.systeminfo.revisjon.webapp"/> <%= webappVersionInfo.get("revision") != null ? webappVersionInfo.get("revision") : "?"%>)</td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.jvm.version"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><%=System.getProperty("java.vendor")%> <%=System.getProperty("java.version")%></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.installdir"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <%=Configuration.getApplicationDirectory()%>
            </td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.dbconnection.url"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <%=Aksess.getConfiguration().getString("database.url")%>
            </td>
        </tr>
        <%
            if (dbConnectionFactory.isPoolingEnabled()) {
        %>
            <tr>
                <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.dbconnection"/></b></td>
            </tr>
            <tr>
                <td>
                    <table>
                        <tr>
                            <td><kantega:label key="aksess.systeminfo.dbconnection.active"/></td>
                            <td><%=dbConnectionFactory.getActiveConnections()%></td>
                        </tr>
                        <tr>
                            <td><kantega:label key="aksess.systeminfo.dbconnection.idle"/></td>
                            <td><%=dbConnectionFactory.getIdleConnections()%></td>
                        </tr>
                        <tr>
                            <td><kantega:label key="aksess.systeminfo.dbconnection.max"/></td>
                            <td><%=dbConnectionFactory.getMaxConnections()%></td>
                        </tr>
                    </table>
                </td>
            </tr>
        <%
        }
        %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="8"></td>
        </tr>
        <tr>
            <td>
                <form action="index.jsp" method="get">
                    <input type="hidden" name="reload" value="true">
                    <input type="submit" value="<kantega:label key="aksess.systeminfo.reloadconfig"/>">
                    <c:if test="${param.reload}">
                        <div class="info">
                            <kantega:label key="aksess.systeminfo.reloadconfig.done"/>
                        </div>
                    </c:if>
                </form>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <%
            List xmlcache = aksessService.getXMLCacheSummary();
            if (xmlcache != null && xmlcache.size() > 0) {
        %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.xmlcache"/></b></td>
        </tr>
        <tr>
            <td>
                <table>
                    <tr>
                        <td><b><kantega:label key="aksess.systeminfo.xmlcache.id"/></b></td>
                        <td><b><kantega:label key="aksess.systeminfo.xmlcache.sistoppdatert"/></b></td>
                    </tr>
                <%
                    DateFormat df = new SimpleDateFormat(Aksess.getDefaultDatetimeFormat());
                    for (int i = 0; i < xmlcache.size(); i++) {
                        XMLCacheEntry cacheEntry = (XMLCacheEntry)xmlcache.get(i);
                %>
                    <tr>
                        <td><%=cacheEntry.getId()%></td>
                        <td><%=df.format(cacheEntry.getLastUpdated())%></td>
                    </tr>
               <%
                    }
               %>
                </table>
            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.systeminfo.memory"/></b></td>
        </tr>
       <%
           DecimalFormat format = new DecimalFormat("#,###.##");
           long mb = 1024*1024;
           double free = Runtime.getRuntime().freeMemory()/(double)mb;
           double total = Runtime.getRuntime().totalMemory()/(double)mb;
           double max = Runtime.getRuntime().maxMemory()/(double)mb;
       %>
        <tr>
            <td>
                <table>
                    <tr>
                        <td><kantega:label key="aksess.systeminfo.memory.free"/></td>
                        <td align="right"><%=format.format(free)%> MB</td>
                    </tr>
                    <tr>
                        <td><kantega:label key="aksess.systeminfo.memory.total"/></td>
                        <td align="right"><%=format.format(total)%> MB</td>
                    </tr>
                    <tr>
                        <td><kantega:label key="aksess.systeminfo.memory.max"/></td>
                        <td align="right"><%=format.format(max)%> MB</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>