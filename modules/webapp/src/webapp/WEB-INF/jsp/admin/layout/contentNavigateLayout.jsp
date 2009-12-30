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
    <kantega:getsection id="head extras"/>
    <%@include file="fragments/publishModesAndButtonsJS.jsp"%>
    <script type="text/javascript">
        var hasSubmitted = false;
        function saveContent(status) {
            debug("contentNavigateLayout.saveContent(): status: " + status + ", hasSubmitted: " + hasSubmitted);
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.status.value = status;
                document.myform.submit();
            }
        }

        $(document).ready(function(){
            $("#EditContentButtons .approve").click(function() {
                Publish.approve(getQueryParam("thisId", currentUrl));
            });
            $("#EditContentButtons .reject").click(function() {
                Publish.reject(getQueryParam("thisId", currentUrl));
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
    <div class="buttonGroup">
        <a href="#" class="button disabled" id="NewSubpageButton"><span class="newSubpage"><kantega:label key="aksess.tools.newSubpage"/></span></a>
        <a href="#" class="button disabled" id="DeletePageButton"><span class="delete"><kantega:label key="aksess.tools.delete"/></span></a>
    </div>
    <div class="buttonGroup">
        <a href="#" class="button disabled" id="CutButton"><span class="cut"><kantega:label key="aksess.tools.cut"/></span></a>
        <a href="#" class="button disabled" id="CopyButton"><span class="copy"><kantega:label key="aksess.tools.copy"/></span></a>
        <a href="#" class="button disabled" id="PasteButton"><span class="paste"><kantega:label key="aksess.tools.paste"/></span></a>
    </div>
    <div class="buttonGroup">
        <a href="#" class="button disabled" id="DisplayPeriodButton"><span class="displayPeriod"><kantega:label key="aksess.tools.displayperiod"/></span></a>
        <a href="#" class="button disabled" id="PrivilegesButton"><span class="privileges"><kantega:label key="aksess.tools.privileges"/></span></a>
    </div>
</kantega:section>



<kantega:section id="body">
    <div id="Content"<kantega:hassection id="contentclass"> class="<kantega:getsection id="contentclass"/>"</kantega:hassection>>
        <div id="Navigation">
            <div id="Filteroptions">
                <a href="#" class="filteroption filter"><kantega:label key="aksess.filteroptions.options"/></a>
                <div class="filteroption">
                    <input type="checkbox" id="FilteroptionHideExpired">
                    <label for="FilteroptionHideExpired"><kantega:label key="aksess.filteroptions.hideexpired"/></label>
                </div>
            </div>
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

            <div id="SearchResults"></div>

            <kantega:getsection id="content"/>

            <c:choose>
                <c:when test="${currentContent != null}">
                    <div id="EditContentButtons" class="buttonBar">
                        <%@include file="fragments/editContentButtons.jsp"%>
                        <form name="myform" style="display:none" action="SaveContentPreview.action" method="post">
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
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="clearing"></div>
    </div>

    <%-- Including the context menus so they are available to jQyery. They are default hidden (by css) from view. --%>
    <%@include file="fragments/contextMenu-page.jsp"%>

    <%@include file="fragments/contextMenu-link.jsp"%>

</kantega:section>

<%@include file="commonLayout.jsp"%>