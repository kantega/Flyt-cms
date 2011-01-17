<%@ page import="no.kantega.publishing.common.data.WorkList" %>
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
    <kantega:label key="aksess.mypage.title"/>
</kantega:section>

<kantega:section id="contentclass">mypage</kantega:section>

<kantega:section id="head extras">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { content : {} };
        }
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/aksess-i18n.jjs"></script>
    <script type="text/javascript" src='<kantega:expireurl url="/wro-oa/admin-mypage.js"/>'></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $("#SettingsButton").click(function(){
                openaksess.common.modalWindow.open({href: 'MyPageSettings.action', iframe: true, title: '<kantega:label key="aksess.tools.settings"/>'});
            });
        });
    </script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
        google.load("visualization", "1", {packages:["areachart"]});
    </script>
</kantega:section>

<kantega:section id="modesMenu">
    <!--
    <div class="buttonGroup">
        <a href="#" class="button first disabled" id="SettingsButton"><span class="settings"><kantega:label key="aksess.tools.settings"/></span></a>
        <span class="buttonSeparator"></span>
        <a href="#" class="button last disabled" id="ResetMyPageButton"><span class="reset"><kantega:label key="aksess.tools.reset"/></span></a>
    </div>-->
</kantega:section>

<kantega:section id="toolsMenu">
</kantega:section>



