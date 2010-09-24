<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>

<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-contentnavigatelayout.css"/>">

    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { content : {} };
        }
        if (typeof properties.content == 'undefined') {
            properties.content = {};
        }
        properties.content['labels'] = {
            confirmDelete : '<kantega:label key="aksess.confirmdelete.title" escapeJavascript="true"/>',
            copyPaste : '<kantega:label key="aksess.copypaste.title" escapeJavascript="true"/>',
            publishinfoPeriod : '<kantega:label key="aksess.publishinfo.period" escapeJavascript="true"/>',
            editPermissions : '<kantega:label key="aksess.editpermissions.title" escapeJavascript="true"/>',
            reject : '<kantega:label key="aksess.reject.title" escapeJavascript="true"/>',
            linkcheckField : '<kantega:label key="aksess.linkcheck.field" escapeJavascript="true"/>',
            linkcheckUrl : '<kantega:label key="aksess.linkcheck.url" escapeJavascript="true"/>',
            linkcheckStatus : '<kantega:label key="aksess.linkcheck.status" escapeJavascript="true"/>',
            linkcheckLastchecked : '<kantega:label key="aksess.linkcheck.lastchecked" escapeJavascript="true"/>',
            linkcheckTimeschecked : '<kantega:label key="aksess.linkcheck.timeschecked" escapeJavascript="true"/>',
            linkcheckStatus10: '<kantega:label key="aksess.linkcheck.statuses.10" escapeJavascript="true"/>',
            linkcheckStatus11: '<kantega:label key="aksess.linkcheck.statuses.11" escapeJavascript="true"/>',
            linkcheckStatus2: '<kantega:label key="aksess.linkcheck.statuses.2" escapeJavascript="true"/>',
            linkcheckStatus4: '<kantega:label key="aksess.linkcheck.statuses.4" escapeJavascript="true"/>',
            linkcheckStatus5: '<kantega:label key="aksess.linkcheck.statuses.5" escapeJavascript="true"/>',
            linkcheckStatus6: '<kantega:label key="aksess.linkcheck.statuses.6" escapeJavascript="true"/>',
            linkcheckStatus7: '<kantega:label key="aksess.linkcheck.statuses.7" escapeJavascript="true"/>',
            linkcheckStatus8: '<kantega:label key="aksess.linkcheck.statuses.8" escapeJavascript="true"/>',
            linkcheckStatus9: '<kantega:label key="aksess.linkcheck.statuses.9" escapeJavascript="true"/>',
            httpStatus401: '<kantega:label key="aksess.linkcheck.httpstatus.401" escapeJavascript="true"/>',
            httpStatus404: '<kantega:label key="aksess.linkcheck.httpstatus.404" escapeJavascript="true"/>',
            httpStatus500: '<kantega:label key="aksess.linkcheck.httpstatus.500" escapeJavascript="true"/>',
            details : '<kantega:label key="aksess.infoslider.details" escapeJavascript="true"/>',
            publishinfoAlias : '<kantega:label key="aksess.publishinfo.alias" escapeJavascript="true"/>',
            contentTitle : '<kantega:label key="aksess.contentproperty.title" escapeJavascript="true"/>',
            contentLastModified : '<kantega:label key="aksess.contentproperty.lastmodified" escapeJavascript="true"/>',
            contentModifiedBy : '<kantega:label key="aksess.contentproperty.modifiedby" escapeJavascript="true"/>',
            contentApprovedBy : '<kantega:label key="aksess.contentproperty.approvedby" escapeJavascript="true"/>',
            contentChangeFrom : '<kantega:label key="aksess.contentproperty.changefrom" escapeJavascript="true"/>',
            contentExpireDate : '<kantega:label key="aksess.contentproperty.expiredate" escapeJavascript="true"/>',
            contentOwnerPerson : '<kantega:label key="aksess.contentproperty.ownerperson" escapeJavascript="true"/>',
            contentDisplayTemplate : '<kantega:label key="aksess.contentproperty.displayTemplate" escapeJavascript="true"/>',
            associations : '<kantega:label key="aksess.infoslider.associations" escapeJavascript="true"/>'
        };
        properties['objectTypeAssociation'] = <%=ObjectType.ASSOCIATION%>;
    </script>
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/admin-contentnavigatelayout.js"/>"></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/interface/ContentClipboardHandler.js'></script>
    <kantega:getsection id="head extras"/>
    <%@include file="fragments/publishModesAndButtonsJS.jsp"%>
    <script type="text/javascript">
        var hasSubmitted = false;
        function saveContent(status) {
            openaksess.common.debug("contentNavigateLayout.saveContent(): status: " + status + ", hasSubmitted: " + hasSubmitted);
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.status.value = status;
                document.myform.submit();
            }
        }

        $(document).ready(function(){
            $("#EditContentButtons .approve").click(function() {
                openaksess.content.publish.approve(stateHandler.getState());
            });
            $("#EditContentButtons .reject").click(function() {
                openaksess.content.publish.reject(stateHandler.getState());
            });
        });
    </script>

