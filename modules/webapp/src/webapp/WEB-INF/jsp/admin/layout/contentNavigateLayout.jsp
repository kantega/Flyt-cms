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
    <c:if test="${currentContent != null}">
    <script type="text/javascript">
        var hasSubmitted = false;
        function saveContent(status) {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.status.value = status;
                document.myform.submit();
            }
        }
    </script>
    </c:if>
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
                <a href="#" class="filteroption filter">Filtreringsvalg</a>
                <div class="filteroption">
                    <input type="checkbox" id="FilteroptionHideExpired">
                    <label for="FilteroptionHideExpired">Skjul utløpte</label>
                </div>
            </div>
            <div id="Navigator"></div>
            <div id="Framesplit"></div>
        </div>
        <kantega:getsection id="content"/>
        <div class="clearing"></div>
    </div>

    <%-- Including the context menus so they are available to jQyery. They are default hidden (by css) from view. --%>
    <%@include file="fragments/contextMenu-page.jsp"%>

    <%@include file="fragments/contextMenu-link.jsp"%>

    <c:if test="${currentContent != null}">
        <form name="myform" style="display:none" action="SaveContentPreview.action" method="post">
            <input type="hidden" name="status" value="">
            <input type="hidden" name="action" value="">
            <input type="hidden" name="currentId" value="${currentContent.id}">
            <input type="hidden" name="isModified" id="IsModified" value="${currentContent.modified}">
        </form>
    </c:if>
</kantega:section>

<%@include file="commonLayout.jsp"%>