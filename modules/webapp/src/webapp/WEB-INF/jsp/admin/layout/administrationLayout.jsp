<%@ page import="no.kantega.publishing.common.data.enums.ContentStatus" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
<%@ page buffer="none" %>
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

<kantega:section id="head">
    <%@ include file="../../../../admin/publish/include/calendarsetup.jsp"%>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
</kantega:section>

<kantega:section id="toolsMenu">
</kantega:section>

<kantega:section id="body">
    <%
        String root = Aksess.getContextPath() + "/admin/administration/";
    %>
    <div id="Content">
        <div id="Navigation">
            <div id="Navigator">
                <ul>
                    <li><a href="<%=root%>ViewSystemInformation.action"><kantega:label key="aksess.systeminfo.title"/></a></li>
                    <li><a href="<%=root%>ListSites.action"><kantega:label key="aksess.sites.title"/></a></li>
                    <li><a href="<%=root%>ReloadTemplateConfiguration.action"><kantega:label key="aksess.templateconfig.title"/></a></li>
                    <%
                        if (Aksess.isTopicMapsEnabled()) {
                    %>
                    <li><a href="<%=root%>ListTopicMaps.action"><kantega:label key="aksess.topicmaps.title"/></a></li>
                    <%
                        }
                    %>
                    <li><kantega:label key="aksess.search.title"/></li>
                    <ul>
                        <li><a href="<%=root%>ViewSearchLog.action"><kantega:label key="aksess.search.log.title"/></a></li>
                        <li><a href="<%=root%>RebuildIndex.action"><kantega:label key="aksess.search.rebuild.title"/></a></li>
                    </ul>
                    <li><kantega:label key="aksess.security.title"/>
                        <ul>
                            <li><a href="<%=root%>useradmin/profile/"><kantega:label key="aksess.useradmin.profile.title"/></a></li>
                            <li><a href="<%=root%>useradmin/role/"><kantega:label key="aksess.useradmin.role.title"/></a></li>
                            <li><a href="<%=root%>ViewAllPermissions.action"><kantega:label key="aksess.viewpermissions.title"/></a></li>
                            <%
                                if (Aksess.isEventLogEnabled()) {
                            %>
                            <li><a href="<%=root%>SearchEventLog.action"><kantega:label key="aksess.eventlog.title"/></a></li>
                            <%
                                }
                            %>
                            <li><a href="<%=root%>ListContentLocks.action"><kantega:label key="aksess.locks.title"/></a></li>
                        </ul>
                    </li>
                    <li><kantega:label key="aksess.overview.title"/>
                        <ul>
                            <li><a href="<%=root%>ListAliases.action"><kantega:label key="aksess.aliases.title"/></a></li>
                            <li><a href="<%=root%>ListContentExpiration.action"><kantega:label key="aksess.contentexpire.title"/></a></li>
                            <li><a href="<%=root%>ListUserChanges.action"><kantega:label key="aksess.userchanges.title"/></a></li>
                            <li><a href="<%=root%>ViewMailSubscribers.action"><kantega:label key="aksess.mailsubscription.title"/></a></li>
                        </ul>
                    </li>
                    
                </ul>
            </div>
            <div id="Framesplit">
                <div id="FramesplitDrag"></div>
            </div>
        </div>

        <div id="MainContentPane">
            <kantega:getsection id="content"/>
            <div class="clearing"></div>
        </div>
    </div>

</kantega:section>

<%@include file="commonLayout.jsp"%>