<kantega:section id="content">
    <script type="text/javascript">
        function restore(itemId) {
            if (confirm("<kantega:label key="aksess.mypage.restore.confirm"/>")) {
                location.href = "RestoreDeletedItem.action?id=" + itemId;
            }
        }

        $(document).ready(function(){
            $("#WorkList").tabs();
            $("#PropertySearch").load("${pageContext.request.contextPath}/admin/mypage/plugins/PropertySearch.action");
            $("#GoogleAnalytics").load("${pageContext.request.contextPath}/admin/mypage/plugins/GoogleAnalytics.action", function() {
                widgetLoaded();
            });
            $("#ContentStatistics").load("${pageContext.request.contextPath}/admin/mypage/plugins/ContentStatistics.action");
            $("#OrgUnitStatistics").load("${pageContext.request.contextPath}/admin/mypage/plugins/OrgUnitStatistics.action");
            $("#UserInfo").load("${pageContext.request.contextPath}/admin/mypage/plugins/UserInfo.action");
        });
    </script>

    <div class="widgetcolumn">

        <c:if test="${fn:length(contentForApproval) > 0}">
            <div class="widget">
                <div class="widget-header"><h2><kantega:label key="aksess.mypage.approval"/></h2></div>
                <div class="widget-content">
                    <table class="fullWidth">
                        <thead>
                        <tr>
                            <th class="title"><kantega:label key="aksess.mypage.page"/></th>
                            <th class="modifiedby"><kantega:label key="aksess.mypage.modifiedby"/></th>
                            <th class="date"><kantega:label key="aksess.mypage.lastmodified"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${contentForApproval}" varStatus="status">
                            <tr class="tableRow${status.index mod 2}">
                                <td class="title"><a href="${pageContext.request.contextPath}/admin/publish/Navigate.action?thisId=<aksess:getattribute name="id" obj="${item}"/>"><aksess:getattribute name="title" obj="${item}"/></a></td>
                                <td><aksess:getattribute name="modifiedby" obj="${item}"/></td>
                                <td class="date"><aksess:getattribute name="lastmodified" obj="${item}"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="ui-state-highlight">
                         <kantega:label key="aksess.mypage.approval.help"/>
                    </div>
                </div>
            </div>
        </c:if>
        <div class="widget">
            <div class="widget-header"><h2><kantega:label key="aksess.mypage.mycontent"/></h2></div>
            <div class="widget-content">
                <div id="WorkList">
                    <ul>
                        <c:forEach var="worklist" items="${myWorkList}" varStatus="status">
                            <%
                                WorkList w = (WorkList)pageContext.getAttribute("worklist");
                                request.setAttribute("workListDescription", w.getDescription());
                            %>
                            <c:if test="${not empty worklist}">
                                <li><a href="#WorkList-${status.index+1}"><kantega:label key="aksess.mypage.${workListDescription}"/></a></li>
                            </c:if>
                        </c:forEach>
                    </ul>
                    <c:forEach var="worklist" items="${myWorkList}" varStatus="status">
                        <c:if test="${not empty worklist}">
                            <div id="WorkList-${status.index+1}">
                                <table class="fullWidth">
                                    <thead>
                                    <tr>
                                        <th class="title"><kantega:label key="aksess.mypage.page"/></th>
                                        <th class="date"><kantega:label key="aksess.mypage.lastmodified"/></th>
                                    </tr>
                                    </thead>
                                    <c:forEach var="item" items="${worklist}" varStatus="pageNo">
                                        <tr class="tableRow${pageNo.index mod 2}">
                                            <td><a href="${pageContext.request.contextPath}/admin/publish/Navigate.action?thisId=<aksess:getattribute name="id" obj="${item}"/>"><aksess:getattribute name="title" obj="${item}"/></a></td>
                                            <td><aksess:getattribute name="lastmodified" obj="${item}"/></td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>

        <c:if test="${fn:length(myDeletedItems) > 0}">
            <div class="widget">
                <div class="widget-header"><h2><kantega:label key="aksess.mypage.deleted"/></h2></div>
                <div class="widget-content">
                    <table class="fullWidth">
                        <thead>
                        <tr>
                            <th class="title"><kantega:label key="aksess.mypage.page"/></th>
                            <th class="date"><kantega:label key="aksess.mypage.deleteddate"/></th>
                            <th class="action">&nbsp;</th>
                        </tr>
                        </thead>
                        <c:forEach var="item" items="${myDeletedItems}" varStatus="status">
                            <tr class="tableRow${status.index mod 2}">
                                <td><c:out value="${item.title}"/></td>
                                <td><admin:formatdate date="${item.deletedDate}"/></td>
                                <td><a href="#" onclick="restore('${item.id}')" class="button restore"><span><kantega:label key="aksess.mypage.restore"/></span></a></td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </c:if>
        <div class="widget">
            <div class="widget-header">
                <h2><kantega:label key="aksess.userinformation.title"/></h2>
            </div>
            <div class="widget-content">
                <div id="UserInfo"><div class="ajaxloading"><kantega:label key="aksess.ajax.loading"/></div></div>
            </div>
        </div>
    </div>

    <div class="widgetcolumn">
        <div class="widget">
            <div class="widget-header"><h2><kantega:label key="aksess.propertysearch.title"/></h2></div>
            <div class="widget-content">
                <div id="PropertySearch"><div class="ajaxloading"><kantega:label key="aksess.ajax.loading"/></div></div>
            </div>
        </div>
        <div class="widget">
            <div class="widget-header">
                <h2><kantega:label key="aksess.contentstatistics.title"/></h2>
            </div>
            <div class="widget-content">
                <div id="ContentStatistics"><div class="ajaxloading"><kantega:label key="aksess.ajax.loading"/></div></div>
            </div>
        </div>
    </div>

    <div class="widgetcolumn">
        <%-- Look up property from aksess-project.conf --%>
        <c:set var="showOrgUnitStatistics"><aksess:getconfig key="mypage.orgunitstatistics.show" /></c:set>
        <c:if test="${showOrgUnitStatistics == true}">
            <div class="widget">
                <div class="widget-header">
                    <h2><kantega:label key="aksess.orgunitstatistics.title"/></h2>
                </div>
                <div class="widget-content">
                    <div id="OrgUnitStatistics"><div class="ajaxloading"><kantega:label key="aksess.ajax.loading"/></div></div>
                </div>
            </div>
        </c:if>

        <div class="widget">
            <div class="widget-header">
                <h2><a href="https://www.google.com/analytics/reporting/dashboard"><kantega:label key="aksess.googleanalytics.title"/></a></h2>
            </div>
            <div class="widget-content">
                <div id="GoogleAnalytics"><div class="ajaxloading"><kantega:label key="aksess.ajax.loading"/></div></div>
            </div>
        </div>
    </div>

</kantega:section>


<%@ include file="../layout/fullwidthLayout.jsp" %>