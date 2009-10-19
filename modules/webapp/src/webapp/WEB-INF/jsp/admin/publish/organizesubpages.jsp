<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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


<kantega:section id="title">
    <kantega:label key="aksess.navigate.title"/>
</kantega:section>

<kantega:section id="content">
    <script type="text/javascript">
        var currentUrl = "${currentNavigateContent.url}";

        $(document).ready(function(){
            updateSubPageList(currentUrl);
            
        });

        function updateMainPane(id, suppressNavigatorUpdate) {
            debug("updateMainPane(): id: " + id);

            currentUrl = getContentUrlFromAssociationId(id);
            updateSubPageList(currentUrl);
        }

        function updateSubPageList(url) {
            $("#SubPages").load("<%=Aksess.getContextPath()%>/admin/publish/ListSubPages.action", {itemIdentifier: url}, function(success){
                debug("updateSubPageList(): response from ListSubPages.action received");
                $("#SubPages ul").sortable({
                    connectWith: '.associationCategory',
                    items: 'li:not(.menu)',
                    axis: 'y',
                    stop: function(e, ui){

                    }
                });

                $("#SubPages li.page").click(function(event){
                    var allSelected = $("#SubPages li.page.selected");
                    if (allSelected.size() == 0 || $(this).hasClass("selected") || event.ctrlKey) {
                        $(this).toggleClass('selected');
                    } else {
                        $(allSelected).each(function(){
                            $(this).removeClass("selected");
                        });
                        $(this).addClass("selected");
                    }
                });
            });
        }

        function setLayoutSpecificSizes() {
            var mainPaneHeight = $("#MainPane").height();
            var statusbarHeight = $("#MainPane .statusbar").height();
            var subPages = $("#SubPages");
            var subPagesPaddingTop = parseInt(subPages.css("paddingTop"));
            var subPagesPaddingBottom = parseInt(subPages.css("paddingBottom"));
            $("#SubPages").css("height", (mainPaneHeight-statusbarHeight-subPagesPaddingTop-subPagesPaddingBottom) + "px");
        }

    </script>

    <div id="MainPane">
        <div class="statusbar">
            <ul class="breadcrumbs">
                <li>Forside</li>
                <li>Lorem ipsum</li>
                <li>Dolor sit amet</li>
            </ul>
            <div class="supportMenu">
                <a href="#" class="brokenLink">Lenkebrudd</a>
                <a href="#" class="crossPublish">Krysspublisert</a>
                <a href="#" class="details">Details</a>
            </div>
        </div>
        <div id="SubPages"></div>
    </div>

</kantega:section>

<%@include file="../layout/contentNavigateLayout.jsp"%>
