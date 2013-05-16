<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.systeminfo.title"/>
</kantega:section>

<kantega:section id="content">

    <admin:box>
            <h1><kantega:label key="aksess.systeminfo.title"/></h1>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.version"/></div>
                <div class="content">
                    <div class="formElement">
                        <div class="content">
                            <div class="row">
                                OpenAksess: ${aksessVersion} (<kantega:label key="aksess.systeminfo.revision"/> ${aksessRevision},
                                                    <fmt:formatDate value="${aksessTimestamp}" pattern="dd.MM.yyyy HH:mm:ss" />)
                            </div>
                            <div class="row">
                                Webapp: ${webappVersion} (<kantega:label key="aksess.systeminfo.revision"/> ${webappRevision},
                                            <fmt:formatDate value="${webappTimestamp}" pattern="dd.MM.yyyy HH:mm:ss" />)
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.starttime"/></div>
                <div class="content">
                    <fmt:formatDate value="${jvmStartDate}" pattern="dd.MM.yyyy HH:mm:ss" />
                </div>
            </div>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.jvm.version"/></div>
                <div class="content">
                     ${vmVendor} ${vmName} ${javaVersion}, ${vmVersion}
                </div>
            </div>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.servletengine"/></div>
                <div class="content">
                    <%=getServletConfig().getServletContext().getServerInfo()%>
                </div>
            </div>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.installdir"/></div>
                <div class="content">${installDir}</div>
            </div>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.dbconnection.url"/></div>
                <div class="content">${databaseUrl}</div>
            </div>

            <c:if test="${dbConnectionPoolEnabled}">
                <div class="formElement">
                    <div class="heading"><kantega:label key="aksess.systeminfo.dbconnection"/></div>
                    <div class="content">
                        <kantega:label key="aksess.systeminfo.dbconnection.active"/>: <%=dbConnectionFactory.getActiveConnections()%>,
                        <kantega:label key="aksess.systeminfo.dbconnection.idle"/>: <%=dbConnectionFactory.getIdleConnections()%>,
                        <kantega:label key="aksess.systeminfo.dbconnection.max"/>: <%=dbConnectionFactory.getMaxConnections()%>.

                    </div>
                </div>
            </c:if>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.memory"/></div>
                <div class="content">
                    <kantega:label key="aksess.systeminfo.memory.free"/>: ${freeMemory} MB,
                    <kantega:label key="aksess.systeminfo.memory.total"/>: ${totalMemory} MB,
                    <kantega:label key="aksess.systeminfo.memory.max"/>: ${maxMemory} MB
                </div>
            </div>
    </admin:box>
    <c:if test="${fn:length(xmlCache) > 1}">
        <admin:box>
                <h1><kantega:label key="aksess.systeminfo.xmlcache"/></h1>

                <table>
                    <thead>
                    <tr>
                        <th><kantega:label key="aksess.systeminfo.xmlcache.id"/></th>
                        <th><kantega:label key="aksess.systeminfo.xmlcache.sistoppdatert"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="cacheEntry" items="${xmlCache}" varStatus="status">
                        <tr class="tableRow${status.index mod 2}">
                            <td>${cacheEntry.id}</td>
                            <td><admin:formatdate date="${cacheEntry.lastUpdated}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
        </admin:box>
    </c:if>

    <admin:box>
            <h1><kantega:label key="aksess.systeminfo.config"/></h1>

            <table id="PropertiesTable">
                <thead>
                <tr>
                    <th class="parameter"><kantega:label key="aksess.systeminfo.config.property"/></th>
                    <th class="property"><kantega:label key="aksess.systeminfo.config.value"/></th>
                </tr>
                </thead>
                <tbody>
                <%
                    Properties configProperties = (Properties)request.getAttribute("configProperties");

                    Iterator properties = configProperties.entrySet().iterator();

                    SortedSet<String> sortedProperties= new TreeSet<String>();

                    while (properties.hasNext()) {
                        Map.Entry entry = (Map.Entry) properties.next();
                        String key = entry.getKey().toString();
                        sortedProperties.add(key);
                    }
                    Iterator it = sortedProperties.iterator();
                    while (it.hasNext()) {
                        String key = (String)it.next();
                        String value = configProperties.getProperty(key);
                        if (key.contains("password")) {
                            value = "******";
                        }
                %>
                <tr>
                    <td class="parameter"><%=key%></td>
                    <td class="property"><%=value%></td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
            <form action="" method="get">
                <input type="hidden" name="reload" value="true">
                <div class="buttonGroup">
                    <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.systeminfo.reloadconfig"/>"></span>
                </div>
                <c:if test="${param.reload}">
                    <div class="ui-state-highlight">
                        <kantega:label key="aksess.systeminfo.reloadconfig.done"/>
                    </div>
                </c:if>
            </form>
    </admin:box>
</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>