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

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/navigate.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/navigate.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/content.jjs"></script>
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
                        <form name="myform" style="display:none" action="${pageContext.request.contextPath}/admin/publish/SaveContentPreview.action" method="post">
                            <%@include file="fragments/editContentButtons.jsp"%>
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