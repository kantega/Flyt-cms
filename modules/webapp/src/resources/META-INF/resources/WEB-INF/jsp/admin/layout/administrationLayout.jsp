<%@ page import="no.kantega.publishing.api.ui.UIServices" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-administrationlayout.css"/>">
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

        UIServices uiServices = (UIServices) WebApplicationContextUtils.getRequiredWebApplicationContext(getServletConfig().getServletContext()).getBean("uiServices");

        MenuItem menu = uiServices.createMenu();
        
        final String root = Aksess.getContextPath();
        final String adminRoot = root + "/admin/administration/";

        menu.addLink("aksess.systeminfo.title", adminRoot + "ViewSystemInformation.action");
        menu.addLink("aksess.sites.title", adminRoot + "ListSites.action");
        menu.addLink("aksess.templateconfig.title", adminRoot + "ReloadTemplateConfiguration.action");

        if (Aksess.isTopicMapsEnabled()) {
            menu.addLink("aksess.topicmaps.title", adminRoot + "topicmaps/ListTopicMaps.action");
        }

        MenuItem searchMenu = menu.addChildMenuItem("aksess.search.title");
        {
            searchMenu.addLink("aksess.search.log.title", adminRoot + "ViewSearchLog.action");
            searchMenu.addLink("aksess.search.rebuild.title", adminRoot + "RebuildIndex.action");
        }

        MenuItem securityMenu = menu.addChildMenuItem("aksess.security.title");
        {
            securityMenu.addLink("aksess.useradmin.profile.title" , adminRoot + "useradmin/profile/");
            securityMenu.addLink("aksess.useradmin.role.title", adminRoot + "useradmin/role/");
            securityMenu.addLink("aksess.viewpermissions.title", adminRoot + "ViewAllPermissions.action");

            if (Aksess.isEventLogEnabled()) {
                securityMenu.addLink("aksess.eventlog.title", adminRoot + "SearchEventLog.action");
                securityMenu.addLink("aksess.locks.title", adminRoot + "ListContentLocks.action");
            }

        }

        MenuItem overViewMenu = menu.addChildMenuItem("aksess.overview.title");

        {
            overViewMenu.addLink("aksess.aliases.title", adminRoot + "ListAliases.action");
            overViewMenu.addLink("aksess.contentexpire.title", adminRoot + "ListContentExpiration.action");
            overViewMenu.addLink("aksess.userchanges.title", adminRoot + "ListUserChanges.action");
            overViewMenu.addLink("aksess.mailsubscription.title", adminRoot + "ViewMailSubscribers.action");
        }

        PluginManager<OpenAksessPlugin> pluginManager = (PluginManager<OpenAksessPlugin>) WebApplicationContextUtils.getRequiredWebApplicationContext(getServletConfig().getServletContext()).getBean("pluginManager");




        // Merge plugin menus into admin menu

        Locale locale = RequestContextUtils.getLocale(request);
        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            PluginMessageSource source = new PluginMessageSource(plugin);

            for(UIContribution contrib : plugin.getUIContributions()) {
                for(MenuItem adminmenuitems : contrib.getAdminMenuItems()) {
                    for(MenuItem item : adminmenuitems.getChildMenuItems()) {
                        addPluginMenuItem(menu, item, source, locale, root);
                    }

                }
        
            }
        }
        pageContext.setAttribute("adminMenu", menu);
    %>
    <div id="Content" class="administration">
        <div id="Navigation">
            <div id="Navigator">
                <ul class="navigator">
                    <c:forEach items="${adminMenu.childMenuItems}" var="item">
                        <li>
                            <c:choose>
                                <c:when test="${item.href != null}">
                                    <span class="title"><a href="${item.href}"><kantega:label key="${item.label}"/></a></span>
                                </c:when>
                                <c:when test="${not empty item.childMenuItems}">
                                    <kantega:label key="${item.label}"/>
                                    <ul class="navigator">
                                        <c:forEach items="${item.childMenuItems}" var="item">
                                            <li>
                                                <span class="title"><a href="${item.href}"><kantega:label key="${item.label}"/></a></span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                            </c:choose>
                        </li>
                    </c:forEach>
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

<%!
    private void addPluginMenuItem(MenuItem menu, MenuItem item, PluginMessageSource source, Locale locale, String root) {
        if(item.getChildMenuItems().isEmpty()) {
            if(item.getHref() != null) {
                String href = item.getHref();

                if(!href.startsWith("http")) {
                    href = root + href;
                }
                menu.addLink(source.getMessage(item.getLabel(), null, locale), href);
            }
        } else {

            MenuItem parent = null;
            for(MenuItem child : menu.getChildMenuItems()) {
                if(child.getLabel().equals(item.getLabel())) {
                    parent = child;
                    break;
                }
            }
            if(parent == null) {
                parent = menu.addChildMenuItem(source.getMessage(item.getLabel(), null, locale));
            }
            for(MenuItem child : new ArrayList<MenuItem>(item.getChildMenuItems())) {
                addPluginMenuItem(parent, child, source, locale, root);
            }

        }

    }

%>