</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
    <%@include file="fragments/publishModesMenu.jsp"%>
</kantega:section>

<kantega:section id="toolsMenu">
    <%@include file="fragments/publishToolsMenu.jsp"%>
</kantega:section>



<kantega:section id="body">
    <div id="Content"<kantega:hassection id="contentclass"> class="<kantega:getsection id="contentclass"/>"</kantega:hassection>>
        <div id="Navigation">
            <div id="Filteroptions">
                <a href="#" class="filtersToggle"><kantega:label key="aksess.filteroptions.options"/></a>
                <div class="hideexpired">
                    <input type="checkbox" id="FilteroptionHideExpired">
                    <label for="FilteroptionHideExpired"><kantega:label key="aksess.filteroptions.hideexpired"/></label>
                </div>
                <div class="filters" style="display: none;">
                    <fieldset id="FilteroptionSort">
                        <legend><kantega:label key="aksess.navigator.sort"/></legend>
                        <div class="row">
                            <input type="radio" class="radio" name="sort" value="priority" id="FilteroptionSort_priority"><label class="radio" for="FilteroptionSort_priority"><kantega:label key="aksess.navigator.sort.priority"/></label>
                            <div class="clearing"></div>
                        </div>
                        <div class="row">
                            <input type="radio" class="radio" name="sort" value="lastmodified" id="FilteroptionSort_lastmodified"><label class="radio" for="FilteroptionSort_lastmodified"><kantega:label key="aksess.navigator.sort.lastmodified"/></label>
                            <div class="clearing"></div>
                        </div>
                        <div class="row">
                            <input type="radio" class="radio" name="sort" value="title" id="FilteroptionSort_title"><label class="radio" for="FilteroptionSort_title"><kantega:label key="aksess.navigator.sort.title"/></label>
                            <div class="clearing"></div>
                        </div>
                    </fieldset>
                    <fieldset id="FilteroptionSites">
                        <legend><kantega:label key="aksess.navigator.sites"/>&nbsp;(<a href="#" id="hideexpiredFilteroptionSites_all"><kantega:label key="aksess.navigator.sites.all"/></a>)</legend>
                        <div class="options">

                        <%-- The sites (options) are loaded by content.Contentstatus.updateFilters() --%>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="infoslider"></div>
            <div id="Navigator"></div>
            <div id="Framesplit"></div>
        </div>


        <div id="MainPane">

            <div id="Statusbar">
                <div id="Breadcrumbs"></div>
                <div class="supportMenu">
                    <a href="#" class="brokenLink"><kantega:label key="aksess.statusbar.brokenlink"/></a>
                    <a href="#" class="crossPublish"><kantega:label key="aksess.statusbar.crosspublished"/></a>
                    <a href="#" class="details"><kantega:label key="aksess.statusbar.details"/></a>
                </div>
            </div>

            <div class="infoslider"></div>

            <kantega:getsection id="content"/>

            <c:choose>
                <c:when test="${hasUnsavedChanges}">
                    <div id="EditContentButtons" class="buttonBar">
                        <%@include file="fragments/editContentButtons.jsp"%>
                        <form name="myform" style="display:none" action="${pageContext.request.contextPath}/admin/publish/SaveContentPreview.action" method="post">
                            <input type="hidden" name="status" value="">
                            <input type="hidden" name="action" value="">
                            <input type="hidden" name="currentId" value="${currentContent.id}">
                            <input type="hidden" name="isModified" id="IsModified" value="${currentContent.modified}">
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="EditContentButtons" class="buttonBar" style="display:none;">
                        <span class="barButton"><input type="button" class="approve" value="<kantega:label key="aksess.button.approve"/>"></span>
                        <span class="barButton"><input type="button" class="reject" value="<kantega:label key="aksess.button.reject"/>"></span>
                        <span class="ui-state-highlight"><kantega:label key="aksess.navigator.approve.help"/></span>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="clearing"></div>
    </div>

    <%-- Including the context menus so they are available to jQyery. They are default hidden (by css) from view. --%>
    <%@include file="fragments/contextMenu-page.jsp"%>
    <%@include file="fragments/contextMenu-link.jsp"%>
    <%@include file="fragments/contextMenu-shortcut.jsp"%>
    <%@include file="fragments/contextMenu-file.jsp"%>

</kantega:section>

<%@include file="commonLayout.jsp"%>