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
        $(document).ready(function(){
            setContentupdateTrigger();
        });

        /**
         * Attaches an onload listener to the contentframe and triggers a contentupdate event every time this onload event is fired,
         * i.e. on every page load in the iframe.
         *
         * Sends the current url as data with the event.
         *
         * Notifies the ContentStateHandler of the currently viewed page in order for this to be stored in the session.
         */
        function setContentupdateTrigger() {
            $("#Contentmain").load(function() {
                var currentContent = getCurrentLocation().href;
                debug("setContentupdateTrigger(): contentmain load event. currentContent: " + currentContent);
                ContentStateHandler.notifyContentUpdate(currentContent, function(success){
                    if(!success) {
                        debug("setContentupdateTrigger(): dwr ContentStateHandler.notifyContentUpdate() success");
                    } else {
                        debug("setContentupdateTrigger(): dwr ContentStateHandler.notifyContentUpdate() failed");
                        //TODO: Handle
                    }
                });
                currentUrl = currentContent;
                $.event.trigger("contentupdate",[currentContent]);
            });
        }


        /**
         * Changes the content of the contentmain iframe.
         * Such a change will trigger a contentupdate trigger if not suppressNavigatorUpdate is explicitly set to true
         *
         * @param id
         * @param suppressNavigatorUpdate true/false. A contentupdate event will be triggered unless set to true.
         */
        function updateMainPane(id, suppressNavigatorUpdate) {
            debug("updateMainPane(): id: " + id);
            if (suppressNavigatorUpdate) {
                suppressNavigatorUpdate = true;
            }
            var iframe = document.getElementById("Contentmain");
            if (iframe) {
                iframe.contentWindow.document.location.href = getContentUrlFromAssociationId(id);
            }
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
        Statistikk
    </div>

</kantega:section>

<%@include file="../../layout/contentNavigateLayout.jsp"%>
