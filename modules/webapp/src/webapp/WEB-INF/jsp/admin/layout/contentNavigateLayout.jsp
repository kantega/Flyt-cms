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
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/navigate.css">
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/navigate.jjs"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/content.jjs"></script>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
    <%@include file="fragments/publishModesMenu.jsp"%>
</kantega:section>

<kantega:section id="toolsMenu">
   <div class="buttonGroup">
        <a href="#" class="button"><span class="newSubpage"><kantega:label key="aksess.tools.newSubpage"/></span></a>
        <a href="#" class="button"><span class="edit"><kantega:label key="aksess.tools.edit"/></span></a>
        <a href="#" class="button"><span class="delete"><kantega:label key="aksess.tools.delete"/></span></a>
    </div>
    <div class="buttonGroup">
        <a href="#" class="button"><span class="cut"><kantega:label key="aksess.tools.cut"/></span></a>
        <a href="#" class="button"><span class="copy"><kantega:label key="aksess.tools.copy"/></span></a>
        <a href="#" class="button"><span class="paste"><kantega:label key="aksess.tools.paste"/></span></a>
    </div>
    <div class="buttonGroup">
        <a href="#" class="button"><span class="displayPeriod"><kantega:label key="aksess.tools.displayPeriod"/></span></a>
        <a href="#" class="button"><span class="privileges"><kantega:label key="aksess.tools.privileges"/></span></a>
    </div>
</kantega:section>



<kantega:section id="body">
    <div id="Content">
        <div id="Navigation">
            <div class="filteroptions">
                <a href="#" class="filteroption filter">Filtreringsvalg</a>
                <div class="filteroption">
                    <input type="checkbox" id="filteroptionHideExpired">
                    <label for="filteroptionHideExpired">Skjul utløpte</label>
                </div>
            </div>
            <div id="Navigator"></div>
            <div id="Framesplit">
                <div id="FramesplitDrag"></div>
            </div>
        </div>
        <kantega:getsection id="content"/>

        <div class="clearing"></div>
    </div>

    <%-- Including the context menus so they are available to jQyery. They are default hidden (by css) from view. --%>
    <%@include file="fragments/contextMenu-page.jsp"%>

    <%@include file="fragments/contextMenu-link.jsp"%>

</kantega:section>





<%@include file="commonLayout.jsp"%>