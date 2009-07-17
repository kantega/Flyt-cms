<%@ page import="no.kantega.publishing.common.data.WorkList" %>
<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.systeminfo.title"/></legend>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.version"/></div>
                <div class="content">
                        ${aksessVersion} (<kantega:label key="aksess.systeminfo.revisjon.aksess"/> ${aksessRevision} / <kantega:label key="aksess.systeminfo.revisjon.webapp"/> ${webappRevision})
                </div>
            </div>

            <div class="formElement">
                <div class="heading"><kantega:label key="aksess.systeminfo.jvm.version"/></div>
                <div class="content">
                    <%=System.getProperty("java.vendor")%> <%=System.getProperty("java.version")%>
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
                    <kantega:label key="aksess.systeminfo.memory.free"/>: ${freeMemory},
                    <kantega:label key="aksess.systeminfo.memory.total"/>: ${totalMemory},
                    <kantega:label key="aksess.systeminfo.memory.max"/>: ${maxMemory},
                </div>
            </div>
        </fieldset>
    </div>

    <c:if test="${fn:length(xmlCache) > 1}">
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.systeminfo.xmlcache"/></legend>

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
            </fieldset>
        </div>
    </c:if>

    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.systeminfo.config"/></legend>

            <table>
                <thead>
                <tr>
                    <th><kantega:label key="aksess.systeminfo.config.property"/></th>
                    <th><kantega:label key="aksess.systeminfo.config.value"/></th>
                </tr>
                </thead>
                <tbody>
                <%
                    Properties configProperties = (Properties)request.getAttribute("configProperties");
                    Iterator properties = configProperties.entrySet().iterator();
                    while (properties.hasNext()) {
                        Map.Entry entry = (Map.Entry) properties.next();
                %>
                <tr>
                    <td><%=entry.getKey()%></td>
                    <td><%=entry.getValue()%></td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </fieldset>

        <form action="" method="get">
            <input type="hidden" name="reload" value="true">
            <input type="submit" class="button ok" value="<kantega:label key="aksess.systeminfo.reloadconfig"/>">
            <c:if test="${param.reload}">
                <div class="info">
                    <kantega:label key="aksess.systeminfo.reloadconfig.done"/>
                </div>
            </c:if>
        </form>
    </div>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>