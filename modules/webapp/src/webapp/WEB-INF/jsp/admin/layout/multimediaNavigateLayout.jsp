<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/navigate.css">
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/multimedia.css">
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/navigate.jjs"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/ajaxupload.3.5.js"></script>
    <script type="text/javascript">

        var currentItemIdentifier = 0;

        $(document).ready(function(){
            debug("$(document).ready(): multimedia");
            bindMultimediaupdateEvents();
            triggerMultimediaupdateEvent();//Must be fired at startup in order to load the navigator
            bindToolButtons();
        });

        /**
         * Registers click event actions to each tool
         */
        function bindToolButtons() {
            bindMediaAjaxUpload();
        }
        /**
         * Contains the binding of all elements that are listening to the multimediaupdate event.
         * New global listeners to this event should be added here.
         */
        function bindMultimediaupdateEvents() {
            //Enables the navigator to listen to contentupdate events. Called every time a contentupdate event is fired.
            $("#Navigator").bind("multimediaupdate", function(e, itemIdentifier){
                debug("bindMultimediaupdateEvents(): "+e.type +" event received");
                if (!suppressNavigatorUpdate) {
                    updateNavigator(itemIdentifier, true);
                } else {
                    suppressNavigatorUpdate = false;
                    debug("bindMultimediaupdateEvents(): navigationUpdate suppressed");
                }
                updateMainPane(itemIdentifier, suppressNavigatorUpdate);
            });
        }

        /**
         * Changes the content of the main pane
         *
         * @param itemIdentifier
         * @param suppressNavigatorUpdate true/false.
         */
        function updateMainPane(itemIdentifier, suppressNavigatorUpdate) {
            debug("updateMainPane(): itemIdentifier: " + itemIdentifier + ", suppressNavigatorUpdate: " + suppressNavigatorUpdate);
            if (suppressNavigatorUpdate) {
                suppressNavigatorUpdate = true;
            }
            $("#MultimediaFolders").load(getViewFolderAction(), {itemIdentifier: itemIdentifier}, function(success){
                addMediaitemClickListeners();
            });
        }

        /**
         * Adds click listeners to the different media types and decides actions on these clicks.
         */
        function addMediaitemClickListeners() {
            $("#MultimediaFolders .folder").click(function(){
                debug("addMediaitemClickListeners(): folder click recieved");
                var idAttr = $(this).attr("id");
                currentItemIdentifier = idAttr.substring("Media".length, idAttr.length);
                triggerMultimediaupdateEvent();
            });

            /*
             $("#MultimediaFolders .media").click(function(){
             debug("addMediaitemClickListeners(): media click recieved");
             var idAttr = $(this).attr("id");
             currentItemIdentifier = idAttr.substring("Media".length, idAttr.length);
             window.location.href = 'EditMultimedia.action?id=' + currentItemIdentifier;
             });*/
        }

        /**
         * Sets the context (right click) menus in the navigator.
         */
        function setContextMenus() {
            setContextMenu("multimedia", ['paste']);
            setContextMenu("folder", ['paste']);
        }

        function getNavigatorAction() {
            return "<%=Aksess.getContextPath()%>/admin/multimedia/MultimediaNavigator.action";
        }

        function getViewFolderAction() {
            return "<%=Aksess.getContextPath()%>/admin/multimedia/ViewFolder.action";
        }

        function getItemIdentifierFromNavigatorHref(href) {
            return getQueryParam("itemIdentifier", href);
        }

        function onNavigatorTitleClick(elm) {
            var href = elm.attr("href");
            var itemIdentifier = getItemIdentifierFromNavigatorHref(href);

            currentItemIdentifier = itemIdentifier;
            triggerMultimediaupdateEvent();
        }
        
        function triggerMultimediaupdateEvent() {
            debug("triggerMultimediaupdateEvent(): mediaupdate event triggered");
            $.event.trigger("multimediaupdate",currentItemIdentifier);
        }

        function setLayoutSpecificSizes() {
            var paddingTop = $("#MultimediaFolders").css("padding-top");
            paddingTop = paddingTop.substring(0, paddingTop.indexOf("px"));
            var paddingBottom = $("#MultimediaFolders").css("padding-bottom");
            paddingBottom = paddingBottom.substring(0, paddingBottom.indexOf("px"));
            var multimediaFoldersHeight = $("#MainPane").height()-paddingTop-paddingBottom;

            $("#MultimediaFolders").css("height", multimediaFoldersHeight + "px");
        }

        function getNavigatorParams() {
            var params = new Object();
            return params;
        }

        function bindMediaAjaxUpload() {
            var button = $('#UploadButton');
            new AjaxUpload(button, {
                action: 'UploadMultimedia.action',
                name: 'file',
                onSubmit : function(file, ext){
                    this.setData({
                        'parentId': currentItemIdentifier,
                        'id' : -1
                    });
                    button.text('Uploading...');
                    this.disable();

                },
                onComplete: function(file, response){
                    button.text('New file');
                    // enable upload button
                    this.enable();
                    displayResults(response);
                }
            });
        }

        function displayResults(xml) {
            var files = xml.getElementsByTagName("file");

            if (files) {
                if (files.length == 1) {
                    var id = files[0].getAttribute("id");
                    location.href = "EditMultimedia.action?id=" + id;
                } else {
                    alert(files.length + " files uploaded");
                }
            } else {
                alert("Upload failed!");
            }
        }

        /**
         * Navigation layout specific implementation of the navigatorResizeOnResize-function.
         * See navigate.jjs
         */
        function navigatorResizeOnResize() {
            $.event.trigger("resize");
        }


    </script>

</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">

</kantega:section>

<kantega:section id="toolsMenu">
    <!-- TODO: Menyen her må lastes via ajax eller noe for at riktige knapper skal vises  -->
    <a href="#" class="button" id="UploadButton"><span class="upload"><kantega:label key="aksess.tools.upload"/></span></a>
</kantega:section>

<kantega:section id="body">
    <div id="Content" class="navigateMultimedia">
        <div id="Navigation">

            <div id="Navigator"></div>
            <div id="Framesplit">
                <div id="FramesplitDrag"></div>
            </div>
        </div>

        <div id="MainPane">
            <kantega:getsection id="content"/>            
        </div>
    </div>

</kantega:section>


<%@include file="commonLayout.jsp"%>