<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<c:set var="navigateActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.navigate.title"/>
</kantega:section>

<kantega:section id="head extras">
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
            var currentUrl = "";

            function checkUrlUpdate() {
                try {
                    var iframeUrl = openaksess.navigate.getCurrentLocation().href;
                    if (iframeUrl != currentUrl) {
                        currentUrl = iframeUrl;
                        openaksess.common.debug("setContentupdateTrigger(): contentmain url changed. currentUrl: " + currentUrl);
                        openaksess.content.triggerContentUpdateEvent(currentUrl);
                    }
                } catch (e) {
                    // External link
                }
            }

            setInterval(checkUrlUpdate, 200);
        }


    </script>
</kantega:section>

<kantega:section id="contentclass">navigateContent</kantega:section>

<kantega:section id="content">

    <div id="ContentmainContainer"><iframe name="contentmain" id="Contentmain" src="${currentUrl}" frameborder="0" height="100%" width="100%"></iframe></div>

</kantega:section>

<%@include file="../layout/contentNavigateLayout.jsp"%>
