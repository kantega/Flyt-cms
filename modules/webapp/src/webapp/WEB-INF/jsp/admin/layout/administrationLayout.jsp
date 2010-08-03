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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/wro/admin-administrationlayout.css">
    <script type="text/javascript">
        $(document).ready(function() {
            var title = document.title
            $("#Navigator span.title a").each(function() {
                var linkTitle = $(this).html();
                if (title.indexOf(linkTitle) != -1) {
                    $(this).addClass("selected");
                }
            });            
        });
    </script>
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
    <div id="Content" class="administration">
        <div id="Navigation">
            <div id="Navigator">
                <ul class="navigator">
                    <li><span class="title"><a href="<%=root%>ViewSystemInformation.action"><kantega:label key="aksess.systeminfo.title"/></a></span></li>
                    <li><span class="title"><a href="<%=root%>ListSites.action"><kantega:label key="aksess.sites.title"/></a></span></li>
                    <li><span class="title"><a href="<%=root%>ReloadTemplateConfiguration.action"><kantega:label key="aksess.templateconfig.title"/></a></span></li>
                    <%
                        if (Aksess.isTopicMapsEnabled()) {
                    %>
                    <li><span class="title"><a href="<%=root%>topicmaps/ListTopicMaps.action"><kantega:label key="aksess.topicmaps.title"/></a></span></li>
                    <%
                        }
                    %>
                    <li class="open"><kantega:label key="aksess.search.title"/>
                    <ul class="navigator">
                        <li><span class="title"><a href="<%=root%>ViewSearchLog.action"><kantega:label key="aksess.search.log.title"/></a></span></li>
                        <li><span class="title"><a href="<%=root%>RebuildIndex.action"><kantega:label key="aksess.search.rebuild.title"/></a></span></li>
                    </ul>
                    </li>
                    <li><kantega:label key="aksess.security.title"/>
                        <ul class="navigator">
                            <li><span class="title"><a href="<%=root%>useradmin/profile/"><kantega:label key="aksess.useradmin.profile.title"/></a></span></li>
                            <li><span class="title"><a href="<%=root%>useradmin/role/"><kantega:label key="aksess.useradmin.role.title"/></a></span></li>
                            <li><span class="title"><a href="<%=root%>ViewAllPermissions.action"><kantega:label key="aksess.viewpermissions.title"/></a></span></li>
                            <%
                                if (Aksess.isEventLogEnabled()) {
                            %>
                            <li><span class="title"><a href="<%=root%>SearchEventLog.action"><kantega:label key="aksess.eventlog.title"/></a></span></li>
                            <%
                                }
                            %>
                            <li><span class="title"><a href="<%=root%>ListContentLocks.action"><kantega:label key="aksess.locks.title"/></a></span></li>
                        </ul>
                    </li>
                    <li><kantega:label key="aksess.overview.title"/>
                        <ul class="navigator">
                            <li><span class="title"><a href="<%=root%>ListAliases.action"><kantega:label key="aksess.aliases.title"/></a></span></li>
                            <li><span class="title"><a href="<%=root%>ListContentExpiration.action"><kantega:label key="aksess.contentexpire.title"/></a></span></li>
                            <li><span class="title"><a href="<%=root%>ListUserChanges.action"><kantega:label key="aksess.userchanges.title"/></a></span></li>
                            <li><span class="title"><a href="<%=root%>ViewMailSubscribers.action"><kantega:label key="aksess.mailsubscription.title"/></a></span></li>
                        </ul>
                    </li>

                </ul>
            </div>
        </div>

        <div id="MainPane">
            <div id="MainPaneContent">
            <kantega:getsection id="content"/>
            </div>
            <div class="clearing"></div>
        </div>
        <div class="clearing"></div>
    </div>

</kantega:section>

<%@include file="commonLayout.jsp"%>